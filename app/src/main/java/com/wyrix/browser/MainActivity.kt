package com.wyrix.browser

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.fragment.app.FragmentActivity
import com.wyrix.browser.browser.viewmodel.BrowserViewModel
import com.wyrix.browser.core.designsystem.WyrixTheme
import com.wyrix.browser.ui.navigation.Screen
import com.wyrix.browser.ui.screens.*

class MainActivity : FragmentActivity() {
    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.dataString?.let { incomingUrl ->
            viewModel.createNewTab(url = incomingUrl)
        }

        setContent {
            WyrixTheme {
                var currentScreen by remember { mutableStateOf(Screen.BROWSER) }

                val tabs by viewModel.tabs.collectAsState()
                val activeTabId by viewModel.activeTabId.collectAsState()
                val currentWorkspace by viewModel.currentWorkspace.collectAsState()

                val bookmarks by viewModel.bookmarks.collectAsState(initial = emptyList())
                val history by viewModel.history.collectAsState(initial = emptyList())
                val downloads by viewModel.downloads.collectAsState(initial = emptyList())

                BackHandler(enabled = currentScreen != Screen.BROWSER) {
                    currentScreen = Screen.BROWSER
                }

                when (currentScreen) {
                    Screen.BROWSER -> {
                        BrowserScreen(
                            viewModel = viewModel,
                            onOpenTabs = { currentScreen = Screen.TAB_SWITCHER },
                            onOpenBookmarks = { currentScreen = Screen.BOOKMARKS },
                            onOpenHistory = { currentScreen = Screen.HISTORY },
                            onOpenDownloads = { currentScreen = Screen.DOWNLOADS },
                            onOpenSettings = { currentScreen = Screen.SETTINGS }
                        )
                    }
                    Screen.TAB_SWITCHER -> {
                        TabSwitcherScreen(
                            tabs = tabs,
                            activeTabId = activeTabId,
                            currentWorkspace = currentWorkspace,
                            onSelectTab = {
                                viewModel.selectTab(it)
                                currentScreen = Screen.BROWSER
                            },
                            onCloseTab = { viewModel.closeTab(it) },
                            onNewTab = { isPrivate ->
                                viewModel.createNewTab(isPrivate = isPrivate)
                                currentScreen = Screen.BROWSER
                            },
                            onSwitchWorkspace = { viewModel.switchWorkspace(it) },
                            onBack = { currentScreen = Screen.BROWSER }
                        )
                    }
                    Screen.BOOKMARKS -> {
                        BookmarksScreen(
                            bookmarks = bookmarks,
                            onSelectBookmark = {
                                viewModel.navigateTo(it)
                                currentScreen = Screen.BROWSER
                            },
                            onDeleteBookmark = { viewModel.deleteBookmark(it) },
                            onBack = { currentScreen = Screen.BROWSER }
                        )
                    }
                    Screen.HISTORY -> {
                        HistoryScreen(
                            history = history,
                            onSelectUrl = {
                                viewModel.navigateTo(it)
                                currentScreen = Screen.BROWSER
                            },
                            onDeleteItem = { viewModel.deleteHistoryItem(it) },
                            onClearAll = { viewModel.clearAllHistory() },
                            onBack = { currentScreen = Screen.BROWSER }
                        )
                    }
                    Screen.DOWNLOADS -> {
                        DownloadsScreen(
                            downloads = downloads,
                            onBack = { currentScreen = Screen.BROWSER }
                        )
                    }
                    Screen.SETTINGS -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.BROWSER }
                        )
                    }
                }
            }
        }
    }
}
