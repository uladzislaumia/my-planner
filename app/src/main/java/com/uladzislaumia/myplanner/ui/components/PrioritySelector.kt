package com.uladzislaumia.myplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.ui.text.label
import com.uladzislaumia.myplanner.ui.theme.MyPlannerTheme

@Composable
fun Priority.color(): Color =
    colorResource(
        when (this) {
            Priority.HIGH -> R.color.priority_high
            Priority.MEDIUM -> R.color.priority_medium
            Priority.LOW -> R.color.priority_low
        },
    )

/** Colored dot of a priority, the same on task cards and in the priority selector. */
@Composable
fun PriorityDot(
    priority: Priority,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
) {
    Box(
        modifier =
            modifier
                .size(10.dp)
                .background(priority.color().copy(alpha = alpha), CircleShape),
    )
}

/**
 * Priority choice under its own title: all three options at once, each with the dot it gets on the task card.
 * The dot stays on the selected segment too, instead of the default check mark.
 */
@Composable
fun PrioritySelector(
    selected: Priority,
    onSelect: (Priority) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The title sits close to its buttons, so it reads as their label rather than a separate row
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.tasks_label_priority),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        PrioritySegments(selected = selected, onSelect = onSelect)
    }
}

@Composable
private fun PrioritySegments(
    selected: Priority,
    onSelect: (Priority) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        Priority.entries.forEachIndexed { index, priority ->
            SegmentedButton(
                selected = selected == priority,
                onClick = { onSelect(priority) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = Priority.entries.size),
                // The icon slot stretches its content to its height, so let the dot keep its own size
                icon = { PriorityDot(priority, Modifier.wrapContentSize()) },
            ) {
                Text(priority.label())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrioritySelectorPreview() {
    MyPlannerTheme {
        PrioritySelector(selected = Priority.MEDIUM, onSelect = {})
    }
}
