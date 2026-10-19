package com.wyrix.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wyrix.browser.core.designsystem.*

@Composable
fun BrowserToolbar(
    url: String,
    progress: Int,
    isSecure: Boolean,
    tabCount: Int,
    shieldActive: Boolean,
    onNavigate: (String) -> Unit,
    onGoHome: () -> Unit,
    onOpenTabs: () -> Unit,
    onOpenControlCenter: () -> Unit,
    onToggleShield: () -> Unit,
    onOpenCommandCenter: () -> Unit
) {
    var text by remember(url) { mutableStateOf(if (url == "about:blank") "" else url) }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(WyrixBlack)
    ) {
        if (progress in 1..99) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(WyrixCyberGradient)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Home Button
            IconButton(onClick = onGoHome, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = WyrixNeonBlue, modifier = Modifier.size(20.dp))
            }

            // Omnibox
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(WyrixPanel, RoundedCornerShape(14.dp))
                    .border(1.dp, if (isEditing) WyrixNeonBlue else WyrixBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (isEditing) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = WyrixTextPrimary,
                                unfocusedTextColor = WyrixTextPrimary,
                                cursorColor = WyrixNeonBlue
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    isEditing = false
                                    if (text.isNotBlank()) onNavigate(text)
                                }
                            )
                        )
                        if (text.isNotEmpty()) {
                            IconButton(onClick = { text = "" }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = WyrixTextDisabled, modifier = Modifier.size(16.dp))
                            }
                        }
                        IconButton(
                            onClick = {
                                isEditing = false
                                if (text.isNotBlank()) onNavigate(text)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Go", tint = WyrixNeonBlue, modifier = Modifier.size(20.dp))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { isEditing = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSecure) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Security",
                            tint = if (isSecure) WyrixNeonBlue else WyrixAlertRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (text.isBlank() || text == "about:blank") "Search or enter address" else text,
                            color = if (text.isBlank() || text == "about:blank") WyrixTextDisabled else WyrixTextPrimary,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Command Palette
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = WyrixPanel,
                border = androidx.compose.foundation.BorderStroke(1.dp, WyrixBorder),
                modifier = Modifier.size(36.dp).clickable { onOpenCommandCenter() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Terminal, contentDescription = "Commands", tint = WyrixNeonViolet, modifier = Modifier.size(18.dp))
                }
            }

            // Tab Counter
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = WyrixDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, WyrixNeonBlue),
                modifier = Modifier.size(36.dp).clickable { onOpenTabs() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = tabCount.toString(),
                        color = WyrixNeonBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Control Center
            IconButton(onClick = onOpenControlCenter, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.DashboardCustomize, contentDescription = "Control Center", tint = WyrixTextPrimary, modifier = Modifier.size(20.dp))
            }
        }
    }
}
