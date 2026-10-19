package com.wyrix.browser.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wyrix.browser.core.designsystem.*
import com.wyrix.browser.core.preferences.*

@Composable
fun SettingsSubScreenScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WyrixBlack)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WyrixTextPrimary)
                }
                Text(title, color = WyrixNeonBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

// 1. Search Settings
@Composable
fun SearchSettingsScreen(
    prefs: WyrixPreferences,
    onSetEngine: (String) -> Unit,
    onSetCustomUrl: (String) -> Unit,
    onToggleSuggestions: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val engines = listOf("Google", "DuckDuckGo", "Brave", "Bing", "Startpage", "Custom")
    var customUrlText by remember(prefs.customSearchUrl) { mutableStateOf(prefs.customSearchUrl) }

    SettingsSubScreenScaffold("Search Settings", onBack) {
        Text("DEFAULT SEARCH ENGINE", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column {
                engines.forEachIndexed { idx, eng ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSetEngine(eng) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(eng, color = WyrixTextPrimary, fontSize = 15.sp)
                        RadioButton(
                            selected = prefs.searchEngine.equals(eng, ignoreCase = true),
                            onClick = { onSetEngine(eng) },
                            colors = RadioButtonDefaults.colors(selectedColor = WyrixNeonBlue)
                        )
                    }
                    if (idx < engines.lastIndex) Divider(color = WyrixBorder)
                }
            }
        }

        if (prefs.searchEngine.equals("Custom", ignoreCase = true)) {
            Text("CUSTOM QUERY TEMPLATE", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = customUrlText,
                onValueChange = {
                    customUrlText = it
                    onSetCustomUrl(it)
                },
                placeholder = { Text("https://example.com/search?q=%s", color = WyrixTextDisabled) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WyrixNeonBlue,
                    unfocusedBorderColor = WyrixBorder,
                    focusedTextColor = WyrixTextPrimary
                )
            )
        }

        Text("SEARCH SUGGESTIONS", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Show Search Suggestions", color = WyrixTextPrimary, fontSize = 15.sp)
                    Text("Display suggestions from history and local bookmarks", color = WyrixTextSecondary, fontSize = 12.sp)
                }
                Switch(checked = prefs.searchSuggestionsEnabled, onCheckedChange = onToggleSuggestions)
            }
        }
    }
}

// 2. Toolbar Position
@Composable
fun ToolbarPositionScreen(
    currentPos: ToolbarPosition,
    onSetPos: (ToolbarPosition) -> Unit,
    onBack: () -> Unit
) {
    SettingsSubScreenScaffold("Address Bar Position", onBack) {
        Text("TOOLBAR PLACEMENT", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onSetPos(ToolbarPosition.TOP) }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Top of Screen", color = WyrixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("Traditional browser layout", color = WyrixTextSecondary, fontSize = 12.sp)
                    }
                    RadioButton(
                        selected = currentPos == ToolbarPosition.TOP,
                        onClick = { onSetPos(ToolbarPosition.TOP) },
                        colors = RadioButtonDefaults.colors(selectedColor = WyrixNeonBlue)
                    )
                }
                Divider(color = WyrixBorder)
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onSetPos(ToolbarPosition.BOTTOM) }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Bottom of Screen (Thumb Reachable)", color = WyrixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("Modern one-handed mobile navigation", color = WyrixTextSecondary, fontSize = 12.sp)
                    }
                    RadioButton(
                        selected = currentPos == ToolbarPosition.BOTTOM,
                        onClick = { onSetPos(ToolbarPosition.BOTTOM) },
                        colors = RadioButtonDefaults.colors(selectedColor = WyrixNeonBlue)
                    )
                }
            }
        }
    }
}

// 3. Privacy & Security
@Composable
fun PrivacySettingsScreen(
    prefs: WyrixPreferences,
    onSetTracking: (TrackingLevel) -> Unit,
    onToggleHttpsOnly: (Boolean) -> Unit,
    onToggleDoNotTrack: (Boolean) -> Unit,
    onToggleClearOnExit: (Boolean) -> Unit,
    onToggleBiometric: (Boolean) -> Unit,
    onClearDataRequested: () -> Unit,
    onBack: () -> Unit
) {
    SettingsSubScreenScaffold("Privacy and Security", onBack) {
        Text("WYRIX SHIELD ANTI-TRACKING", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column {
                TrackingLevel.values().forEachIndexed { idx, level ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onSetTracking(level) }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(level.name, color = WyrixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                if (level == TrackingLevel.STRICT) "Blocks social, cryptominers, fingerprinters & cookies"
                                else if (level == TrackingLevel.STANDARD) "Balanced protection with fewer broken websites"
                                else "All tracking protection disabled",
                                color = WyrixTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        RadioButton(
                            selected = prefs.trackingLevel == level,
                            onClick = { onSetTracking(level) },
                            colors = RadioButtonDefaults.colors(selectedColor = WyrixNeonBlue)
                        )
                    }
                    if (idx < TrackingLevel.values().lastIndex) Divider(color = WyrixBorder)
                }
            }
        }

        Text("ADVANCED SECURITY", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("HTTPS-Only Mode", color = WyrixTextPrimary, fontSize = 14.sp)
                        Text("Upgrade every connection to secure HTTPS", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                    Switch(checked = prefs.httpsOnlyMode, onCheckedChange = onToggleHttpsOnly)
                }
                Divider(color = WyrixBorder)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Send 'Do Not Track' Request", color = WyrixTextPrimary, fontSize = 14.sp)
                        Text("Inform websites that you do not want to be tracked", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                    Switch(checked = prefs.doNotTrack, onCheckedChange = onToggleDoNotTrack)
                }
                Divider(color = WyrixBorder)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Biometric Browser Lock", color = WyrixTextPrimary, fontSize = 14.sp)
                        Text("Require fingerprint or face authentication on resume", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                    Switch(checked = prefs.biometricLockEnabled, onCheckedChange = onToggleBiometric)
                }
            }
        }

        Text("DATA DELETION", color = WyrixAlertRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onClearDataRequested() },
            colors = CardDefaults.cardColors(containerColor = WyrixPanel),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = WyrixAlertRed)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Clear Browsing Data Now", color = WyrixAlertRed, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Clear GeckoView engine cache, cookies, history and tabs", color = WyrixTextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

// 4. Safety Check Screen
@Composable
fun SafetyCheckScreen(
    prefs: WyrixPreferences,
    onBack: () -> Unit
) {
    SettingsSubScreenScaffold("Safety Check", onBack) {
        Text("SYSTEM SECURITY AUDIT", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (prefs.trackingLevel == TrackingLevel.STRICT) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (prefs.trackingLevel == TrackingLevel.STRICT) WyrixElectricGreen else WyrixAlertRed
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("WYRIX Shield Tracking Defense", color = WyrixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(if (prefs.trackingLevel == TrackingLevel.STRICT) "Strict Mode Active • 0 Leakage" else "Protection downgraded", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                }
                Divider(color = WyrixBorder)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (prefs.httpsOnlyMode) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (prefs.httpsOnlyMode) WyrixElectricGreen else WyrixAlertRed
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Transport Layer Security (HTTPS)", color = WyrixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(if (prefs.httpsOnlyMode) "Enforcing TLS 1.3 / HTTPS-Only" else "Insecure connections allowed", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                }
                Divider(color = WyrixBorder)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (prefs.biometricLockEnabled) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (prefs.biometricLockEnabled) WyrixElectricGreen else WyrixNeonBlue
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("App Lock Protection", color = WyrixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(if (prefs.biometricLockEnabled) "Biometric verification enrolled" else "App lock not enabled", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// 5. Default Browser Launcher
@Composable
fun DefaultBrowserScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    SettingsSubScreenScaffold("Default Browser", onBack) {
        Text("SET WYRIX AS DEFAULT", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Open Links Automatically in WYRIX", color = WyrixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Make WYRIX your default Android web browser for instant shield protection on every external link.", color = WyrixTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                val roleManager = context.getSystemService(android.app.role.RoleManager::class.java)
                                val intent = roleManager.createRequestRoleIntent(android.app.role.RoleManager.ROLE_BROWSER)
                                context.startActivity(intent)
                            } else {
                                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                                context.startActivity(intent)
                            }
                        } catch (e: Exception) {
                            val intent = Intent(Settings.ACTION_SETTINGS)
                            context.startActivity(intent)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WyrixNeonBlue)
                ) {
                    Text("Open Android Default Apps", color = WyrixBlack, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 6. Autofill & Passwords Screen
@Composable
fun AutofillSettingsScreen(
    autofillEnabled: Boolean,
    onToggleAutofill: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    SettingsSubScreenScaffold("Autofill and Passwords", onBack) {
        Text("CREDENTIAL MANAGEMENT", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Enable Autofill", color = WyrixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Use system credential managers to fill forms and logins securely", color = WyrixTextSecondary, fontSize = 12.sp)
                    }
                    Switch(checked = autofillEnabled, onCheckedChange = onToggleAutofill)
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            context.startActivity(Intent(Settings.ACTION_SETTINGS))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Configure System Autofill Providers", color = WyrixNeonBlue)
                }
            }
        }
    }
}

// 7. Tabs and Tab Groups Screen
@Composable
fun TabsSettingsScreen(
    prefs: WyrixPreferences,
    onSetTabStyle: (TabViewStyle) -> Unit,
    onToggleRestore: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    SettingsSubScreenScaffold("Tabs and Tab Groups", onBack) {
        Text("TAB SWITCHER VIEW MODE", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onSetTabStyle(TabViewStyle.GRID) }.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Chrome-Style Grid Cards", color = WyrixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("2-Column visual layout with workspace badges", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                    RadioButton(
                        selected = prefs.tabViewStyle == TabViewStyle.GRID,
                        onClick = { onSetTabStyle(TabViewStyle.GRID) },
                        colors = RadioButtonDefaults.colors(selectedColor = WyrixNeonBlue)
                    )
                }
                Divider(color = WyrixBorder)
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onSetTabStyle(TabViewStyle.LIST) }.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Compact List View", color = WyrixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Fast vertical list for high-density tab browsing", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                    RadioButton(
                        selected = prefs.tabViewStyle == TabViewStyle.LIST,
                        onClick = { onSetTabStyle(TabViewStyle.LIST) },
                        colors = RadioButtonDefaults.colors(selectedColor = WyrixNeonBlue)
                    )
                }
            }
        }

        Text("SESSION RESTORATION", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Restore Tabs on Startup", color = WyrixTextPrimary, fontSize = 14.sp)
                    Text("Keep previously opened normal tabs across app restarts", color = WyrixTextSecondary, fontSize = 11.sp)
                }
                Switch(checked = prefs.restoreSessionOnStartup, onCheckedChange = onToggleRestore)
            }
        }
    }
}

// 8. Homepage Customization Screen
@Composable
fun HomepageSettingsScreen(
    prefs: WyrixPreferences,
    onToggleShortcuts: (Boolean) -> Unit,
    onToggleShieldStats: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    SettingsSubScreenScaffold("Homepage Customization", onBack) {
        Text("NEURAL DASHBOARD COMPONENTS", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Show Quick Shortcuts", color = WyrixTextPrimary, fontSize = 14.sp)
                        Text("Display shortcuts grid on the homepage", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                    Switch(checked = prefs.showHomepageShortcuts, onCheckedChange = onToggleShortcuts)
                }
                Divider(color = WyrixBorder)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Show Shield Live Status Card", color = WyrixTextPrimary, fontSize = 14.sp)
                        Text("Real-time telemetry and privacy status indicator", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                    Switch(checked = prefs.showHomepageShieldStats, onCheckedChange = onToggleShieldStats)
                }
            }
        }
    }
}

// 9. Website Notifications Screen
@Composable
fun NotificationsSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    SettingsSubScreenScaffold("Website Notifications", onBack) {
        Text("NOTIFICATION POLICIES", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Manage Push Notifications", color = WyrixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Websites must prompt before sending notifications. Manage active channels in system settings.", color = WyrixTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WyrixNeonBlue)
                ) {
                    Text("Open Notification Channels", color = WyrixBlack, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 10. Appearance & Themes Screen
@Composable
fun AppearanceSettingsScreen(
    prefs: WyrixPreferences,
    onSetTheme: (AppTheme) -> Unit,
    onSetAccent: (AccentColorChoice) -> Unit,
    onBack: () -> Unit
) {
    SettingsSubScreenScaffold("Appearance and Themes", onBack) {
        Text("THEME PRESETS", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column {
                AppTheme.values().forEachIndexed { idx, theme ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onSetTheme(theme) }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            when(theme) {
                                AppTheme.AMOLED -> "AMOLED Deep Black (#030407)"
                                AppTheme.DARK -> "Midnight Slate Dark"
                                AppTheme.LIGHT -> "Cyber Light Mode"
                                AppTheme.SYSTEM -> "Follow System Preference"
                            },
                            color = WyrixTextPrimary,
                            fontSize = 14.sp
                        )
                        RadioButton(
                            selected = prefs.appTheme == theme,
                            onClick = { onSetTheme(theme) },
                            colors = RadioButtonDefaults.colors(selectedColor = WyrixNeonBlue)
                        )
                    }
                    if (idx < AppTheme.values().lastIndex) Divider(color = WyrixBorder)
                }
            }
        }

        Text("CYBER ACCENT COLORS", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column {
                AccentColorChoice.values().forEachIndexed { idx, acc ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onSetAccent(acc) }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(acc.name.replace("_", " "), color = WyrixTextPrimary, fontSize = 14.sp)
                        RadioButton(
                            selected = prefs.accentColor == acc,
                            onClick = { onSetAccent(acc) },
                            colors = RadioButtonDefaults.colors(selectedColor = WyrixNeonBlue)
                        )
                    }
                    if (idx < AccentColorChoice.values().lastIndex) Divider(color = WyrixBorder)
                }
            }
        }
    }
}

// 11. Accessibility Screen
@Composable
fun AccessibilitySettingsScreen(
    prefs: WyrixPreferences,
    onSetZoom: (Int) -> Unit,
    onToggleForceZoom: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    SettingsSubScreenScaffold("Accessibility", onBack) {
        Text("TEXT ZOOM SCALING: ${prefs.textZoomPercent}%", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Slider(
                    value = prefs.textZoomPercent.toFloat(),
                    onValueChange = { onSetZoom(it.toInt()) },
                    valueRange = 50f..200f,
                    steps = 14,
                    colors = SliderDefaults.colors(thumbColor = WyrixNeonBlue, activeTrackColor = WyrixNeonBlue)
                )
                Text("Sample text preview at ${prefs.textZoomPercent}% zoom", fontSize = (14 * (prefs.textZoomPercent / 100f)).sp, color = WyrixTextPrimary)
            }
        }

        Text("GESTURE CONTROLS", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Force Enable Zoom", color = WyrixTextPrimary, fontSize = 14.sp)
                    Text("Override site restrictions and allow pinch-to-zoom on all websites", color = WyrixTextSecondary, fontSize = 11.sp)
                }
                Switch(checked = prefs.forceEnableZoom, onCheckedChange = onToggleForceZoom)
            }
        }
    }
}

// 12. Site Permissions Screen
@Composable
fun SitePermissionsScreen(
    prefs: WyrixPreferences,
    onToggleJs: (Boolean) -> Unit,
    onSetCookiePolicy: (CookiePolicy) -> Unit,
    onBack: () -> Unit
) {
    SettingsSubScreenScaffold("Site Permissions", onBack) {
        Text("WEB EXECUTION CONTROLS", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("JavaScript Execution", color = WyrixTextPrimary, fontSize = 14.sp)
                    Text("Disable to prevent all script execution on websites", color = WyrixTextSecondary, fontSize = 11.sp)
                }
                Switch(checked = prefs.javaScriptEnabled, onCheckedChange = onToggleJs)
            }
        }

        Text("COOKIE POLICIES", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column {
                CookiePolicy.values().forEachIndexed { idx, cp ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onSetCookiePolicy(cp) }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            when(cp) {
                                CookiePolicy.ACCEPT_ALL -> "Accept All Cookies"
                                CookiePolicy.BLOCK_THIRD_PARTY -> "Block Third-Party & Cross-Site Cookies"
                                CookiePolicy.BLOCK_ALL -> "Block All Cookies (May Break Sites)"
                            },
                            color = WyrixTextPrimary,
                            fontSize = 13.sp
                        )
                        RadioButton(
                            selected = prefs.cookiePolicy == cp,
                            onClick = { onSetCookiePolicy(cp) },
                            colors = RadioButtonDefaults.colors(selectedColor = WyrixNeonBlue)
                        )
                    }
                    if (idx < CookiePolicy.values().lastIndex) Divider(color = WyrixBorder)
                }
            }
        }
    }
}

// 13. Languages Screen
@Composable
fun LanguagesSettingsScreen(
    currentLang: String,
    onSetLang: (String) -> Unit,
    onBack: () -> Unit
) {
    val languages = listOf("SYSTEM" to "Follow System Locale", "EN" to "English (US)", "HI" to "हिंदी (Hindi)")
    SettingsSubScreenScaffold("Languages and Translation", onBack) {
        Text("APPLICATION INTERFACE LANGUAGE", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column {
                languages.forEachIndexed { idx, (code, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onSetLang(code) }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = WyrixTextPrimary, fontSize = 14.sp)
                        RadioButton(
                            selected = currentLang == code,
                            onClick = { onSetLang(code) },
                            colors = RadioButtonDefaults.colors(selectedColor = WyrixNeonBlue)
                        )
                    }
                    if (idx < languages.lastIndex) Divider(color = WyrixBorder)
                }
            }
        }
    }
}

// 14. Advanced Download Manager Screen
@Composable
fun DownloadsSettingsScreen(
    prefs: WyrixPreferences,
    onToggleWifiOnly: (Boolean) -> Unit,
    onToggleAskLocation: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    SettingsSubScreenScaffold("Advanced Download Manager", onBack) {
        Text("DOWNLOAD SETTINGS", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Download via Wi-Fi Only", color = WyrixTextPrimary, fontSize = 14.sp)
                        Text("Pause downloads while on mobile cellular data", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                    Switch(checked = prefs.downloadWifiOnly, onCheckedChange = onToggleWifiOnly)
                }
                Divider(color = WyrixBorder)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ask Where to Save Each File", color = WyrixTextPrimary, fontSize = 14.sp)
                        Text("Prompt file naming dialog before initiating transfer", color = WyrixTextSecondary, fontSize = 11.sp)
                    }
                    Switch(checked = prefs.askDownloadLocation, onCheckedChange = onToggleAskLocation)
                }
            }
        }
    }
}

// 15. About WYRIX Browser Screen
@Composable
fun AboutSettingsScreen(onBack: () -> Unit) {
    SettingsSubScreenScaffold("About WYRIX Browser", onBack) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("WYRIX BROWSER", color = WyrixNeonBlue, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("v1.0.0-PROD (Build 2026.10)", color = WyrixTextSecondary, fontSize = 12.sp)
            }
        }

        Text("ENGINE & PLATFORM SPECIFICATIONS", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Rendering Engine: Mozilla GeckoView 133.0.20241121140525", color = WyrixTextPrimary, fontSize = 13.sp)
                Text("Architecture: Jetpack Compose + Coroutines + Room + DataStore", color = WyrixTextPrimary, fontSize = 13.sp)
                Text("Telemetry Audit: 0 telemetry servers. 100% Client-side local browsing.", color = WyrixElectricGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Target SDK: 34 (Android 14) • Min SDK: 28 (Android 9.0)", color = WyrixTextSecondary, fontSize = 12.sp)
            }
        }

        Text("OPEN SOURCE LICENSES", color = WyrixNeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = WyrixPanel), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Mozilla Public License 2.0 (MPL-2.0)", color = WyrixTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Covers GeckoView core binary artifacts and GeckoSession bindings.", color = WyrixTextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Apache License 2.0", color = WyrixTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Covers AndroidX libraries, Jetpack Compose, Material3, and Room.", color = WyrixTextSecondary, fontSize = 11.sp)
            }
        }
    }
}
