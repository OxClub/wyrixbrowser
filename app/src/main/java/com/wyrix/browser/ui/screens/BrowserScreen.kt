package com.wyrix.browser.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.wyrix.browser.browser.engine.GeckoViewHost
import com.wyrix.browser.browser.viewmodel.BrowserViewModel
import com.wyrix.browser.core.preferences.ToolbarPosition
import com.wyrix.browser.core.preferences.TrackingLevel
import com.wyrix.browser.ui.components.*

@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    onOpenTabs: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val activeTab by viewModel.activeTab.collectAsState()
    val allTabs by viewModel.tabs.collectAsState()
    val currentWorkspace by viewModel.currentWorkspace.collectAsState()
    val desktopMode by viewModel.desktopMode.collectAsState()
    val prefs by viewModel.preferences.collectAsState()

    var showCommandCenter by remember { mutableStateOf(false) }
    var showControlCenter by remember { mutableStateOf(false) }

    val toolbarContent: @Composable () -> Unit = {
        BrowserToolbar(
            url = activeTab?.url.orEmpty(),
            progress = activeTab?.progress ?: 0,
            isSecure = activeTab?.isSecure ?: true,
            tabCount = allTabs.size,
            shieldActive = prefs.trackingLevel != TrackingLevel.OFF,
            onNavigate = { viewModel.navigateTo(it) },
            onGoHome = { viewModel.goHome() },
            onOpenTabs = onOpenTabs,
            onOpenControlCenter = { showControlCenter = true },
            onToggleShield = {
                viewModel.setTrackingProtection(
                    if (prefs.trackingLevel == TrackingLevel.OFF) TrackingLevel.STRICT else TrackingLevel.OFF
                )
            },
            onOpenCommandCenter = { showCommandCenter = true }
        )
    }

    Scaffold(
        topBar = {
            if (prefs.toolbarPosition == ToolbarPosition.TOP) {
                toolbarContent()
            }
        },
        bottomBar = {
            if (prefs.toolbarPosition == ToolbarPosition.BOTTOM) {
                toolbarContent()
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            val sessionHolder = activeTab?.id?.let { viewModel.getSession(it) }
            val currentUrl = activeTab?.url.orEmpty()

            if (currentUrl.isNotBlank() && currentUrl != "about:blank") {
                if (sessionHolder != null) {
                    GeckoViewHost(
                        session = sessionHolder.session,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                HomeScreen(
                    currentWorkspace = currentWorkspace,
                    shieldActive = prefs.trackingLevel != TrackingLevel.OFF,
                    selectedEngine = prefs.searchEngine,
                    onSelectEngine = { viewModel.setSearchEngine(it) },
                    onNavigate = { query, engine -> viewModel.navigateTo(query, engine) },
                    onSwitchWorkspace = { viewModel.switchWorkspace(it) }
                )
            }
        }
    }

    WyrixCommandCenter(
        isOpen = showCommandCenter,
        onDismiss = { showCommandCenter = false },
        commands = listOf(
            WyrixCommand("New Tab", "Navigation") { viewModel.createNewTab() },
            WyrixCommand("New Private Tab", "Privacy") { viewModel.createNewTab(isPrivate = true) },
            WyrixCommand("Go to Home", "Navigation") { viewModel.goHome() },
            WyrixCommand("Reload Page", "Navigation") { viewModel.reload() },
            WyrixCommand("Toggle Desktop Mode", "Display") { viewModel.toggleDesktopMode() },
            WyrixCommand("Bookmark Page", "Bookmarks") { viewModel.bookmarkCurrentPage() },
            WyrixCommand("Open Settings", "Config") { onOpenSettings() }
        )
    )

    WyrixControlCenterSheet(
        isOpen = showControlCenter,
        onDismiss = { showControlCenter = false },
        desktopMode = desktopMode,
        shieldActive = prefs.trackingLevel != TrackingLevel.OFF,
        onToggleDesktop = { viewModel.toggleDesktopMode() },
        onToggleShield = {
            viewModel.setTrackingProtection(
                if (prefs.trackingLevel == TrackingLevel.OFF) TrackingLevel.STRICT else TrackingLevel.OFF
            )
        },
        onOpenBookmarks = onOpenBookmarks,
        onOpenHistory = onOpenHistory,
        onOpenDownloads = onOpenDownloads,
        onOpenSettings = onOpenSettings
    )
}
