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
                    if (savedTabs.isNotEmpty()) {
                        savedTabs.forEach { tabEntity ->
                            createTabInternal(
                                id = tabEntity.id,
                                url = tabEntity.url,
                                isPrivate = false,
                                workspace = try { Workspace.valueOf(tabEntity.workspaceId) } catch (e: Exception) { Workspace.PERSONAL }
                            )
                        }
                        _activeTabId.value = savedTabs.first().id
                    } else {
                        createNewTab("https://start.duckduckgo.com", isPrivate = false)
                    }
                } ?: run {
                    createNewTab("https://start.duckduckgo.com", isPrivate = false)
                }
            } catch (e: Exception) {
                createNewTab("https://start.duckduckgo.com", isPrivate = false)
            }
        }
    }

    fun getSession(tabId: String): WyrixSessionHolder? = sessionPool[tabId]

    fun createNewTab(url: String = "https://start.duckduckgo.com", isPrivate: Boolean = false) {
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
        sessionHolder.loadUri(url)

        val newTab = TabModel(
            id = id,
            title = "Loading...",
            url = url,
            isPrivate = isPrivate,
            workspace = workspace
        )
        _tabs.update { it + newTab }

        if (!isPrivate) persistTab(newTab)
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
        viewModelScope.launch { dao.deleteTabById(tabId) }
    }

    fun navigateTo(url: String) {
        val currentId = _activeTabId.value ?: return
        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            if (url.contains(".") && !url.contains(" ")) "https://$url"
            else "https://duckduckgo.com/?q=${java.net.URLEncoder.encode(url, "UTF-8")}"
        } else url
        sessionPool[currentId]?.loadUri(formattedUrl)
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
        _tabs.update { list -> list.map { if (it.id == tabId) it.copy(url = url) else it } }
        if (!isPrivate) {
            viewModelScope.launch {
                dao.insertHistory(HistoryEntity(title = activeTab.value?.title ?: url, url = url))
                _tabs.value.find { it.id == tabId }?.let { persistTab(it) }
            }
        }
    }

    private fun updateTabTitle(tabId: String, title: String) {
        _tabs.update { list -> list.map { if (it.id == tabId) it.copy(title = title) else it } }
    }

    private fun updateTabProgress(tabId: String, progress: Int) {
        _tabs.update { list -> list.map { if (it.id == tabId) it.copy(progress = progress) else it } }
    }

    private fun updateTabSecurity(tabId: String, isSecure: Boolean) {
        _tabs.update { list -> list.map { if (it.id == tabId) it.copy(isSecure = isSecure) else it } }
    }

    private fun persistTab(tab: TabModel) {
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
