package com.uladzislaumia.myplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.ui.text.displayName
import com.uladzislaumia.myplanner.ui.theme.MyPlannerTheme
import java.text.SimpleDateFormat
import java.util.Date
import android.text.format.DateFormat as AndroidDateFormat

/**
 * Task card in the warm style. The deadline is shown as a time chip; [showDate] adds the date to it,
 * for lists that don't already group tasks by day. [showCategory] hides the category chip on screens
 * that already show the category, like the screen of one category.
 */
@Composable
fun PlannerCard(
    data: PlannerItem,
    modifier: Modifier = Modifier,
    showDate: Boolean = true,
    showCategory: Boolean = true,
    onClick: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    val priorityColor =
        colorResource(
            when (data.priority) {
                Priority.HIGH -> R.color.priority_high
                Priority.MEDIUM -> R.color.priority_medium
                Priority.LOW -> R.color.priority_low
            },
        )
    val category = if (showCategory) Category.findById(data.categoryId) else null
    val contentAlpha = if (data.isCompleted) COMPLETED_ALPHA else 1f

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(10.dp),
                    shape = CircleShape,
                    color = priorityColor.copy(alpha = contentAlpha),
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface.copy(alpha = contentAlpha),
                    textDecoration = if (data.isCompleted) TextDecoration.LineThrough else null,
                )
            }

            val dueDate = data.dueDate
            if (category != null || dueDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (dueDate != null) {
                        CardChip(text = "⏰ ${formatDeadline(dueDate, showDate)}", contentAlpha = contentAlpha)
                    }
                    if (category != null) {
                        CardChip(
                            text = category.displayName(),
                            contentAlpha = contentAlpha,
                            leadingIcon = {
                                CategoryIcon(category, modifier = Modifier.alpha(contentAlpha), size = 16.dp)
                            },
                        )
                    }
                }
            }

            if (data.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (data.isCompleted) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "✓ " + stringResource(R.string.tasks_card_completed),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.priority_low),
                )
            }
        }
    }
}

@Composable
private fun CardChip(
    text: String,
    contentAlpha: Float,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = colorScheme.secondaryContainer.copy(alpha = contentAlpha),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSecondaryContainer.copy(alpha = contentAlpha),
            )
        }
    }
}

/** Deadline time in the user's 12/24-hour format, optionally preceded by a short date. */
@Composable
private fun formatDeadline(
    date: Date,
    showDate: Boolean,
): String {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val timeFormat = remember(context, locale) { AndroidDateFormat.getTimeFormat(context) }
    val dateFormat =
        remember(locale) { SimpleDateFormat(AndroidDateFormat.getBestDateTimePattern(locale, "dMMM"), locale) }
    val time = timeFormat.format(date)
    return if (showDate) "${dateFormat.format(date)}, $time" else time
}

private const val COMPLETED_ALPHA = 0.55f

@Preview(showBackground = true)
@Composable
fun PlannerCardPreview() {
    MyPlannerTheme {
        PlannerCard(
            data =
                PlannerItem(
                    id = "1",
                    title = "Book tickets",
                    description = "Reserve flight tickets and hotel accommodation for the upcoming trip.",
                    categoryId = Category.TRAVEL.id,
                    groupId = null,
                    assigneeId = null,
                    dueDate = Date(),
                    priority = Priority.HIGH,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlannerCardCompletedPreview() {
    MyPlannerTheme {
        PlannerCard(
            data =
                PlannerItem(
                    id = "2",
                    title = "Morning yoga",
                    description = "Complete a 30-minute stretching and yoga workout.",
                    categoryId = Category.SPORTS.id,
                    groupId = null,
                    assigneeId = null,
                    dueDate = null,
                    isCompleted = true,
                    priority = Priority.MEDIUM,
                ),
            showDate = false,
        )
    }
}
