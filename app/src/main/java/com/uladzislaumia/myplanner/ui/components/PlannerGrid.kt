package com.uladzislaumia.myplanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.domain.model.Category
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.ui.theme.MyPlannerTheme

@Composable
fun PlannerGrid(
    items: List<PlannerItem>,
    onItemClick: (PlannerItem) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            PlannerCard(
                data = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlannerGridPreview() {
    MyPlannerTheme {
        val demoCount = 4
        val demoItems = List(demoCount) { i ->
            PlannerItem(
                id = i.toString(),
                title = "Item $i",
                description = "Description $i",
                categoryId = Category.GROCERIES.id,
                groupId = null,
                assigneeId = null,
                dueDate = null,
                priority = Priority.MEDIUM
            )
        }
        PlannerGrid(items = demoItems)
    }
}
