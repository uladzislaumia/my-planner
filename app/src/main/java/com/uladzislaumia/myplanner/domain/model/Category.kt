package com.uladzislaumia.myplanner.domain.model

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val colorHex: String,
) {
    companion object {
        val GROCERIES = Category("groceries", "Groceries", "🛒", "#4CAF50")
        val TRAVEL = Category("travel", "Travel & Trips", "✈️", "#00BCD4")
        val HOME = Category("home", "Home & Chores", "🏠", "#FF9800")
        val SPORTS = Category("sports", "Sports & Fitness", "🏋️‍♂️", "#E91E63")
        val ENTERTAINMENT = Category("entertainment", "Entertainment", "🎬", "#9C27B0")
        val SHOPPING = Category("shopping", "Shopping", "🛍️", "#3F51B5")
        val WORK = Category("work", "Work & Study", "💼", "#2196F3")
        val HEALTH = Category("health", "Health & Self-Care", "🩺", "#009688")
        val FINANCE = Category("finance", "Finance & Bills", "💳", "#FF5722")

        val DEFAULT_CATEGORIES = listOf(
            GROCERIES,
            TRAVEL,
            HOME,
            SPORTS,
            ENTERTAINMENT,
            SHOPPING,
            WORK,
            HEALTH,
            FINANCE,
        )

        fun findById(id: String?): Category? {
            return DEFAULT_CATEGORIES.find { it.id == id }
        }
    }
}
