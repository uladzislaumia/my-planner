package com.uladzislaumia.myplanner.ui.navigation

import com.uladzislaumia.myplanner.ui.viewmodel.CategoryFilter
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object SignUp : Route

    @Serializable
    data object Home : Route

    /** Tasks of one category: [categoryId], tasks without a category ([uncategorized]) or all tasks (neither). */
    @Serializable
    data class CategoryTasks(
        val categoryId: String? = null,
        val uncategorized: Boolean = false,
    ) : Route {
        val filter: CategoryFilter
            get() =
                when {
                    categoryId != null -> CategoryFilter.Of(categoryId)
                    uncategorized -> CategoryFilter.Uncategorized
                    else -> CategoryFilter.All
                }

        companion object {
            fun of(filter: CategoryFilter): CategoryTasks =
                when (filter) {
                    CategoryFilter.All -> CategoryTasks()
                    CategoryFilter.Uncategorized -> CategoryTasks(uncategorized = true)
                    is CategoryFilter.Of -> CategoryTasks(categoryId = filter.categoryId)
                }
        }
    }
}
