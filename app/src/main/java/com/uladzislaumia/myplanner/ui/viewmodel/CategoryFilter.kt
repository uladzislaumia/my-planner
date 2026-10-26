package com.uladzislaumia.myplanner.ui.viewmodel

import com.uladzislaumia.myplanner.domain.model.PlannerItem

/** Which tasks a category tile and the category screen show. */
sealed interface CategoryFilter {
    fun matches(item: PlannerItem): Boolean

    data object All : CategoryFilter {
        override fun matches(item: PlannerItem) = true
    }

    data object Uncategorized : CategoryFilter {
        override fun matches(item: PlannerItem) = item.categoryId == null
    }

    data class Of(
        val categoryId: String,
    ) : CategoryFilter {
        override fun matches(item: PlannerItem) = item.categoryId == categoryId
    }
}
