package com.uladzislaumia.myplanner.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.ui.components.CategoryGrid
import com.uladzislaumia.myplanner.ui.components.PlannerList
import com.uladzislaumia.myplanner.ui.components.UserAvatar
import com.uladzislaumia.myplanner.ui.components.WeekStrip
import com.uladzislaumia.myplanner.ui.components.warmBackground
import com.uladzislaumia.myplanner.ui.dialogs.CreateCategoryDialog
import com.uladzislaumia.myplanner.ui.sheets.AccountBottomSheet
import com.uladzislaumia.myplanner.ui.sheets.AddTaskBottomSheet
import com.uladzislaumia.myplanner.ui.sheets.EditTaskBottomSheet
import com.uladzislaumia.myplanner.ui.text.firstName
import com.uladzislaumia.myplanner.ui.viewmodel.CategoryFilter
import com.uladzislaumia.myplanner.ui.viewmodel.MainUiState
import com.uladzislaumia.myplanner.ui.viewmodel.MainViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    user: User?,
    onLogout: () -> Unit,
    onCategoryClick: (CategoryFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val categories by mainViewModel.categories.collectAsState()
    val selectedDate by mainViewModel.selectedDate.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    var showAccountSheet by remember { mutableStateOf(false) }
    var showCreateCategoryDialog by remember { mutableStateOf(false) }
    var selectedItemForEdit by remember { mutableStateOf<PlannerItem?>(null) }
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.CALENDAR) }

    Scaffold(
        modifier = modifier.fillMaxSize().warmBackground(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =
                            when (selectedTab) {
                                HomeTab.CALENDAR -> greeting(user)
                                HomeTab.CATEGORIES -> stringResource(R.string.home_tab_categories)
                            },
                        fontWeight = FontWeight.Bold,
                        // A long name is cut at the end, the greeting itself stays whole
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = { showAccountSheet = true }) {
                        UserAvatar(user = user, contentDescription = stringResource(R.string.account_title))
                    }
                },
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
        bottomBar = {
            NavigationBar {
                HomeTab.entries.forEach { tab ->
                    HomeTabItem(
                        tab = tab,
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            if (selectedTab == HomeTab.CALENDAR) {
                // Week calendar strip, a day is always selected
                WeekStrip(
                    selectedDate = selectedDate,
                    onDateClick = mainViewModel::selectDate,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val state = uiState) {
                    is MainUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is MainUiState.Success -> {
                        when (selectedTab) {
                            HomeTab.CALENDAR -> {
                                PlannerList(
                                    sections = state.calendarSections,
                                    selectedDate = selectedDate,
                                    onItemClick = { selectedItemForEdit = it },
                                )
                            }

                            HomeTab.CATEGORIES -> {
                                CategoryGrid(
                                    categories = categories,
                                    items = state.items,
                                    onCategoryClick = onCategoryClick,
                                    onAddCategoryClick = { showCreateCategoryDialog = true },
                                )
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
        }

        if (showAddSheet) {
            AddTaskBottomSheet(
                onDismiss = { showAddSheet = false },
                categories = categories,
                onConfirm = { title, description, priority, categoryId, dueDate ->
                    mainViewModel.addItem(title, description, priority, categoryId, dueDate)
                    showAddSheet = false
                },
            )
        }

        if (showAccountSheet) {
            AccountBottomSheet(
                user = user,
                onDismiss = { showAccountSheet = false },
                onLogout = {
                    showAccountSheet = false
                    onLogout()
                },
            )
        }

        if (showCreateCategoryDialog) {
            CreateCategoryDialog(
                onDismiss = { showCreateCategoryDialog = false },
                onConfirm = { name, icon, colorHex ->
                    mainViewModel.addCategory(name, icon, colorHex)
                    showCreateCategoryDialog = false
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

/**
 * Bottom bar tab: the whole area (icon and label) is clickable and shows the ripple,
 * unlike NavigationBarItem, whose ripple stays inside the icon indicator.
 */
@Composable
private fun RowScope.HomeTabItem(
    tab: HomeTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier =
            Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .selectable(selected = selected, onClick = onClick, role = Role.Tab)
                .padding(vertical = 8.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .clip(CircleShape)
                    .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                    .padding(horizontal = 20.dp, vertical = 4.dp),
        ) {
            // Hand-drawn icons keep their own colors, the unselected one is faded out
            Icon(
                painter = painterResource(tab.iconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier =
                    Modifier
                        .size(28.dp)
                        .alpha(if (selected) 1f else UNSELECTED_TAB_ALPHA),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(tab.labelRes),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color =
                if (selected) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = UNSELECTED_TAB_ALPHA)
                },
        )
    }
}

/** "Good morning, Anna" by the time of day, with only the first name to keep it short. */
@Composable
private fun greeting(user: User?): String {
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val firstName = user?.firstName.orEmpty()
    if (firstName.isEmpty()) return stringResource(R.string.app_name)
    val greetingRes =
        when (hour) {
            in MORNING_START_HOUR until AFTERNOON_START_HOUR -> R.string.home_greeting_morning
            in AFTERNOON_START_HOUR until EVENING_START_HOUR -> R.string.home_greeting_afternoon
            else -> R.string.home_greeting_evening
        }
    return stringResource(greetingRes, firstName)
}

private enum class HomeTab(
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int,
) {
    CALENDAR(R.string.home_tab_calendar, R.drawable.ic_tab_calendar),
    CATEGORIES(R.string.home_tab_categories, R.drawable.ic_tab_categories),
}

private const val UNSELECTED_TAB_ALPHA = 0.55f
private const val MORNING_START_HOUR = 5
private const val AFTERNOON_START_HOUR = 12
private const val EVENING_START_HOUR = 18
