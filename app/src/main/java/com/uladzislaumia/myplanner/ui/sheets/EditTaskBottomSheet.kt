package com.uladzislaumia.myplanner.ui.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.ui.components.CategoryDropdown
import com.uladzislaumia.myplanner.ui.components.DateTimePickerField
import com.uladzislaumia.myplanner.ui.text.label
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

private val AUTOSAVE_DEBOUNCE = 300.milliseconds

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun EditTaskBottomSheet(
    item: PlannerItem,
    onDismiss: () -> Unit,
    categories: List<Category> = Category.DEFAULT_CATEGORIES,
    onSave: (PlannerItem) -> Unit,
    onDelete: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(item.title) }
    var description by remember { mutableStateOf(item.description) }
    var selectedPriority by remember { mutableStateOf(item.priority) }
    var selectedCategoryId by remember { mutableStateOf(item.categoryId) }
    var selectedDueDate by remember { mutableStateOf<Date?>(item.dueDate) }
    var isCompleted by remember { mutableStateOf(item.isCompleted) }

    var lastSavedItem by remember { mutableStateOf(item) }
    var isDeleted by remember { mutableStateOf(false) }
    val currentOnSave by rememberUpdatedState(onSave)

    fun buildDraft() =
        item.copy(
            title = title,
            description = description,
            priority = selectedPriority,
            categoryId = selectedCategoryId,
            dueDate = selectedDueDate,
            isCompleted = isCompleted,
        )

    // Skip writes when nothing actually changed
    fun saveIfChanged(draft: PlannerItem) {
        if (draft.title.isNotBlank() && draft != lastSavedItem) {
            lastSavedItem = draft
            currentOnSave(draft)
        }
    }

    // Debounce writes while typing so a burst of edits results in a single save
    LaunchedEffect(Unit) {
        snapshotFlow { buildDraft() }
            .debounce(AUTOSAVE_DEBOUNCE)
            .collect { saveIfChanged(it) }
    }

    // Flush pending changes when the sheet leaves the screen (unless the task was deleted)
    DisposableEffect(Unit) {
        onDispose {
            if (!isDeleted) saveIfChanged(buildDraft())
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
        ) {
            Text(
                text = stringResource(R.string.tasks_edit_title),
                style = MaterialTheme.typography.titleLarge,
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.tasks_field_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.tasks_field_description)) },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(stringResource(R.string.tasks_label_priority), style = MaterialTheme.typography.labelMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Priority.entries.forEach { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        label = { Text(priority.label()) },
                    )
                }
            }

            CategoryDropdown(
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = {
                    selectedCategoryId = it
                },
                categories = categories,
            )

            Text(stringResource(R.string.tasks_label_deadline), style = MaterialTheme.typography.labelMedium)
            DateTimePickerField(
                selectedDateTime = selectedDueDate,
                onDateTimeSelected = {
                    selectedDueDate = it
                },
            )

            Text(stringResource(R.string.tasks_label_status), style = MaterialTheme.typography.labelMedium)
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                SegmentedButton(
                    selected = !isCompleted,
                    onClick = { isCompleted = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                ) {
                    Text(stringResource(R.string.tasks_status_active))
                }
                SegmentedButton(
                    selected = isCompleted,
                    onClick = { isCompleted = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                ) {
                    Text(stringResource(R.string.tasks_status_completed))
                }
            }

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = {
                        isDeleted = true
                        onDelete(item.id)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.tasks_delete_button),
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.tasks_delete_button))
                }
            }
        }
    }
}
