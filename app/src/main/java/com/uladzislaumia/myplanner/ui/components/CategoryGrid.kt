package com.uladzislaumia.myplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.ui.text.title
import com.uladzislaumia.myplanner.ui.theme.MyPlannerTheme
import com.uladzislaumia.myplanner.ui.viewmodel.CategoryFilter

/**
 * Two-column grid of categories: all tasks, every category, tasks without a category and an "add" tile.
 * Each tile counts all its tasks, completed ones included.
 */
@Composable
fun CategoryGrid(
    categories: List<Category>,
    items: List<PlannerItem>,
    onCategoryClick: (CategoryFilter) -> Unit,
    onAddCategoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val filters =
        listOf(CategoryFilter.All) + categories.map { CategoryFilter.Of(it.id) } + CategoryFilter.Uncategorized

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        // Bottom padding keeps the last row clear of the floating action button
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        items(filters, key = { it.toString() }) { filter ->
            CategoryTile(
                filter = filter,
                categories = categories,
                taskCount = items.count(filter::matches),
                onClick = { onCategoryClick(filter) },
            )
        }
        item(key = "add") {
            AddCategoryTile(onClick = onAddCategoryClick)
        }
    }
}

@Composable
private fun CategoryTile(
    filter: CategoryFilter,
    categories: List<Category>,
    taskCount: Int,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val tint =
        when (filter) {
            CategoryFilter.All -> colorScheme.primary
            CategoryFilter.Uncategorized -> colorScheme.secondaryContainer
            is CategoryFilter.Of -> categories.find { it.id == filter.categoryId }?.color() ?: colorScheme.primary
        }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth().height(TILE_HEIGHT.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(56.dp),
            ) {
                Surface(
                    shape = CircleShape,
                    color = tint.copy(alpha = TINT_ALPHA),
                    modifier = Modifier.fillMaxSize(),
                ) {}
                CategoryIcon(filter, categories, size = 36.dp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = filter.title(categories),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = pluralStringResource(R.plurals.categories_tasks_count, taskCount, taskCount),
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AddCategoryTile(onClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        border = BorderStroke(1.5.dp, primary),
        modifier = Modifier.fillMaxWidth().height(TILE_HEIGHT.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.categories_add),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = primary,
            )
        }
    }
}

private fun Category.color(): Color? =
    try {
        Color(colorHex.toColorInt())
    } catch (_: IllegalArgumentException) {
        null
    }

private const val TILE_HEIGHT = 140
private const val TINT_ALPHA = 0.25f

@Preview(showBackground = true)
@Composable
fun CategoryGridPreview() {
    MyPlannerTheme {
        CategoryGrid(
            categories = Category.DEFAULT_CATEGORIES,
            items = emptyList(),
            onCategoryClick = {},
            onAddCategoryClick = {},
        )
    }
}
