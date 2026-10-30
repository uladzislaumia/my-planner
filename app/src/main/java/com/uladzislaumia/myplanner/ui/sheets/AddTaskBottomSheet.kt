package com.uladzislaumia.myplanner.ui.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.ui.components.CategoryDropdown
import com.uladzislaumia.myplanner.ui.components.DateTimePickerField
import com.uladzislaumia.myplanner.ui.components.PrioritySelector
import com.uladzislaumia.myplanner.ui.components.TaskTitleField
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    categories: List<Category> = Category.DEFAULT_CATEGORIES,
    initialCategoryId: String? = null,
    initialDueDate: Date? = null,
    onConfirm: (
        title: String,
        description: String,
        priority: Priority,
        categoryId: String?,
        dueDate: Date?,
    ) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var selectedCategoryId by remember { mutableStateOf(initialCategoryId) }
    var selectedDueDate by remember { mutableStateOf(initialDueDate) }
    val titleFocusRequester = remember { FocusRequester() }

    // Start typing the title right away
    LaunchedEffect(Unit) {
        titleFocusRequester.requestFocus()
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
            // The title being typed is the heading; Create stays beside it, in sight above the keyboard
            Row(verticalAlignment = Alignment.CenterVertically) {
                TaskTitleField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = stringResource(R.string.tasks_add_title),
                    focusRequester = titleFocusRequester,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onConfirm(title, description, selectedPriority, selectedCategoryId, selectedDueDate)
                        }
                    },
                    enabled = title.isNotBlank(),
                ) {
                    Text(stringResource(R.string.tasks_create_button))
                }
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
                onCategorySelected = { selectedCategoryId = it },
                categories = categories,
            )

            PrioritySelector(selected = selectedPriority, onSelect = { selectedPriority = it })

            DateTimePickerField(
                selectedDateTime = selectedDueDate,
                onDateTimeSelected = { selectedDueDate = it },
            )
        }
    }
}
