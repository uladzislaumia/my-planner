package com.uladzislaumia.myplanner.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.ui.viewmodel.CategoryFilter

/** Hand-drawn icon of a built-in category, or null for a custom one, which keeps the user's emoji. */
@DrawableRes
fun Category.drawnIconRes(): Int? =
    when (id) {
        Category.GROCERIES.id -> R.drawable.ic_category_groceries
        Category.TRAVEL.id -> R.drawable.ic_category_travel
        Category.HOME.id -> R.drawable.ic_category_home
        Category.SPORTS.id -> R.drawable.ic_category_sports
        Category.ENTERTAINMENT.id -> R.drawable.ic_category_entertainment
        Category.SHOPPING.id -> R.drawable.ic_category_shopping
        Category.WORK.id -> R.drawable.ic_category_work
        Category.HEALTH.id -> R.drawable.ic_category_health
        Category.FINANCE.id -> R.drawable.ic_category_finance
        else -> null
    }

/** The category's hand-drawn icon, falling back to its emoji drawn at about the same size. */
@Composable
fun CategoryIcon(
    category: Category,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
) {
    val iconRes = category.drawnIconRes()
    if (iconRes != null) {
        DrawnIcon(iconRes, modifier, size)
    } else {
        Text(
            text = category.icon,
            fontSize = with(LocalDensity.current) { (size * EMOJI_SIZE_RATIO).toSp() },
            modifier = modifier,
        )
    }
}

/** Icon of a category tile: sparkles for all tasks, a blank tag for tasks without a category. */
@Composable
fun CategoryIcon(
    filter: CategoryFilter,
    categories: List<Category>,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
) {
    when (filter) {
        CategoryFilter.All -> {
            DrawnIcon(R.drawable.ic_category_all, modifier, size)
        }

        CategoryFilter.Uncategorized -> {
            DrawnIcon(R.drawable.ic_category_none, modifier, size)
        }

        is CategoryFilter.Of -> {
            val category = categories.find { it.id == filter.categoryId }
            // A category that isn't loaded yet or no longer exists gets the blank tag
            if (category != null) {
                CategoryIcon(category, modifier, size)
            } else {
                DrawnIcon(R.drawable.ic_category_none, modifier, size)
            }
        }
    }
}

@Composable
private fun DrawnIcon(
    @DrawableRes iconRes: Int,
    modifier: Modifier,
    size: Dp,
) {
    Image(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = modifier.size(size),
    )
}

private const val EMOJI_SIZE_RATIO = 0.8f
