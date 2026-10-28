package com.uladzislaumia.myplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.ui.theme.MyPlannerTheme
import com.uladzislaumia.myplanner.ui.util.plusDays
import com.uladzislaumia.myplanner.ui.util.startOfDay
import com.uladzislaumia.myplanner.ui.util.startOfWeek
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.roundToInt

/**
 * Week of day tiles that swipes left and right to the previous and next weeks.
 * The header shows the month of the visible week and, once away from today, a button back to today.
 */
@Composable
fun WeekStrip(
    selectedDate: Date,
    onDateClick: (Date) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    val today = remember { Date().startOfDay() }
    val currentWeekStart = remember(today) { today.startOfWeek() }
    val pagerState =
        rememberPagerState(
            initialPage = CURRENT_WEEK_PAGE + weeksBetween(currentWeekStart, selectedDate.startOfWeek()),
        ) { WEEK_PAGE_COUNT }
    val scope = rememberCoroutineScope()
    val monthFormat = remember(locale) { SimpleDateFormat("LLLL yyyy", locale) }
    val colorScheme = MaterialTheme.colorScheme

    // The middle day of the visible week decides the month, so a week across two months shows the one with more days
    val visibleWeekMiddle =
        currentWeekStart.plusDays((pagerState.currentPage - CURRENT_WEEK_PAGE) * DAYS_IN_WEEK + MIDDLE_DAY_OFFSET)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .padding(horizontal = 16.dp),
        ) {
            Text(
                text = monthFormat.format(visibleWeekMiddle).replaceFirstChar { it.titlecase(locale) },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (pagerState.currentPage != CURRENT_WEEK_PAGE || selectedDate != today) {
                Surface(
                    onClick = {
                        onDateClick(today)
                        scope.launch { pagerState.animateScrollToPage(CURRENT_WEEK_PAGE) }
                    },
                    shape = CircleShape,
                    color = colorScheme.secondaryContainer,
                ) {
                    Text(
                        text = stringResource(R.string.calendar_today),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    )
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val weekStart = currentWeekStart.plusDays((page - CURRENT_WEEK_PAGE) * DAYS_IN_WEEK)
            WeekRow(
                days = List(DAYS_IN_WEEK) { weekStart.plusDays(it) },
                selectedDate = selectedDate,
                today = today,
                onDateClick = onDateClick,
            )
        }
    }
}

@Composable
private fun WeekRow(
    days: List<Date>,
    selectedDate: Date,
    today: Date,
    onDateClick: (Date) -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val weekdayFormat = remember(locale) { SimpleDateFormat("EEE", locale) }
    val dayFormat = remember(locale) { SimpleDateFormat("d", locale) }
    val colorScheme = MaterialTheme.colorScheme

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        days.forEach { day ->
            val isSelected = day == selectedDate
            val isToday = day == today
            val contentColor = if (isSelected) colorScheme.onPrimary else colorScheme.onSecondaryContainer
            Surface(
                onClick = { onDateClick(day) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) colorScheme.primary else colorScheme.secondaryContainer,
                border = if (isToday && !isSelected) BorderStroke(1.5.dp, colorScheme.primary) else null,
                modifier = Modifier.weight(1f),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    Text(
                        text = weekdayFormat.format(day).replaceFirstChar { it.titlecase(locale) },
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.8f),
                        maxLines = 1,
                    )
                    Text(
                        text = dayFormat.format(day),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                        color = contentColor,
                    )
                }
            }
        }
    }
}

/** Whole weeks from one week start to another; rounding absorbs the hour a daylight saving switch adds or removes. */
private fun weeksBetween(
    from: Date,
    to: Date,
): Int = ((to.time - from.time).toDouble() / MILLIS_PER_WEEK).roundToInt()

private const val DAYS_IN_WEEK = 7
private const val MIDDLE_DAY_OFFSET = 3
private const val MILLIS_PER_WEEK = 7 * 24 * 60 * 60 * 1000L

// Ten years of weeks in each direction, with the current week in the middle
private const val CURRENT_WEEK_PAGE = 520
private const val WEEK_PAGE_COUNT = CURRENT_WEEK_PAGE * 2 + 1

@Preview(showBackground = true)
@Composable
fun WeekStripPreview() {
    MyPlannerTheme {
        WeekStrip(selectedDate = Date().startOfDay(), onDateClick = {})
    }
}
