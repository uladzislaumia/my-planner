package com.uladzislaumia.myplanner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.ui.dialogs.AddTaskDialog
import com.uladzislaumia.myplanner.ui.dialogs.EditTaskDialog
import com.uladzislaumia.myplanner.ui.theme.MyPlannerTheme
import com.uladzislaumia.myplanner.ui.viewmodel.MainUiState
import com.uladzislaumia.myplanner.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by mainViewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedItemForEdit by remember { mutableStateOf<PlannerItem?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("My Planner") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is MainUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is MainUiState.Success -> {
                    PlannerGrid(
                        items = state.items,
                        onItemClick = { selectedItemForEdit = it }
                    )
                }
                is MainUiState.Error -> {
                    Text(
                        text = "Ошибка: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        if (showAddDialog) {
            AddTaskDialog(
                onDismiss = { showAddDialog = false }
            ) { title, description, priority ->
                mainViewModel.addItem(title, description, priority)
                showAddDialog = false
            }
        }

        selectedItemForEdit?.let { item ->
            EditTaskDialog(
                item = item,
                onDismiss = { selectedItemForEdit = null },
                onSave = { updatedItem ->
                    mainViewModel.updateItem(updatedItem)
                    selectedItemForEdit = null
                }
            ) { itemId ->
                mainViewModel.deleteItem(itemId)
                selectedItemForEdit = null
            }
        }
    }
}

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

@Composable
fun PlannerCard(
    data: PlannerItem,
    onClick: () -> Unit = {},
) {
    val priorityColor = when (data.priority) {
        Priority.HIGH -> Color.Red
        Priority.MEDIUM -> Color.Yellow
        Priority.LOW -> Color.Green
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = CircleShape,
                    color = priorityColor
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            if (data.isCompleted) {
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
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
                categoryId = null,
                groupId = null,
                assigneeId = null,
                dueDate = null,
                priority = Priority.MEDIUM
            )
        }
        PlannerGrid(items = demoItems)
    }
}
