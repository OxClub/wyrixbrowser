package com.wyrix.browser.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.wyrix.browser.browser.engine.GeckoViewHost
import com.wyrix.browser.browser.viewmodel.BrowserViewModel
import com.wyrix.browser.ui.components.BrowserToolbar
import com.wyrix.browser.ui.components.WyrixCommand
import com.wyrix.browser.ui.components.WyrixCommandCenter

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
    val shieldEnabled by viewModel.shieldEnabled.collectAsState()

    var showCommandCenter by remember { mutableStateOf(false) }

    val commands = listOf(
        WyrixCommand("New Tab", "Navigation") { viewModel.createNewTab() },
        WyrixCommand("New Private Tab", "Privacy") { viewModel.createNewTab(isPrivate = true) },
        WyrixCommand("Toggle Wyrix Shield", "Security") { viewModel.toggleShield() },
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
                onOpenTabs = onOpenTabs,
                onOpenMenu = onOpenSettings,
                onToggleShield = { viewModel.toggleShield() },
                onOpenCommandCenter = { showCommandCenter = true }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            val sessionHolder = activeTab?.id?.let { viewModel.getSession(it) }
            if (sessionHolder != null) {
                GeckoViewHost(
                    session = sessionHolder.session,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                HomeScreen(onNavigate = { viewModel.navigateTo(it) })
            }
        }
    }

    WyrixCommandCenter(
        isOpen = showCommandCenter,
        onDismiss = { showCommandCenter = false },
        commands = commands
    )
}
