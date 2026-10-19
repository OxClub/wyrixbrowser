package com.wyrix.browser.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.wyrix.browser.browser.engine.GeckoViewHost
import com.wyrix.browser.browser.viewmodel.BrowserViewModel
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
    val shieldEnabled by viewModel.shieldEnabled.collectAsState()
    val desktopMode by viewModel.desktopMode.collectAsState()
    val selectedEngine by viewModel.selectedEngine.collectAsState()

    var showCommandCenter by remember { mutableStateOf(false) }
    var showControlCenter by remember { mutableStateOf(false) }

    val commands = listOf(
        WyrixCommand("New Tab", "Navigation") { viewModel.createNewTab() },
        WyrixCommand("New Private Tab", "Privacy") { viewModel.createNewTab(isPrivate = true) },
        WyrixCommand("Go to Home", "Navigation") { viewModel.goHome() },
        WyrixCommand("Reload Page", "Navigation") { viewModel.reload() },
        WyrixCommand("Toggle Wyrix Shield", "Security") { viewModel.toggleShield() },
        WyrixCommand("Toggle Desktop Mode", "Display") { viewModel.toggleDesktopMode() },
        WyrixCommand("Bookmark Current Page", "Bookmarks") { viewModel.bookmarkCurrentPage() },
        WyrixCommand("View Bookmarks", "Saved") { onOpenBookmarks() },
        WyrixCommand("View History", "Saved") { onOpenHistory() },
        WyrixCommand("Downloads", "Storage") { onOpenDownloads() },
        WyrixCommand("Browser Settings", "Config") { onOpenSettings() }
    )

    Scaffold(
        topBar = {
            BrowserToolbar(
                url = activeTab?.url.orEmpty(),
                progress = activeTab?.progress ?: 0,
                isSecure = activeTab?.isSecure ?: true,
                tabCount = allTabs.size,
                shieldActive = shieldEnabled,
                onNavigate = { viewModel.navigateTo(it) },
                onGoHome = { viewModel.goHome() },
                onOpenTabs = onOpenTabs,
                onOpenControlCenter = { showControlCenter = true },
                onToggleShield = { viewModel.toggleShield() },
                onOpenCommandCenter = { showCommandCenter = true }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            val sessionHolder = activeTab?.id?.let { viewModel.getSession(it) }
            val currentUrl = activeTab?.url.orEmpty()

            // 1. GeckoView is always alive in background so surface is warm and ready
            if (sessionHolder != null) {
                GeckoViewHost(
                    session = sessionHolder.session,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 2. If no URL is active, show the Futuristic HomeScreen HUD on top
            if (currentUrl.isBlank() || currentUrl == "about:blank") {
                HomeScreen(
                    currentWorkspace = currentWorkspace,
                    shieldActive = shieldEnabled,
                    selectedEngine = selectedEngine,
                    onSelectEngine = { viewModel.setSelectedEngine(it) },
                    onNavigate = { query, engine -> viewModel.navigateTo(query, engine) },
                    onSwitchWorkspace = { viewModel.switchWorkspace(it) }
                )
            }
        }
    }

    WyrixCommandCenter(
        isOpen = showCommandCenter,
        onDismiss = { showCommandCenter = false },
        commands = commands
    )

    WyrixControlCenterSheet(
        isOpen = showControlCenter,
        onDismiss = { showControlCenter = false },
        desktopMode = desktopMode,
        shieldActive = shieldEnabled,
        onToggleDesktop = { viewModel.toggleDesktopMode() },
        onToggleShield = { viewModel.toggleShield() },
        onOpenBookmarks = onOpenBookmarks,
        onOpenHistory = onOpenHistory,
        onOpenDownloads = onOpenDownloads,
        onOpenSettings = onOpenSettings
    )
}
