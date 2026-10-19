package com.wyrix.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wyrix.browser.core.designsystem.*

data class ControlAction(val title: String, val icon: ImageVector, val action: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WyrixControlCenterSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    desktopMode: Boolean,
    shieldActive: Boolean,
    onToggleDesktop: () -> Unit,
    onToggleShield: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenSettings: () -> Unit
) {
    if (!isOpen) return

    val actions = listOf(
        ControlAction(if (desktopMode) "Mobile Site" else "Desktop Site", Icons.Default.Laptop, onToggleDesktop),
        ControlAction(if (shieldActive) "Shield: ON" else "Shield: OFF", Icons.Default.Shield, onToggleShield),
        ControlAction("Bookmarks", Icons.Default.Bookmark, onOpenBookmarks),
        ControlAction("History", Icons.Default.History, onOpenHistory),
        ControlAction("Downloads", Icons.Default.Download, onOpenDownloads),
        ControlAction("Settings", Icons.Default.Settings, onOpenSettings)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = WyrixDarkSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(WyrixBorder, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("WYRIX CONTROL CENTER", color = WyrixNeonBlue, fontSize = 12.sp, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().height(180.dp)
            ) {
                items(actions.size) { i ->
                    val item = actions[i]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .border(1.dp, WyrixBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                item.action()
                                onDismiss()
                            },
                        colors = CardDefaults.cardColors(containerColor = WyrixPanel),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(item.icon, contentDescription = null, tint = WyrixNeonBlue, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(item.title, color = WyrixTextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
