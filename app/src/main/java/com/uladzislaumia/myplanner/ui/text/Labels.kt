package com.uladzislaumia.myplanner.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.ui.viewmodel.CategoryFilter

@Composable
fun Priority.label(): String =
    stringResource(
        when (this) {
            Priority.LOW -> R.string.tasks_priority_low
            Priority.MEDIUM -> R.string.tasks_priority_medium
            Priority.HIGH -> R.string.tasks_priority_high
        },
    )

/** Built-in categories are stored with English names, so translate them by id; custom ones keep the user's name. */
@Composable
fun Category.displayName(): String {
    val resId =
        when (id) {
            Category.GROCERIES.id -> R.string.categories_default_groceries
            Category.TRAVEL.id -> R.string.categories_default_travel
            Category.HOME.id -> R.string.categories_default_home
            Category.SPORTS.id -> R.string.categories_default_sports
            Category.ENTERTAINMENT.id -> R.string.categories_default_entertainment
            Category.SHOPPING.id -> R.string.categories_default_shopping
            Category.WORK.id -> R.string.categories_default_work
            Category.HEALTH.id -> R.string.categories_default_health
            Category.FINANCE.id -> R.string.categories_default_finance
            else -> null
        }
    return resId?.let { stringResource(it) } ?: name
}

/** Title of a category tile and of the category screen. */
@Composable
fun CategoryFilter.title(categories: List<Category>): String =
    when (this) {
        CategoryFilter.All -> stringResource(R.string.categories_all_tasks)
        CategoryFilter.Uncategorized -> stringResource(R.string.categories_none)
        is CategoryFilter.Of -> categories.find { it.id == categoryId }?.displayName() ?: categoryId
    }
