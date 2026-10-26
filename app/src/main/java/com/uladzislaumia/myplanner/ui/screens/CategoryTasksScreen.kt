package com.uladzislaumia.myplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.ui.components.CategoryIcon
import com.uladzislaumia.myplanner.ui.components.PlannerCard
import com.uladzislaumia.myplanner.ui.components.warmBackground
import com.uladzislaumia.myplanner.ui.sheets.AddTaskBottomSheet
import com.uladzislaumia.myplanner.ui.sheets.EditTaskBottomSheet
import com.uladzislaumia.myplanner.ui.text.title
import com.uladzislaumia.myplanner.ui.viewmodel.CategoryFilter
import com.uladzislaumia.myplanner.ui.viewmodel.MainUiState
import com.uladzislaumia.myplanner.ui.viewmodel.MainViewModel

/** Tasks of one category tile, completed ones mixed in; a new task gets this category preselected. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTasksScreen(
    filter: CategoryFilter,
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val categories by mainViewModel.categories.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    var selectedItemForEdit by remember { mutableStateOf<PlannerItem?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize().warmBackground(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CategoryIcon(filter, categories, size = 28.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = filter.title(categories),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.home_add_task))
            }
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            when (val state = uiState) {
                is MainUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is MainUiState.Success -> {
                    val items = state.items.filter(filter::matches)
                    if (items.isEmpty()) {
                        Text(
                            text = stringResource(R.string.calendar_no_tasks),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = EMPTY_TEXT_ALPHA),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                        LazyColumn(
                            // Bottom padding keeps the last card clear of the floating action button
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(items, key = { it.id }) { item ->
                                PlannerCard(
                                    data = item,
                                    onClick = { selectedItemForEdit = item },
                                )
                            }
                        }
                    }
                }

                is MainUiState.Error -> {
                    Text(
                        text = stringResource(R.string.home_error_with_message, state.message),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }

        if (showAddSheet) {
            AddTaskBottomSheet(
                onDismiss = { showAddSheet = false },
                categories = categories,
                initialCategoryId = (filter as? CategoryFilter.Of)?.categoryId,
                onConfirm = { title, description, priority, categoryId, dueDate ->
                    mainViewModel.addItem(title, description, priority, categoryId, dueDate)
                    showAddSheet = false
                },
            )
        }

        selectedItemForEdit?.let { item ->
            EditTaskBottomSheet(
                item = item,
                onDismiss = { selectedItemForEdit = null },
                categories = categories,
                onSave = mainViewModel::updateItem,
                onDelete = { itemId ->
                    mainViewModel.deleteItem(itemId)
                    selectedItemForEdit = null
                },
            )
        }
    }
}

private const val EMPTY_TEXT_ALPHA = 0.7f
