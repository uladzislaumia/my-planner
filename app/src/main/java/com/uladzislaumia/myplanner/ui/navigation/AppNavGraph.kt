package com.uladzislaumia.myplanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.ui.screens.HomeScreen
import com.uladzislaumia.myplanner.ui.screens.LoginScreen
import com.uladzislaumia.myplanner.ui.screens.SignUpScreen
import com.uladzislaumia.myplanner.ui.viewmodel.AuthViewModel
import com.uladzislaumia.myplanner.ui.viewmodel.MainViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    currentUser: User?,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val startDestination: Route = if (currentUser != null) Route.Home else Route.Login

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate(Route.Login) {
                popUpTo(0)
            }
        } else {
            navController.navigate(Route.Home) {
                popUpTo(0)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Route.Login> {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignUp = {
                    navController.navigate(Route.SignUp)
                }
            )
        }
        composable<Route.SignUp> {
            SignUpScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable<Route.Home> {
            HomeScreen(
                mainViewModel = mainViewModel,
                onLogout = {
                    authViewModel.logout()
                }
            )
        }
    }
}
