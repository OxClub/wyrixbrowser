package com.wyrix.browser.ui.screens

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
