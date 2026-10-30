package com.uladzislaumia.myplanner.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.ui.util.DEFAULT_DEADLINE_HOUR
import com.uladzislaumia.myplanner.ui.util.plusDays
import com.uladzislaumia.myplanner.ui.util.startOfDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.text.format.DateFormat as AndroidDateFormat

/**
 * Deadline as two outlined fields like the rest of the form: the date, which can be cleared, and the reminder time,
 * available once there is a date. Tapping a field opens its picker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerField(
    selectedDateTime: Date?,
    onDateTimeSelected: (Date?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    // Same 12 or 24 hour format as the device and the task cards
    val is24Hour = remember(context, locale) { AndroidDateFormat.is24HourFormat(context) }
    val timeFormat = remember(context, locale) { AndroidDateFormat.getTimeFormat(context) }

    val calendar =
        remember(selectedDateTime) {
            Calendar.getInstance().apply {
                if (selectedDateTime != null) {
                    time = selectedDateTime
                } else {
                    set(Calendar.HOUR_OF_DAY, DEFAULT_DEADLINE_HOUR)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }
            }
        }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        PickerField(
            value = selectedDateTime?.let { relativeDate(it, locale) }.orEmpty(),
            label = stringResource(R.string.tasks_field_date),
            iconRes = R.drawable.ic_sketch_calendar,
            onClick = { showDatePicker = true },
            trailingIcon =
                if (selectedDateTime != null) {
                    {
                        IconButton(onClick = { onDateTimeSelected(null) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.tasks_clear_date),
                            )
                        }
                    }
                } else {
                    null
                },
            // The date is the longer value and has the clear button, so it gets more room
            modifier = Modifier.weight(DATE_FIELD_WEIGHT),
        )
        PickerField(
            value = selectedDateTime?.let { timeFormat.format(it) }.orEmpty(),
            label = stringResource(R.string.tasks_field_time),
            iconRes = R.drawable.ic_sketch_bell,
            onClick = { showTimePicker = true },
            enabled = selectedDateTime != null,
            modifier = Modifier.weight(TIME_FIELD_WEIGHT),
        )
    }

    if (showDatePicker) {
        val datePickerState =
            rememberDatePickerState(
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
                            val newCal =
                                Calendar.getInstance().apply {
                                    if (selectedDateTime != null) time = selectedDateTime
                                    set(Calendar.YEAR, pickedCal.get(Calendar.YEAR))
                                    set(Calendar.MONTH, pickedCal.get(Calendar.MONTH))
                                    set(Calendar.DAY_OF_MONTH, pickedCal.get(Calendar.DAY_OF_MONTH))
                                    if (selectedDateTime == null) {
                                        set(Calendar.HOUR_OF_DAY, DEFAULT_DEADLINE_HOUR)
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
        val timePickerState =
            rememberTimePickerState(
                initialHour = calendar.get(Calendar.HOUR_OF_DAY),
                initialMinute = calendar.get(Calendar.MINUTE),
                is24Hour = is24Hour,
            )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newCal =
                            Calendar.getInstance().apply {
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

/**
 * "Today", "Tomorrow" or "Yesterday" like the calendar headers,
 * otherwise a short date with the year only if it differs from the current one.
 */
@Composable
private fun relativeDate(
    date: Date,
    locale: Locale,
): String {
    val day = date.startOfDay()
    val today = Date().startOfDay()
    val sameYear = remember(day, today) { day.year() == today.year() }
    val skeleton = if (sameYear) "EEEdMMM" else "dMMMyyyy"
    val dateFormat =
        remember(locale, skeleton) {
            SimpleDateFormat(AndroidDateFormat.getBestDateTimePattern(locale, skeleton), locale)
        }
    return when (day) {
        today.plusDays(-1) -> stringResource(R.string.calendar_yesterday)
        today -> stringResource(R.string.calendar_today)
        today.plusDays(1) -> stringResource(R.string.calendar_tomorrow)
        else -> dateFormat.format(day)
    }
}

private fun Date.year(): Int = Calendar.getInstance().apply { time = this@year }.get(Calendar.YEAR)

/** Read-only outlined field that opens a picker on tap instead of taking text. */
@Composable
private fun PickerField(
    value: String,
    label: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val currentOnClick by rememberUpdatedState(onClick)
    // A read-only text field swallows clicks, so react to taps through its interaction source
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                // Don't leave the field highlighted as focused once the picker closes
                focusManager.clearFocus()
                currentOnClick()
            }
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        enabled = enabled,
        singleLine = true,
        label = { Text(label, maxLines = 1) },
        leadingIcon = { Icon(painter = painterResource(iconRes), contentDescription = null) },
        trailingIcon = trailingIcon,
        interactionSource = interactionSource,
        modifier = modifier,
    )
}

private const val DATE_FIELD_WEIGHT = 3f
private const val TIME_FIELD_WEIGHT = 2f
