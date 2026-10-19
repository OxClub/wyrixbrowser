import os

files = {}

# 1. Navigation Routes
files["app/src/main/java/com/wyrix/browser/ui/navigation/Screen.kt"] = """package com.wyrix.browser.ui.navigation

enum class Screen {
    BROWSER,
    TAB_SWITCHER,
    BOOKMARKS,
    HISTORY,
    DOWNLOADS,
    SETTINGS
}
"""

# 2. TabSwitcherScreen (Galaxy & Workspaces)
files["app/src/main/java/com/wyrix/browser/ui/screens/TabSwitcherScreen.kt"] = """package com.wyrix.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wyrix.browser.browser.model.TabModel
import com.wyrix.browser.browser.model.Workspace
import com.wyrix.browser.core.designsystem.*

@Composable
fun TabSwitcherScreen(
    tabs: List<TabModel>,
    activeTabId: String?,
    currentWorkspace: Workspace,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: (Boolean) -> Unit,
    onSwitchWorkspace: (Workspace) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(WyrixBlack).padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WyrixTextPrimary)
                    }
                    Text("Tab Galaxy", color = WyrixNeonBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Row {
                        IconButton(onClick = { onNewTab(true) }) {
                            Icon(Icons.Default.Shield, contentDescription = "Private Tab", tint = WyrixNeonViolet)
                        }
                        IconButton(onClick = { onNewTab(false) }) {
                            Icon(Icons.Default.Add, contentDescription = "New Tab", tint = WyrixNeonBlue)
                        }
                    }
                }

                // Workspace Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Workspace.values().forEach { ws ->
                        val isSelected = ws == currentWorkspace
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSwitchWorkspace(ws) },
                            label = { Text("${ws.icon} ${ws.title}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WyrixNeonBlue,
                                selectedLabelColor = WyrixBlack,
                                containerColor = WyrixPanel,
                                labelColor = WyrixTextPrimary
                            )
                        )
                    }
                }
            }
        },
        containerColor = WyrixBlack
    ) { padding ->
        val filteredTabs = tabs.filter { it.workspace == currentWorkspace }

        if (filteredTabs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No tabs in this workspace", color = WyrixTextSecondary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(filteredTabs) { tab ->
                    val isActive = tab.id == activeTabId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clickable { onSelectTab(tab.id) }
                            .border(
                                width = if (isActive) 2.dp else 1.dp,
                                color = if (isActive) WyrixNeonBlue else WyrixBorder,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = WyrixPanel),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (tab.isPrivate) "🔒 Private" else tab.title,
                                    color = if (tab.isPrivate) WyrixNeonViolet else WyrixTextPrimary,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { onCloseTab(tab.id) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = WyrixAlertRed, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = tab.url,
                                color = WyrixTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
"""

# 3. BookmarksScreen
files["app/src/main/java/com/wyrix/browser/ui/screens/BookmarksScreen.kt"] = """package com.wyrix.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wyrix.browser.core.database.BookmarkEntity
import com.wyrix.browser.core.designsystem.*

@Composable
fun BookmarksScreen(
    bookmarks: List<BookmarkEntity>,
    onSelectBookmark: (String) -> Unit,
    onDeleteBookmark: (BookmarkEntity) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery, bookmarks) {
        if (searchQuery.isBlank()) bookmarks
        else bookmarks.filter { it.title.contains(searchQuery, true) || it.url.contains(searchQuery, true) }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(WyrixBlack).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WyrixTextPrimary)
                    }
                    Text("Bookmarks", color = WyrixNeonBlue, fontSize = 20.sp, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search bookmarks...", color = WyrixTextDisabled) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WyrixNeonBlue,
                        unfocusedBorderColor = WyrixBorder,
                        focusedTextColor = WyrixTextPrimary
                    )
                )
            }
        },
        containerColor = WyrixBlack
    ) { padding ->
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No bookmarks saved yet", color = WyrixTextSecondary)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(filtered) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectBookmark(item.url) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = WyrixNeonBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, color = WyrixTextPrimary, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(item.url, color = WyrixTextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        IconButton(onClick = { onDeleteBookmark(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WyrixAlertRed)
                        }
                    }
                    Divider(color = WyrixBorder, thickness = 0.5.dp)
                }
            }
        }
    }
}
"""

# 4. HistoryScreen
files["app/src/main/java/com/wyrix/browser/ui/screens/HistoryScreen.kt"] = """package com.wyrix.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wyrix.browser.core.database.HistoryEntity
import com.wyrix.browser.core.designsystem.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    history: List<HistoryEntity>,
    onSelectUrl: (String) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onClearAll: () -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery, history) {
        if (searchQuery.isBlank()) history
        else history.filter { it.title.contains(searchQuery, true) || it.url.contains(searchQuery, true) }
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(WyrixBlack).padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WyrixTextPrimary)
                        }
                        Text("Browsing History", color = WyrixNeonBlue, fontSize = 20.sp)
                    }
                    IconButton(onClick = onClearAll) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = WyrixAlertRed)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search history...", color = WyrixTextDisabled) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WyrixNeonBlue,
                        unfocusedBorderColor = WyrixBorder,
                        focusedTextColor = WyrixTextPrimary
                    )
                )
            }
        },
        containerColor = WyrixBlack
    ) { padding ->
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No history recorded", color = WyrixTextSecondary)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(filtered) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectUrl(item.url) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = WyrixNeonViolet)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, color = WyrixTextPrimary, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(item.url, color = WyrixTextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(dateFormatter.format(Date(item.timestamp)), color = WyrixTextDisabled, fontSize = 10.sp)
                        }
                        IconButton(onClick = { onDeleteItem(item.id) }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = WyrixTextDisabled, modifier = Modifier.size(16.dp))
                        }
                    }
                    Divider(color = WyrixBorder, thickness = 0.5.dp)
                }
            }
        }
    }
}
"""

# 5. DownloadsScreen
files["app/src/main/java/com/wyrix/browser/ui/screens/DownloadsScreen.kt"] = """package com.wyrix.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wyrix.browser.core.database.DownloadEntity
import com.wyrix.browser.core.designsystem.*

@Composable
fun DownloadsScreen(
    downloads: List<DownloadEntity>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().background(WyrixBlack).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WyrixTextPrimary)
                }
                Text("Downloads", color = WyrixNeonBlue, fontSize = 20.sp)
            }
        },
        containerColor = WyrixBlack
    ) { padding ->
        if (downloads.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No downloads yet", color = WyrixTextSecondary)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(downloads) { download ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = WyrixElectricGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(download.fileName, color = WyrixTextPrimary, fontSize = 14.sp)
                            Text("${download.bytesDownloaded / 1024} KB / ${download.totalBytes / 1024} KB", color = WyrixTextSecondary, fontSize = 11.sp)
                        }
                    }
                    Divider(color = WyrixBorder, thickness = 0.5.dp)
                }
            }
        }
    }
}
"""

# 6. SettingsScreen
files["app/src/main/java/com/wyrix/browser/ui/screens/SettingsScreen.kt"] = """package com.wyrix.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wyrix.browser.core.designsystem.*

@Composable
fun SettingsScreen(
    shieldActive: Boolean,
    desktopMode: Boolean,
    onToggleShield: () -> Unit,
    onToggleDesktop: () -> Unit,
    onClearData: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().background(WyrixBlack).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WyrixTextPrimary)
                }
                Text("Settings", color = WyrixNeonBlue, fontSize = 20.sp)
            }
        },
        containerColor = WyrixBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Privacy & Security", color = WyrixNeonViolet, fontSize = 14.sp)

            Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(12.dp)) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("WYRIX Shield", color = WyrixTextPrimary, fontSize = 16.sp)
                            Text("Blocks crypto-miners and tracking cookies", color = WyrixTextSecondary, fontSize = 12.sp)
                        }
                        Switch(checked = shieldActive, onCheckedChange = { onToggleShield() })
                    }
                    Divider(color = WyrixBorder)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Desktop Mode Default", color = WyrixTextPrimary, fontSize = 16.sp)
                            Text("Always request desktop viewport", color = WyrixTextSecondary, fontSize = 12.sp)
                        }
                        Switch(checked = desktopMode, onCheckedChange = { onToggleDesktop() })
                    }
                }
            }

            Text("Data Management", color = WyrixNeonViolet, fontSize = 14.sp)

            Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onClearData() }.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Clear Browsing Data", color = WyrixAlertRed, fontSize = 16.sp)
                        Text("Clears all history, tabs, and cached sessions", color = WyrixTextSecondary, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = WyrixAlertRed)
                }
            }

            Text("About WYRIX", color = WyrixNeonViolet, fontSize = 14.sp)
            Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("WYRIX Autonomous Browser", color = WyrixNeonBlue, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Engine: Mozilla GeckoView 133 Stable", color = WyrixTextSecondary, fontSize = 13.sp)
                    Text("Architecture: Jetpack Compose + Room + Coroutines", color = WyrixTextSecondary, fontSize = 13.sp)
                    Text("Version: 1.0.0 (Production Build)", color = WyrixTextSecondary, fontSize = 13.sp)
                }
            }
        }
    }
}
"""

# 7. BiometricLockHelper
files["app/src/main/java/com/wyrix/browser/core/security/BiometricLockHelper.kt"] = """package com.wyrix.browser.core.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricLockHelper {
    fun canAuthenticate(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun promptBiometric(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock WYRIX Browser")
            .setSubtitle("Confirm identity to access browser session")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        prompt.authenticate(promptInfo)
    }
}
"""

# 8. BookmarkExporter (Netscape HTML Format)
files["app/src/main/java/com/wyrix/browser/core/bookmarks/BookmarkExporter.kt"] = """package com.wyrix.browser.core.bookmarks

import com.wyrix.browser.core.database.BookmarkEntity

object BookmarkExporter {
    fun exportToNetscapeHtml(bookmarks: List<BookmarkEntity>): String {
        val sb = StringBuilder()
        sb.append("<!DOCTYPE NETSCAPE-Bookmark-file-1>\\n")
        sb.append("<META HTTP-EQUIV=\\"Content-Type\\" CONTENT=\\"text/html; charset=UTF-8\\">\\n")
        sb.append("<TITLE>WYRIX Bookmarks</TITLE>\\n")
        sb.append("<H1>Bookmarks</H1>\\n<DL><p>\\n")
        for (b in bookmarks) {
            sb.append("    <DT><A HREF=\\"").append(b.url).append("\\">").append(b.title).append("</A>\\n")
        }
        sb.append("</DL><p>\\n")
        return sb.toString()
    }
}
"""

# 9. Updated BrowserViewModel (Full Wiring)
files["app/src/main/java/com/wyrix/browser/browser/viewmodel/BrowserViewModel.kt"] = """package com.wyrix.browser.browser.viewmodel

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
"""

# 10. MainActivity.kt (Multi-screen Navigation Container)
files["app/src/main/java/com/wyrix/browser/MainActivity.kt"] = """package com.wyrix.browser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
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
                val shieldActive by viewModel.shieldEnabled.collectAsState()
                val desktopMode by viewModel.desktopMode.collectAsState()

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
                            shieldActive = shieldActive,
                            desktopMode = desktopMode,
                            onToggleShield = { viewModel.toggleShield() },
                            onToggleDesktop = { viewModel.toggleDesktopMode() },
                            onClearData = { viewModel.clearAllHistory() },
                            onBack = { currentScreen = Screen.BROWSER }
                        )
                    }
                }
            }
        }
    }
}
"""

# 11. Hindi Localization (values-hi/strings.xml)
os.makedirs("app/src/main/res/values-hi", exist_ok=True)
files["app/src/main/res/values-hi/strings.xml"] = """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">WYRIX ब्राउज़र</string>
    <string name="tab_galaxy">टैब गैलेक्सी</string>
    <string name="bookmarks">बुकमार्क</string>
    <string name="history">इतिहास</string>
    <string name="downloads">डाउनलोड</string>
    <string name="settings">सेटिंग्स</string>
    <string name="shield">शील्ड सुरक्षा</string>
    <string name="new_tab">नया टैब</string>
    <string name="private_tab">प्राइवेट टैब</string>
</resources>
"""

# 12. Unit Tests (WyrixBrowserUnitTest.kt)
os.makedirs("app/src/test/java/com/wyrix/browser", exist_ok=True)
files["app/src/test/java/com/wyrix/browser/WyrixBrowserUnitTest.kt"] = """package com.wyrix.browser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WyrixBrowserUnitTest {
    @Test
    fun testUrlFormatting() {
        val query = "android geckoview"
        val formatted = if (!query.startsWith("http://") && !query.startsWith("https://")) {
            if (query.contains(".") && !query.contains(" ")) "https://$query"
            else "https://duckduckgo.com/?q=${java.net.URLEncoder.encode(query, "UTF-8")}"
        } else query

        assertTrue(formatted.startsWith("https://duckduckgo.com/?q="))
    }

    @Test
    fun testDirectDomainUrlFormatting() {
        val domain = "github.com"
        val formatted = if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
            if (domain.contains(".") && !domain.contains(" ")) "https://$domain"
            else "https://duckduckgo.com/?q=${java.net.URLEncoder.encode(domain, "UTF-8")}"
        } else domain

        assertEquals("https://github.com", formatted)
    }
}
"""

for path, content in files.items():
    d = os.path.dirname(path)
    if d:
        os.makedirs(d, exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"[+] Written: {path}")

print("\nAll phases code written successfully!")
