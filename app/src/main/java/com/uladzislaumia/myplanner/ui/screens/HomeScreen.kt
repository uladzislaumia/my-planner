package com.uladzislaumia.myplanner.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.ui.components.CategoryCarousel
import com.uladzislaumia.myplanner.ui.components.PlannerGrid
import com.uladzislaumia.myplanner.ui.dialogs.AddTaskDialog
import com.uladzislaumia.myplanner.ui.dialogs.CreateCategoryDialog
import com.uladzislaumia.myplanner.ui.dialogs.EditTaskDialog
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
    val categories by mainViewModel.categories.collectAsState()
    val selectedCategoryId by mainViewModel.selectedCategoryId.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showCreateCategoryDialog by remember { mutableStateOf(false) }
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val allItems = (uiState as? MainUiState.Success)?.items ?: emptyList()

            // Large Category Cards Carousel
            CategoryCarousel(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                allItems = allItems,
                onSelectCategory = { mainViewModel.selectCategory(it) },
                onAddCategoryClick = { showCreateCategoryDialog = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
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
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddTaskDialog(
                onDismiss = { showAddDialog = false }
            ) { title, description, priority, categoryId ->
                mainViewModel.addItem(title, description, priority, categoryId)
                showAddDialog = false
            }
        }

        if (showCreateCategoryDialog) {
            CreateCategoryDialog(
                onDismiss = { showCreateCategoryDialog = false },
                onConfirm = { name, icon, colorHex ->
                    mainViewModel.addCategory(name, icon, colorHex)
                    showCreateCategoryDialog = false
                }
            )
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
