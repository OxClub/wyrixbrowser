package com.wyrix.browser.browser.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wyrix.browser.browser.engine.WyrixGeckoRuntime
import com.wyrix.browser.browser.engine.WyrixSessionHolder
import com.wyrix.browser.browser.model.TabModel
import com.wyrix.browser.browser.model.Workspace
import com.wyrix.browser.core.database.*
import com.wyrix.browser.core.preferences.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mozilla.geckoview.GeckoSessionSettings
import java.net.URLEncoder
import java.util.UUID

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val db = WyrixDatabase.getInstance(application)
    private val dao = db.browserDao()
    private val prefsRepo = WyrixPreferencesRepository(application)

    val preferences: StateFlow<WyrixPreferences> = prefsRepo.preferencesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, WyrixPreferences())

    private val sessionPool = mutableMapOf<String, WyrixSessionHolder>()

    private val _tabs = MutableStateFlow<List<TabModel>>(emptyList())
    val tabs: StateFlow<List<TabModel>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow<String?>(null)
    val activeTabId: StateFlow<String?> = _activeTabId.asStateFlow()

    private val _currentWorkspace = MutableStateFlow(Workspace.PERSONAL)
    val currentWorkspace: StateFlow<Workspace> = _currentWorkspace.asStateFlow()

    private val _desktopMode = MutableStateFlow(false)
    val desktopMode: StateFlow<Boolean> = _desktopMode.asStateFlow()

    val bookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val history: Flow<List<HistoryEntity>> = dao.getAllHistory()
    val downloads: Flow<List<DownloadEntity>> = dao.getAllDownloads()

    val activeTab: StateFlow<TabModel?> = combine(_tabs, _activeTabId) { tabs, activeId ->
        tabs.find { it.id == activeId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        // Observe and apply engine settings live
        viewModelScope.launch {
            preferences.collect { p ->
                WyrixGeckoRuntime.applyTrackingProtection(getApplication(), p.trackingLevel)
                WyrixGeckoRuntime.applyCookiePolicy(getApplication(), p.cookiePolicy)
                WyrixGeckoRuntime.applyJavaScript(getApplication(), p.javaScriptEnabled)
            }
        }

        viewModelScope.launch {
            try {
                if (preferences.value.restoreSessionOnStartup) {
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
                            return@launch
                        }
                    }
                }
                createNewTab(url = "", isPrivate = false)
            } catch (e: Exception) {
                createNewTab(url = "", isPrivate = false)
            }
        }
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

    fun navigateTo(input: String, overrideEngine: String? = null) {
        val currentId = _activeTabId.value ?: return
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return

        val engine = overrideEngine ?: preferences.value.searchEngine

        val formattedUrl = when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> {
                val query = URLEncoder.encode(trimmed, "UTF-8")
                when (engine.lowercase()) {
                    "google" -> "https://www.google.com/search?q=$query"
                    "brave" -> "https://search.brave.com/search?q=$query"
                    "bing" -> "https://www.bing.com/search?q=$query"
                    "startpage" -> "https://www.startpage.com/sp/search?query=$query"
                    "custom" -> {
                        val template = preferences.value.customSearchUrl
                        if (template.contains("%s")) template.replace("%s", query)
                        else "https://duckduckgo.com/?q=$query"
                    }
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

    fun bookmarkCurrentPage() {
        val current = activeTab.value ?: return
        if (current.url.isBlank()) return
        viewModelScope.launch {
            dao.insertBookmark(BookmarkEntity(title = current.title, url = current.url))
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) { viewModelScope.launch { dao.deleteBookmark(bookmark) } }
    fun deleteHistoryItem(id: Long) { viewModelScope.launch { dao.deleteHistory(id) } }
    fun clearAllHistory() { viewModelScope.launch { dao.clearHistory() } }

    fun clearCompleteEngineData() {
        viewModelScope.launch {
            dao.clearHistory()
            _tabs.value.forEach { sessionPool[it.id]?.close() }
            sessionPool.clear()
            _tabs.value = emptyList()
            createNewTab("", false)
            WyrixGeckoRuntime.clearAllWebStorage(getApplication()) {}
        }
    }

    // Settings Mutators
    fun setSearchEngine(eng: String) = viewModelScope.launch { prefsRepo.setSearchEngine(eng) }
    fun setCustomSearchUrl(url: String) = viewModelScope.launch { prefsRepo.setCustomSearchUrl(url) }
    fun setSearchSuggestions(enabled: Boolean) = viewModelScope.launch { prefsRepo.setSearchSuggestions(enabled) }
    fun setToolbarPosition(pos: ToolbarPosition) = viewModelScope.launch { prefsRepo.setToolbarPosition(pos) }
    fun setTrackingProtection(level: TrackingLevel) = viewModelScope.launch { prefsRepo.setTrackingLevel(level) }
    fun setHttpsOnly(enabled: Boolean) = viewModelScope.launch { prefsRepo.setHttpsOnly(enabled) }
    fun setDoNotTrack(enabled: Boolean) = viewModelScope.launch { prefsRepo.setDoNotTrack(enabled) }
    fun setClearOnExit(enabled: Boolean) = viewModelScope.launch { prefsRepo.setClearOnExit(enabled) }
    fun setBiometricLock(enabled: Boolean) = viewModelScope.launch { prefsRepo.setBiometricLock(enabled) }
    fun setAutofill(enabled: Boolean) = viewModelScope.launch { prefsRepo.setAutofill(enabled) }
    fun setTabViewStyle(style: TabViewStyle) = viewModelScope.launch { prefsRepo.setTabViewStyle(style) }
    fun setRestoreSession(enabled: Boolean) = viewModelScope.launch { prefsRepo.setRestoreSession(enabled) }
    fun setShowShortcuts(show: Boolean) = viewModelScope.launch { prefsRepo.setShowShortcuts(show) }
    fun setShowShieldStats(show: Boolean) = viewModelScope.launch { prefsRepo.setShowShieldStats(show) }
    fun setAppTheme(theme: AppTheme) = viewModelScope.launch { prefsRepo.setAppTheme(theme) }
    fun setAccentColor(acc: AccentColorChoice) = viewModelScope.launch { prefsRepo.setAccentColor(acc) }
    fun setTextZoom(percent: Int) = viewModelScope.launch { prefsRepo.setTextZoom(percent) }
    fun setForceZoom(enabled: Boolean) = viewModelScope.launch { prefsRepo.setForceZoom(enabled) }
    fun setJavaScript(enabled: Boolean) = viewModelScope.launch { prefsRepo.setJavaScript(enabled) }
    fun setCookiePolicy(policy: CookiePolicy) = viewModelScope.launch { prefsRepo.setCookiePolicy(policy) }
    fun setAppLanguage(lang: String) = viewModelScope.launch { prefsRepo.setAppLanguage(lang) }
    fun setDownloadWifiOnly(wifiOnly: Boolean) = viewModelScope.launch { prefsRepo.setDownloadWifiOnly(wifiOnly) }
    fun setAskDownloadLocation(ask: Boolean) = viewModelScope.launch { prefsRepo.setAskDownloadLocation(ask) }

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
