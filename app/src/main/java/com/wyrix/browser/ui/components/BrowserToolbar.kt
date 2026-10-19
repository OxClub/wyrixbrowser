package com.wyrix.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    onOpenTabs: () -> Unit,
    onOpenMenu: () -> Unit,
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
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = WyrixNeonBlue,
                trackColor = Color.Transparent
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleShield) {
                Icon(
                    imageVector = if (shieldActive) Icons.Default.Shield else Icons.Default.ShieldMoon,
                    contentDescription = "Shield",
                    tint = if (shieldActive) WyrixElectricGreen else WyrixAlertRed
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(WyrixPanel, RoundedCornerShape(12.dp))
                    .border(1.dp, WyrixBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp),
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
                                },
                                onGo = {
                                    isEditing = false
                                    if (text.isNotBlank()) onNavigate(text)
                                },
                                onDone = {
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
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (text.isBlank() || text == "about:blank") "Search or enter address" else text,
                            color = if (text.isBlank() || text == "about:blank") WyrixTextDisabled else WyrixTextPrimary,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            IconButton(onClick = onOpenCommandCenter) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Command Center",
                    tint = WyrixNeonViolet
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(WyrixDarkSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, WyrixNeonBlue, RoundedCornerShape(8.dp))
                    .clickable { onOpenTabs() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tabCount.toString(),
                    color = WyrixNeonBlue,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = onOpenMenu) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = WyrixTextPrimary
                )
            }
        }
    }
}
