package com.wyrix.browser.browser.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wyrix.browser.browser.engine.WyrixSessionHolder
import com.wyrix.browser.browser.model.TabModel
import com.wyrix.browser.browser.model.Workspace
import com.wyrix.browser.core.database.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mozilla.geckoview.GeckoSessionSettings
import java.net.URLEncoder
import java.util.UUID

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val db = WyrixDatabase.getInstance(application)
    private val dao = db.browserDao()

    private val sessionPool = mutableMapOf<String, WyrixSessionHolder>()

    private val _tabs = MutableStateFlow<List<TabModel>>(emptyList())
    val tabs: StateFlow<List<TabModel>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow<String?>(null)
    val activeTabId: StateFlow<String?> = _activeTabId.asStateFlow()

    private val _currentWorkspace = MutableStateFlow(Workspace.PERSONAL)
    val currentWorkspace: StateFlow<Workspace> = _currentWorkspace.asStateFlow()

    private val _shieldEnabled = MutableStateFlow(true)
    val shieldEnabled: StateFlow<Boolean> = _shieldEnabled.asStateFlow()

    private val _desktopMode = MutableStateFlow(false)
    val desktopMode: StateFlow<Boolean> = _desktopMode.asStateFlow()

    private val _selectedEngine = MutableStateFlow("Google")
    val selectedEngine: StateFlow<String> = _selectedEngine.asStateFlow()

    val bookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val history: Flow<List<HistoryEntity>> = dao.getAllHistory()
    val downloads: Flow<List<DownloadEntity>> = dao.getAllDownloads()

    val activeTab: StateFlow<TabModel?> = combine(_tabs, _activeTabId) { tabs, activeId ->
        tabs.find { it.id == activeId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            try {
                dao.getNormalTabs().firstOrNull()?.let { savedTabs ->
                    val validTabs = savedTabs.filter { it.url.isNotBlank() && it.url != "about:blank" }
                    if (validTabs.isNotEmpty()) {
                        validTabs.forEach { tabEntity ->
                            createTabInternal(
                                id = tabEntity.id,
                                url = tabEntity.url,
                                isPrivate = false,
                                workspace = try { Workspace.valueOf(tabEntity.workspaceId) } catch (e: Exception) { Workspace.PERSONAL }
                            )
                        }
                        _activeTabId.value = validTabs.first().id
                    } else {
                        createNewTab(url = "", isPrivate = false)
                    }
                } ?: run {
                    createNewTab(url = "", isPrivate = false)
                }
            } catch (e: Exception) {
                createNewTab(url = "", isPrivate = false)
            }
        }
    }

    fun setSelectedEngine(engine: String) {
        _selectedEngine.value = engine
    }

    fun getSession(tabId: String): WyrixSessionHolder? = sessionPool[tabId]

    fun createNewTab(url: String = "", isPrivate: Boolean = false) {
        val newId = UUID.randomUUID().toString()
        createTabInternal(newId, url, isPrivate, _currentWorkspace.value)
        _activeTabId.value = newId
    }

    private fun createTabInternal(id: String, url: String, isPrivate: Boolean, workspace: Workspace) {
        val sessionHolder = WyrixSessionHolder(
            tabId = id,
            isPrivate = isPrivate,
            context = getApplication(),
            onTitleChanged = { newTitle -> updateTabTitle(id, newTitle) },
            onUrlChanged = { newUrl -> handleUrlUpdate(id, newUrl, isPrivate) },
            onProgressChanged = { progress -> updateTabProgress(id, progress) },
            onSecurityChanged = { isSecure -> updateTabSecurity(id, isSecure) }
        )
        sessionPool[id] = sessionHolder
        if (url.isNotBlank() && url != "about:blank") {
            sessionHolder.loadUri(url)
        }

        val newTab = TabModel(
            id = id,
            title = if (url.isBlank()) "New Tab" else "Loading...",
            url = url,
            isPrivate = isPrivate,
            workspace = workspace
        )
        _tabs.update { it + newTab }

        if (!isPrivate && url.isNotBlank()) persistTab(newTab)
    }

    fun selectTab(tabId: String) { _activeTabId.value = tabId }

    fun closeTab(tabId: String) {
        sessionPool[tabId]?.close()
        sessionPool.remove(tabId)
        val remainingTabs = _tabs.value.filterNot { it.id == tabId }
        _tabs.value = remainingTabs
        if (_activeTabId.value == tabId) {
            _activeTabId.value = remainingTabs.lastOrNull()?.id
        }
        if (_tabs.value.isEmpty()) {
            createNewTab(url = "", isPrivate = false)
        }
        viewModelScope.launch { dao.deleteTabById(tabId) }
    }

    fun navigateTo(input: String, engine: String = _selectedEngine.value) {
        val currentId = _activeTabId.value ?: return
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return

        val formattedUrl = when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> {
                val query = URLEncoder.encode(trimmed, "UTF-8")
                when (engine.lowercase()) {
                    "google" -> "https://www.google.com/search?q=$query"
                    "brave" -> "https://search.brave.com/search?q=$query"
                    "bing" -> "https://www.bing.com/search?q=$query"
                    else -> "https://duckduckgo.com/?q=$query"
                }
            }
        }

        _tabs.update { list -> list.map { if (it.id == currentId) it.copy(url = formattedUrl, title = "Loading...") else it } }
        sessionPool[currentId]?.loadUri(formattedUrl)
    }

    fun goHome() {
        val currentId = _activeTabId.value ?: return
        _tabs.update { list -> list.map { if (it.id == currentId) it.copy(url = "") else it } }
    }

    fun reload() {
        val currentId = _activeTabId.value ?: return
        sessionPool[currentId]?.reload()
    }

    fun switchWorkspace(workspace: Workspace) { _currentWorkspace.value = workspace }
    fun toggleShield() { _shieldEnabled.update { !it } }

    fun toggleDesktopMode() {
        _desktopMode.update { !it }
        val currentId = _activeTabId.value ?: return
        val session = sessionPool[currentId]?.session ?: return
        session.settings.userAgentMode = if (_desktopMode.value) {
            GeckoSessionSettings.USER_AGENT_MODE_DESKTOP
        } else {
            GeckoSessionSettings.USER_AGENT_MODE_MOBILE
        }
        session.reload()
    }

    fun findInPage(text: String) {
        val currentId = _activeTabId.value ?: return
        sessionPool[currentId]?.session?.finder?.find(text, 0)
    }

    fun bookmarkCurrentPage() {
        val current = activeTab.value ?: return
        if (current.url.isBlank()) return
        viewModelScope.launch {
            dao.insertBookmark(BookmarkEntity(title = current.title, url = current.url))
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch { dao.deleteBookmark(bookmark) }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch { dao.deleteHistory(id) }
    }

    fun clearAllHistory() {
        viewModelScope.launch { dao.clearHistory() }
    }

    private fun handleUrlUpdate(tabId: String, url: String, isPrivate: Boolean) {
        if (url.isBlank() || url == "about:blank") return
        _tabs.update { list -> list.map { if (it.id == tabId) it.copy(url = url) else it } }
        if (!isPrivate) {
            viewModelScope.launch {
                dao.insertHistory(HistoryEntity(title = activeTab.value?.title ?: url, url = url))
                _tabs.value.find { it.id == tabId }?.let { persistTab(it) }
            }
        }
    }

    private fun updateTabTitle(tabId: String, title: String) {
        if (title.isBlank()) return
        _tabs.update { list -> list.map { if (it.id == tabId) it.copy(title = title) else it } }
    }

    private fun updateTabProgress(tabId: String, progress: Int) {
        _tabs.update { list -> list.map { if (it.id == tabId) it.copy(progress = progress) else it } }
    }

    private fun updateTabSecurity(tabId: String, isSecure: Boolean) {
        _tabs.update { list -> list.map { if (it.id == tabId) it.copy(isSecure = isSecure) else it } }
    }

    private fun persistTab(tab: TabModel) {
        if (tab.url.isBlank() || tab.url == "about:blank") return
        viewModelScope.launch {
            try {
                dao.insertOrUpdateTab(
                    TabEntity(
                        id = tab.id,
                        title = tab.title,
                        url = tab.url,
                        isPrivate = tab.isPrivate,
                        workspaceId = tab.workspace.name,
                        groupId = null,
                        position = 0
                    )
                )
            } catch (e: Exception) {}
        }
    }

    override fun onCleared() {
        super.onCleared()
        sessionPool.values.forEach { it.close() }
        sessionPool.clear()
    }
}
