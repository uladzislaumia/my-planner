package com.uladzislaumia.myplanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.res.stringResource
import com.uladzislaumia.myplanner.R
import java.text.DateFormat

private const val DEFAULT_HOUR = 9

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DateTimePickerField(
    selectedDateTime: Date?,
    onDateTimeSelected: (Date?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormat = remember { DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val calendar = remember(selectedDateTime) {
        Calendar.getInstance().apply {
            if (selectedDateTime != null) {
                time = selectedDateTime
            } else {
                set(Calendar.HOUR_OF_DAY, DEFAULT_HOUR)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
        }
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        if (selectedDateTime == null) {
            OutlinedButton(onClick = { showDatePicker = true }) {
                Text(stringResource(R.string.tasks_select_date))
            }
        } else {
            AssistChip(
                onClick = { showDatePicker = true },
                label = { Text("📅 ${dateFormat.format(selectedDateTime)}") },
                trailingIcon = {
                    IconButton(
                        onClick = { onDateTimeSelected(null) },
                        modifier = Modifier.size(18.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.tasks_clear_date),
                        )
                    }
                },
            )

            AssistChip(
                onClick = { showTimePicker = true },
                label = { Text("⏰ ${timeFormat.format(selectedDateTime)}") },
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = calendar.timeInMillis,
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val pickedCal = Calendar.getInstance().apply { timeInMillis = selectedMillis }
                            val newCal = Calendar.getInstance().apply {
                                if (selectedDateTime != null) time = selectedDateTime
                                set(Calendar.YEAR, pickedCal.get(Calendar.YEAR))
                                set(Calendar.MONTH, pickedCal.get(Calendar.MONTH))
                                set(Calendar.DAY_OF_MONTH, pickedCal.get(Calendar.DAY_OF_MONTH))
                                if (selectedDateTime == null) {
                                    set(Calendar.HOUR_OF_DAY, DEFAULT_HOUR)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                }
                            }
                            onDateTimeSelected(newCal.time)
                        }
                        showDatePicker = false
                    },
                ) {
                    Text(stringResource(R.string.common_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = true,
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newCal = Calendar.getInstance().apply {
                            if (selectedDateTime != null) {
                                time = selectedDateTime
                            }
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                        }
                        onDateTimeSelected(newCal.time)
                        showTimePicker = false
                    },
                ) {
                    Text(stringResource(R.string.common_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            },
            text = {
                TimePicker(state = timePickerState)
            },
        )
    }
}
