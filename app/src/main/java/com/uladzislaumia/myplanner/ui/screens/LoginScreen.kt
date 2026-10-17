package com.uladzislaumia.myplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.ui.viewmodel.AuthUiState
import com.uladzislaumia.myplanner.ui.viewmodel.AuthViewModel
import androidx.compose.ui.res.stringResource
import com.uladzislaumia.myplanner.R
import com.uladzislaumia.myplanner.ui.text.asString

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.auth_login_title),
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        viewModel.clearError()
                    },
                    label = { Text(stringResource(R.string.auth_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        viewModel.clearError()
                    },
                    label = { Text(stringResource(R.string.auth_password)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                when (val state = uiState) {
                    is AuthUiState.Error -> {
                        Text(
                            text = state.message.asString(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else -> {}
                }

                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.auth_login_button))
                    }
                }

                TextButton(onClick = onNavigateToSignUp) {
                    Text(stringResource(R.string.auth_login_to_signup))
                }
            }
        }
    }
}
