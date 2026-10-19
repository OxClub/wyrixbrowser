package com.wyrix.browser.ui.screens

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
