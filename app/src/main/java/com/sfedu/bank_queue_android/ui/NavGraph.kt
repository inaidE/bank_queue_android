package com.sfedu.bank_queue_android.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sfedu.bank_queue_android.ui.auth.LoginScreen
import com.sfedu.bank_queue_android.ui.auth.RegisterScreen
import com.sfedu.bank_queue_android.ui.profile.ProfileScreen
import com.sfedu.bank_queue_android.ui.ticket.CreateTicketScreen
import com.sfedu.bank_queue_android.ui.ticket.TicketDetailScreen
import com.sfedu.bank_queue_android.ui.ticket.TicketListScreen
import kotlinx.coroutines.launch
import androidx.compose.material3.TopAppBar
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sfedu.bank_queue_android.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val vm: UserViewModel = hiltViewModel()
    val token = vm.token

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Аутентификация")
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Авторизация") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("login")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Регистрация") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("register")
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Банковская очередь") },
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = "Create") },
                        label = { Text("Создать тикет") },
                        selected = false,
                        onClick = { navController.navigate("create") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Tickets") },
                        label = { Text("Мои тикеты") },
                        selected = false,
                        onClick = { navController.navigate("tickets") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Профиль") },
                        selected = false,
                        onClick = { navController.navigate("profile") }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = if (token.isNullOrBlank()) "login" else "tickets",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("login") {
                    LoginScreen(
                        nav = navController,
                        onSuccess = {
                            navController.navigate("profile") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
                composable("register") { RegisterScreen(nav = navController, onSuccess = { navController.navigateUp() }) }
                composable("create") { CreateTicketScreen(hiltViewModel(), onCreated = { navController.navigate("tickets") }) }
                composable("tickets") { TicketListScreen(
                    hiltViewModel(),
                    onClick = { id -> navController.navigate("ticket/$id") }) }
                composable("profile") {
                    ProfileScreen(navController, hiltViewModel())
                }
                composable(
                    route = "ticket/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id")!!
                    TicketDetailScreen(
                        id = id,
                        nav = navController,
                        vm = hiltViewModel()
                    )
                }
            }
        }
    }
}

