package com.sfedu.bank_queue_android.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sfedu.bank_queue_android.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    nav: NavController,
    vm: UserViewModel = hiltViewModel()
) {
    // При старте экрана грузим профиль
    LaunchedEffect(Unit) {
        vm.loadProfile()
    }

    val user      = vm.profile
    val isLoading = vm.isLoadingProfile
    val error     = vm.errorMessage

    Box(Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
            return@Box
        }

        user?.let { u ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Имя: ${u.name}",    style = MaterialTheme.typography.bodyLarge)
                Text("Логин: ${u.login}", style = MaterialTheme.typography.bodyLarge)
                Text("Email: ${u.email}", style = MaterialTheme.typography.bodyLarge)
                Text("Телефон: ${u.phoneNumber}", style = MaterialTheme.typography.bodyLarge)

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { nav.navigate("profile/edit") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Редактировать профиль")
                }

                Button(
                    onClick = { nav.navigate("profile/change-password") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сменить пароль")
                }

                Button(
                    onClick = {
                        vm.logout {
                            // после логаута вернуться на экран логина
                            nav.navigate("login") {
                                popUpTo("tickets") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Выйти", color = MaterialTheme.colorScheme.onError)
                }

                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
