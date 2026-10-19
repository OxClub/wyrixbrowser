package com.wyrix.browser.ui.screens

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
