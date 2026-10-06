package com.uladzislaumia.myplanner.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

private val PRESET_EMOJIS = listOf(
    "🛒", "✈️", "🏠", "🏋️‍♂️", "🎬", "🛍️", "💼", "🩺", "💳",
    "🎯", "📚", "💻", "🎓", "⚽", "🏃‍♂️", "🧘‍♀️", "🚴‍♂️", "🍏",
    "🍲", "☕", "🍕", "🍔", "🏖️", "🚗", "🛠️", "🪴", "🎨",
    "🎵", "🎮", "🐶", "🎁", "💡", "📝", "📊", "💰", "🥳",
)

private val PRESET_COLORS = listOf(
    "#4CAF50", "#8BC34A", "#009688", "#2E7D32",
    "#00BCD4", "#03A9F4", "#2196F3", "#1565C0",
    "#9C27B0", "#673AB7", "#E91E63",
    "#FF9800", "#FFC107", "#FF5722",
    "#F44336", "#D32F2F",
    "#607D8B", "#795548", "#37474F",
)

@Composable
fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, icon: String, colorHex: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(PRESET_EMOJIS.first()) }
    var selectedColorHex by remember { mutableStateOf(PRESET_COLORS.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(24.dp),
        title = { Text("Create New Category") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text("Icon:", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(PRESET_EMOJIS) { emoji ->
                        FilterChip(
                            selected = selectedIcon == emoji,
                            onClick = { selectedIcon = emoji },
                            label = { Text(emoji) }
                        )
                    }
                }

                Text("Color:", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(PRESET_COLORS) { colorHex ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(colorHex))
                        } catch (_: Exception) {
                            Color.Gray
                        }
                        val isSelected = selectedColorHex == colorHex
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColorHex = colorHex }
                                .then(
                                    if (isSelected) {
                                        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {}
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedIcon, selectedColorHex)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
