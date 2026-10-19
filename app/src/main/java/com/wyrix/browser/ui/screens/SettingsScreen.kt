package com.wyrix.browser.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wyrix.browser.browser.viewmodel.BrowserViewModel
import com.wyrix.browser.core.designsystem.*
import com.wyrix.browser.core.preferences.*
import com.wyrix.browser.ui.screens.settings.*

@Composable
fun SettingsScreen(
    viewModel: BrowserViewModel,
    onBack: () -> Unit
) {
    val prefs by viewModel.preferences.collectAsState()
    var selectedCategory by remember { mutableStateOf<SettingsCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showClearDataDialog by remember { mutableStateOf(false) }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear All Browsing Data?", color = WyrixAlertRed, fontWeight = FontWeight.Bold) },
            text = { Text("This permanently deletes all tabs, browsing history, cookies, and local website cache from the GeckoView engine.", color = WyrixTextPrimary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCompleteEngineData()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WyrixAlertRed)
                ) {
                    Text("Clear Everything", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = WyrixTextSecondary)
                }
            },
            containerColor = WyrixPanel
        )
    }

    if (selectedCategory != null) {
        when (selectedCategory!!) {
            SettingsCategory.SEARCH -> SearchSettingsScreen(
                prefs = prefs,
                onSetEngine = { viewModel.setSearchEngine(it) },
                onSetCustomUrl = { viewModel.setCustomSearchUrl(it) },
                onToggleSuggestions = { viewModel.setSearchSuggestions(it) },
                onBack = { selectedCategory = null }
            )
            SettingsCategory.TOOLBAR -> ToolbarPositionScreen(
                currentPos = prefs.toolbarPosition,
                onSetPos = { viewModel.setToolbarPosition(it) },
                onBack = { selectedCategory = null }
            )
            SettingsCategory.PRIVACY -> PrivacySettingsScreen(
                prefs = prefs,
                onSetTracking = { viewModel.setTrackingProtection(it) },
                onToggleHttpsOnly = { viewModel.setHttpsOnly(it) },
                onToggleDoNotTrack = { viewModel.setDoNotTrack(it) },
                onToggleClearOnExit = { viewModel.setClearOnExit(it) },
                onToggleBiometric = { viewModel.setBiometricLock(it) },
                onClearDataRequested = { showClearDataDialog = true },
                onBack = { selectedCategory = null }
            )
            SettingsCategory.SAFETY_CHECK -> SafetyCheckScreen(prefs = prefs, onBack = { selectedCategory = null })
            SettingsCategory.DEFAULT_BROWSER -> DefaultBrowserScreen(onBack = { selectedCategory = null })
            SettingsCategory.AUTOFILL -> AutofillSettingsScreen(
                autofillEnabled = prefs.autofillEnabled,
                onToggleAutofill = { viewModel.setAutofill(it) },
                onBack = { selectedCategory = null }
            )
            SettingsCategory.TABS -> TabsSettingsScreen(
                prefs = prefs,
                onSetTabStyle = { viewModel.setTabViewStyle(it) },
                onToggleRestore = { viewModel.setRestoreSession(it) },
                onBack = { selectedCategory = null }
            )
            SettingsCategory.HOMEPAGE -> HomepageSettingsScreen(
                prefs = prefs,
                onToggleShortcuts = { viewModel.setShowShortcuts(it) },
                onToggleShieldStats = { viewModel.setShowShieldStats(it) },
                onBack = { selectedCategory = null }
            )
            SettingsCategory.NOTIFICATIONS -> NotificationsSettingsScreen(onBack = { selectedCategory = null })
            SettingsCategory.APPEARANCE -> AppearanceSettingsScreen(
                prefs = prefs,
                onSetTheme = { viewModel.setAppTheme(it) },
                onSetAccent = { viewModel.setAccentColor(it) },
                onBack = { selectedCategory = null }
            )
            SettingsCategory.ACCESSIBILITY -> AccessibilitySettingsScreen(
                prefs = prefs,
                onSetZoom = { viewModel.setTextZoom(it) },
                onToggleForceZoom = { viewModel.setForceZoom(it) },
                onBack = { selectedCategory = null }
            )
            SettingsCategory.SITE_PERMISSIONS -> SitePermissionsScreen(
                prefs = prefs,
                onToggleJs = { viewModel.setJavaScript(it) },
                onSetCookiePolicy = { viewModel.setCookiePolicy(it) },
                onBack = { selectedCategory = null }
            )
            SettingsCategory.LANGUAGES -> LanguagesSettingsScreen(
                currentLang = prefs.appLanguage,
                onSetLang = { viewModel.setAppLanguage(it) },
                onBack = { selectedCategory = null }
            )
            SettingsCategory.DOWNLOADS -> DownloadsSettingsScreen(
                prefs = prefs,
                onToggleWifiOnly = { viewModel.setDownloadWifiOnly(it) },
                onToggleAskLocation = { viewModel.setAskDownloadLocation(it) },
                onBack = { selectedCategory = null }
            )
            SettingsCategory.ABOUT -> AboutSettingsScreen(onBack = { selectedCategory = null })
        }
        return
    }

    val filteredCategories = remember(searchQuery) {
        if (searchQuery.isBlank()) SettingsCategory.values().toList()
        else SettingsCategory.values().filter {
            it.title.contains(searchQuery, ignoreCase = true) || it.subtitle.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(WyrixBlack).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WyrixTextPrimary)
                    }
                    Text("Settings", color = WyrixNeonBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search all 16 settings...", color = WyrixTextDisabled, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = WyrixNeonBlue) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = WyrixTextDisabled)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WyrixPanel,
                        unfocusedContainerColor = WyrixPanel,
                        focusedBorderColor = WyrixNeonBlue,
                        unfocusedBorderColor = WyrixBorder,
                        focusedTextColor = WyrixTextPrimary
                    ),
                    singleLine = true
                )
            }
        },
        containerColor = WyrixBlack
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredCategories) { cat ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedCategory = cat },
                    colors = CardDefaults.cardColors(containerColor = WyrixPanel),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = WyrixDarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, WyrixBorder),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = when(cat) {
                                        SettingsCategory.SEARCH -> Icons.Default.Search
                                        SettingsCategory.TOOLBAR -> Icons.Default.ViewStream
                                        SettingsCategory.PRIVACY -> Icons.Default.Shield
                                        SettingsCategory.SAFETY_CHECK -> Icons.Default.CheckCircle
                                        SettingsCategory.DEFAULT_BROWSER -> Icons.Default.Launch
                                        SettingsCategory.AUTOFILL -> Icons.Default.Password
                                        SettingsCategory.TABS -> Icons.Default.Tab
                                        SettingsCategory.HOMEPAGE -> Icons.Default.Home
                                        SettingsCategory.NOTIFICATIONS -> Icons.Default.Notifications
                                        SettingsCategory.APPEARANCE -> Icons.Default.Palette
                                        SettingsCategory.ACCESSIBILITY -> Icons.Default.Accessibility
                                        SettingsCategory.SITE_PERMISSIONS -> Icons.Default.Security
                                        SettingsCategory.LANGUAGES -> Icons.Default.Translate
                                        SettingsCategory.DOWNLOADS -> Icons.Default.Download
                                        SettingsCategory.ABOUT -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = WyrixNeonBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cat.title, color = WyrixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text(cat.subtitle, color = WyrixTextSecondary, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = WyrixTextDisabled)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
