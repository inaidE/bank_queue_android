package com.sfedu.bank_queue_android.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sfedu.bank_queue_android.model.User
import com.sfedu.bank_queue_android.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    nav: NavController,
    vm: UserViewModel = hiltViewModel()
) {
    val token by remember { vm::token }
    val profile by remember { vm::profile }
    val isLoading by remember { vm::isLoadingProfile }
    val error by remember { vm::errorMessage }

    // флаг, чтобы пропустить первый null
    var tokenInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        if (!tokenInitialized) {
            // помечаем, что мы уже “увидели” первый токен (даже если он null)
            tokenInitialized = true
        } else {
            // а теперь реагируем по-настоящему
            if (token.isNullOrBlank()) {
                nav.navigate("login") {
                    popUpTo("tickets") { inclusive = true }
                }
            } else {
                vm.loadProfile()
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            profile != null -> {
                ProfileContent(
                    profile = profile!!,
                    onEdit = { nav.navigate("profile/edit") },
                    onChangePassword = { nav.navigate("profile/change-password") },
                    onLogout = {
                        vm.logout {
                            nav.navigate("login") {
                                popUpTo("tickets") { inclusive = true }
                            }
                        }
                    }
                )
            }
            error != null -> {
                // Только показываем ошибку и кнопку Retry
                Column(
                    Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Не удалось загрузить профиль:\n$error",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { vm.loadProfile() }) {
                        Text("Повторить")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: User,
    onEdit: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Имя: ${profile.name}", style = MaterialTheme.typography.bodyLarge)
        Text("Логин: ${profile.login}", style = MaterialTheme.typography.bodyLarge)
        Text("Email: ${profile.email}", style = MaterialTheme.typography.bodyLarge)
        Text("Телефон: ${profile.phoneNumber}", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onEdit, Modifier.fillMaxWidth()) {
            Text("Редактировать профиль")
        }
        Button(onClick = onChangePassword, Modifier.fillMaxWidth()) {
            Text("Сменить пароль")
        }
        Button(
            onClick = onLogout,
            Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Выйти", color = MaterialTheme.colorScheme.onError)
        }
    }
}
