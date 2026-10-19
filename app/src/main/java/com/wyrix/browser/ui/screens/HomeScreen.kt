package com.wyrix.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wyrix.browser.core.designsystem.*

data class QuickTile(val title: String, val url: String, val icon: String)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val tiles = listOf(
        QuickTile("DuckDuckGo", "https://duckduckgo.com", "🦆"),
        QuickTile("GitHub", "https://github.com", "🐙"),
        QuickTile("Wikipedia", "https://wikipedia.org", "🌐"),
        QuickTile("Reddit", "https://reddit.com", "🤖")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WyrixBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WYRIX",
            color = WyrixNeonBlue,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 4.sp
        )
        Text(
            text = "Autonomous Neural Web Interface",
            color = WyrixTextSecondary,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(tiles) { tile ->
                Surface(
                    color = WyrixPanel,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(80.dp)
                        .clickable { onNavigate(tile.url) }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(tile.icon, fontSize = 24.sp)
                        Text(tile.title, color = WyrixTextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
