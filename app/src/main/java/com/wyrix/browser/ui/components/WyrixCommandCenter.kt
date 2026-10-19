package com.wyrix.browser.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wyrix.browser.core.designsystem.*

data class WyrixCommand(val label: String, val category: String, val action: () -> Unit)

@Composable
fun WyrixCommandCenter(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    commands: List<WyrixCommand>
) {
    if (!isOpen) return

    var query by remember { mutableStateOf("") }
    val filteredCommands = remember(query, commands) {
        if (query.isBlank()) commands
        else commands.filter { it.label.contains(query, ignoreCase = true) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = WyrixPanel,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("WYRIX Command Palette", color = WyrixNeonBlue, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Type command or action...", color = WyrixTextDisabled) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WyrixNeonBlue,
                        unfocusedBorderColor = WyrixBorder,
                        focusedTextColor = WyrixTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn {
                    items(filteredCommands) { command ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    command.action()
                                    onDismiss()
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(command.category, color = WyrixNeonViolet, style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(command.label, color = WyrixTextPrimary, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
