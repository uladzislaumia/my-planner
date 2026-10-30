package com.uladzislaumia.myplanner.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.ui.text.displayName
import com.uladzislaumia.myplanner.ui.viewmodel.CategoryFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<Category> = Category.DEFAULT_CATEGORIES,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.find { it.id == selectedCategoryId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selectedCategory?.displayName() ?: stringResource(R.string.categories_none),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.categories_field)) },
            // "No Category" gets its own icon too, so the text doesn't shift when a category is picked
            leadingIcon = {
                if (selectedCategory != null) {
                    CategoryIcon(selectedCategory)
                } else {
                    CategoryIcon(CategoryFilter.Uncategorized, categories)
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier =
                Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.categories_none_option)) },
                leadingIcon = { CategoryIcon(CategoryFilter.Uncategorized, categories) },
                onClick = {
                    onCategorySelected(null)
                    expanded = false
                },
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.displayName()) },
                    leadingIcon = { CategoryIcon(category) },
                    onClick = {
                        onCategorySelected(category.id)
                        expanded = false
                    },
                )
            }
        }
    }
}
