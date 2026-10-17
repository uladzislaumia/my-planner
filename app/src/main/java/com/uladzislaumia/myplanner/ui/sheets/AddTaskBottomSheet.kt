package com.uladzislaumia.myplanner.ui.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.ui.components.CategoryDropdown
import com.uladzislaumia.myplanner.ui.components.DateTimePickerField
import java.util.Date
import androidx.compose.ui.res.stringResource
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.ui.text.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    categories: List<Category> = Category.DEFAULT_CATEGORIES,
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
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedDueDate by remember { mutableStateOf<Date?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = stringResource(R.string.tasks_add_title),
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
                onCategorySelected = { selectedCategoryId = it },
                categories = categories,
            )

            Text(stringResource(R.string.tasks_label_deadline), style = MaterialTheme.typography.labelMedium)
            DateTimePickerField(
                selectedDateTime = selectedDueDate,
                onDateTimeSelected = { selectedDueDate = it },
            )

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description, selectedPriority, selectedCategoryId, selectedDueDate)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(stringResource(R.string.tasks_create_button))
            }
        }
    }
}
