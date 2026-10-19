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
