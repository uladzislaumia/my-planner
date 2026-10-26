package com.uladzislaumia.myplanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.ui.theme.MyPlannerTheme
import com.uladzislaumia.myplanner.ui.util.plusDays
import com.uladzislaumia.myplanner.ui.util.startOfDay
import com.uladzislaumia.myplanner.ui.viewmodel.DaySection
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import android.text.format.DateFormat as AndroidDateFormat

/** Tasks grouped by day under sticky day headers; an empty section shows a "no tasks" row. */
@Composable
fun PlannerList(
    sections: List<DaySection>,
    modifier: Modifier = Modifier,
    onItemClick: (PlannerItem) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        sections.forEach { section ->
            stickyHeader(key = section.date.time) {
                DayHeader(date = section.date)
            }
            if (section.items.isEmpty()) {
                item(key = "empty-${section.date.time}") {
                    Text(
                        text = stringResource(R.string.calendar_no_tasks),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(section.items, key = { it.id }) { item ->
                    PlannerCard(
                        data = item,
                        // The day header already shows the date
                        showDate = false,
                        onClick = { onItemClick(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DayHeader(date: Date) {
    val locale = LocalConfiguration.current.locales[0]
    val today = remember { Date().startOfDay() }
    val relativeDay =
        when (date) {
            today -> stringResource(R.string.calendar_today)
            today.plusDays(1) -> stringResource(R.string.calendar_tomorrow)
            else -> null
        }
    val fullDate =
        remember(date, locale) {
            val sameYear = date.year() == today.year()
            val skeleton = if (sameYear) "EEEEdMMMM" else "EEEEdMMMMyyyy"
            SimpleDateFormat(AndroidDateFormat.getBestDateTimePattern(locale, skeleton), locale)
                .format(date)
                .replaceFirstChar { it.titlecase(locale) }
        }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = if (relativeDay != null) "$relativeDay · $fullDate" else fullDate,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color =
                if (date ==
                    today
                ) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

private fun Date.year(): Int = Calendar.getInstance().apply { time = this@year }.get(Calendar.YEAR)

@Preview(showBackground = true)
@Composable
fun PlannerListPreview() {
    MyPlannerTheme {
        val demoCount = 3
        val today = Date().startOfDay()
        val demoItems =
            List(demoCount) { i ->
                PlannerItem(
                    id = i.toString(),
                    title = "Item $i",
                    description = "Description $i",
                    categoryId = Category.GROCERIES.id,
                    groupId = null,
                    assigneeId = null,
                    dueDate = today,
                    priority = Priority.MEDIUM,
                )
            }
        PlannerList(
            sections =
                listOf(
                    DaySection(today, demoItems),
                    DaySection(today.plusDays(2), emptyList()),
                ),
        )
    }
}
