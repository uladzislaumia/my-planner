package com.uladzislaumia.myplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import com.uladzislaumia.myplanner.domain.model.Priority
import com.uladzislaumia.myplanner.ui.screens.LoginScreen
import com.uladzislaumia.myplanner.ui.screens.SignUpScreen
import com.uladzislaumia.myplanner.ui.theme.MyPlannerTheme
import com.uladzislaumia.myplanner.ui.viewmodel.AuthViewModel
import com.uladzislaumia.myplanner.ui.viewmodel.MainUiState
import com.uladzislaumia.myplanner.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkGooglePlayServices()
        enableEdgeToEdge()
        setContent {
            MyPlannerTheme {
                val mainViewModel: MainViewModel = hiltViewModel()
                val authViewModel: AuthViewModel = hiltViewModel()

                val uiState by mainViewModel.uiState.collectAsState()
                val currentUser by authViewModel.authState.collectAsState(initial = authViewModel.currentUser)

                // Local navigation state for auth screens
                var currentScreen by remember { mutableStateOf("login") }

                val isLoggedIn = currentUser != null

                if (isLoggedIn) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = { Text("My Planner") },
                                actions = {
                                    TextButton(onClick = { authViewModel.logout() }) {
                                        Text("Logout", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                            when (val state = uiState) {
                                is MainUiState.Loading -> {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                                is MainUiState.Success -> {
                                    PlannerGrid(items = state.items)
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
                    }
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        if (currentScreen == "login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateToSignUp = { currentScreen = "signup" },
                                modifier = Modifier.padding(innerPadding)
                            )
                        } else {
                            SignUpScreen(
                                viewModel = authViewModel,
                                onNavigateToLogin = { currentScreen = "login" },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            Timber.w("Google Play Services result code: $resultCode")
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)?.show()
            } else {
                Timber.e("This device does not support Google Play Services.")
            }
        }
    }

    companion object {
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    }
}

@Composable
fun PlannerGrid(items: List<PlannerItem>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            PlannerCard(data = item)
        }
    }
}

@Composable
fun PlannerCard(data: PlannerItem) {
    val priorityColor = when (data.priority) {
        Priority.HIGH -> Color.Red
        Priority.MEDIUM -> Color.Yellow
        Priority.LOW -> Color.Green
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
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
                    text = "Завершено",
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
        val demoItems = List(4) { i ->
            PlannerItem(
                id = "$i",
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
