package com.uladzislaumia.myplanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.uladzislaumia.myplanner.ui.navigation.AppNavGraph
import com.uladzislaumia.myplanner.ui.theme.MyPlannerTheme
import com.uladzislaumia.myplanner.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkGooglePlayServices()
        requestNotificationPermission()
        enableEdgeToEdge()
        setContent {
            MyPlannerTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                val currentUser by authViewModel.authState.collectAsState(initial = authViewModel.currentUser)

                AppNavGraph(
                    navController = navController,
                    currentUser = currentUser,
                    authViewModel = authViewModel,
                )
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE,
                )
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
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }
}
