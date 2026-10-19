package com.wyrix.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.wyrix.browser.browser.viewmodel.BrowserViewModel
import com.wyrix.browser.core.designsystem.WyrixTheme
import com.wyrix.browser.ui.screens.BrowserScreen

class MainActivity : ComponentActivity() {
    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.dataString?.let { incomingUrl ->
            viewModel.createNewTab(url = incomingUrl)
        }
        setContent {
            WyrixTheme {
                BrowserScreen(
                    viewModel = viewModel,
                    onOpenTabs = {},
                    onOpenBookmarks = {},
                    onOpenHistory = {},
                    onOpenDownloads = {},
                    onOpenSettings = {}
                )
            }
        }
    }
}
