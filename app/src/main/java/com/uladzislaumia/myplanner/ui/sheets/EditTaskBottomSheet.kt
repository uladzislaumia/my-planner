package com.uladzislaumia.myplanner.ui.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.ui.components.CategoryDropdown
import com.uladzislaumia.myplanner.ui.components.CompletedLabel
import com.uladzislaumia.myplanner.ui.components.DateTimePickerField
import com.uladzislaumia.myplanner.ui.components.PrioritySelector
import com.uladzislaumia.myplanner.ui.components.TaskTitleField
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
    var showDeleteDialog by remember { mutableStateOf(false) }
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
                    // Scrolls when the form doesn't fit: a small screen, large font or the keyboard
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
        ) {
            // The task's own title is the heading. Deleting is rare and can't be undone,
            // so it waits in the corner behind a confirmation
            Row(verticalAlignment = Alignment.CenterVertically) {
                TaskTitleField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = stringResource(R.string.tasks_field_title),
                    // Autosave skips a blank title, so say why the change isn't kept
                    errorText = if (title.isBlank()) stringResource(R.string.tasks_error_title_empty) else null,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sketch_trash),
                        contentDescription = stringResource(R.string.tasks_delete_button),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }

            if (isCompleted) {
                CompletedLabel()
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.tasks_field_description)) },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            CategoryDropdown(
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = {
                    selectedCategoryId = it
                },
                categories = categories,
            )

            PrioritySelector(selected = selectedPriority, onSelect = { selectedPriority = it })

            DateTimePickerField(
                selectedDateTime = selectedDueDate,
                onDateTimeSelected = {
                    selectedDueDate = it
                },
            )

            // Edits save themselves, so completing the task is the one action left for the bottom of the sheet
            if (isCompleted) {
                OutlinedButton(
                    onClick = { isCompleted = false },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.tasks_mark_active))
                }
            } else {
                FilledTonalButton(
                    onClick = { isCompleted = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sketch_check),
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.tasks_mark_completed))
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.tasks_delete_confirm_title)) },
            text = { Text(stringResource(R.string.tasks_delete_confirm_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        isDeleted = true
                        onDelete(item.id)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) {
                    Text(stringResource(R.string.tasks_delete_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            },
        )
    }
}
