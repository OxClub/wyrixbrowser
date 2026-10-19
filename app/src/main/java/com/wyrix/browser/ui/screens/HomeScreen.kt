package com.wyrix.browser.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wyrix.browser.browser.model.Workspace
import com.wyrix.browser.core.designsystem.*

data class QuickTile(val title: String, val url: String, val icon: String, val badge: String)

@Composable
fun HomeScreen(
    currentWorkspace: Workspace,
    shieldActive: Boolean,
    onNavigate: (String) -> Unit,
    onSwitchWorkspace: (Workspace) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedEngine by remember { mutableStateOf("DuckDuckGo") }

    val engines = listOf("DuckDuckGo", "Google", "Brave", "Bing")

    val quickTiles = listOf(
        QuickTile("GitHub", "https://github.com", "🐙", "DEV"),
        QuickTile("ChatGPT", "https://chatgpt.com", "🤖", "AI"),
        QuickTile("DuckDuckGo", "https://duckduckgo.com", "🦆", "SEARCH"),
        QuickTile("Wikipedia", "https://wikipedia.org", "🌐", "WIKI"),
        QuickTile("Reddit", "https://reddit.com", "👾", "FEED"),
        QuickTile("YouTube", "https://youtube.com", "▶️", "MEDIA")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WyrixBlack)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Futuristic WYRIX Emblem
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(WyrixDarkSurface, CircleShape)
                .border(2.dp, WyrixCyberGradient, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("W", color = WyrixNeonBlue, fontSize = 36.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "WYRIX NEURAL",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 6.sp,
            color = WyrixTextPrimary
        )
        Text(
            text = "SECURE AUTONOMOUS WEB ENGINE",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
            color = WyrixNeonBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Futuristic Live Shield Status HUD Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (shieldActive) WyrixBorderGlow else WyrixBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = WyrixCardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(if (shieldActive) WyrixElectricGreen.copy(alpha = 0.15f) else WyrixAlertRed.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (shieldActive) Icons.Default.Shield else Icons.Default.ShieldMoon,
                            contentDescription = null,
                            tint = if (shieldActive) WyrixElectricGreen else WyrixAlertRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (shieldActive) "WYRIX SHIELD ACTIVE" else "SHIELD PAUSED",
                            color = WyrixTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (shieldActive) "Strict Anti-Tracking • Fingerprint Guard" else "Protection temporarily offline",
                            color = WyrixTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = WyrixPanel,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WyrixBorder)
                ) {
                    Text(
                        text = "100% PRIVATE",
                        color = WyrixNeonBlue,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search Engine Selector Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(engines) { engine ->
                val isSelected = engine == selectedEngine
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) WyrixNeonBlue else WyrixPanel,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WyrixNeonBlue else WyrixBorder),
                    modifier = Modifier.clickable { selectedEngine = engine }
                ) {
                    Text(
                        text = engine,
                        color = if (isSelected) WyrixBlack else WyrixTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center HUD Omnibox
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search with $selectedEngine or type URL...", color = WyrixTextDisabled, fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = WyrixNeonBlue, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onNavigate(searchQuery) }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Go", tint = WyrixNeonBlue)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, WyrixCyberGradient, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WyrixPanel,
                unfocusedContainerColor = WyrixPanel,
                focusedTextColor = WyrixTextPrimary,
                unfocusedTextColor = WyrixTextPrimary,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                if (searchQuery.isNotBlank()) onNavigate(searchQuery)
            })
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Workspaces Selector Dock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WyrixPanel, RoundedCornerShape(14.dp))
                .border(1.dp, WyrixBorder, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Workspace.values().forEach { ws ->
                val isSelected = ws == currentWorkspace
                val bgColor by animateColorAsState(if (isSelected) WyrixNeonViolet else Color.Transparent)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(bgColor, RoundedCornerShape(10.dp))
                        .clickable { onSwitchWorkspace(ws) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${ws.icon} ${ws.title}",
                        color = if (isSelected) Color.White else WyrixTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Launch Grid
        Text(
            text = "NEURAL SHORTCUTS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = WyrixTextSecondary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().height(190.dp)
        ) {
            items(quickTiles) { tile ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .border(1.dp, WyrixBorder, RoundedCornerShape(14.dp))
                        .clickable { onNavigate(tile.url) },
                    colors = CardDefaults.cardColors(containerColor = WyrixCardBg),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(tile.icon, fontSize = 22.sp)
                        Text(tile.title, color = WyrixTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text(tile.badge, color = WyrixNeonBlue, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
