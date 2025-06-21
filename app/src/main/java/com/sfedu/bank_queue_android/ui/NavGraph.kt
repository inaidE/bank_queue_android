package com.sfedu.bank_queue_android.ui

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
import androidx.compose.runtime.LaunchedEffect
import com.sfedu.bank_queue_android.viewmodel.UserViewModel

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
                Text("Auth")
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Login") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("login")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Register") },
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
                    title = { Text("Bank Queue") },
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
                        label = { Text("Create") },
                        selected = false,
                        onClick = { navController.navigate("create") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Tickets") },
                        label = { Text("Tickets") },
                        selected = false,
                        onClick = { navController.navigate("tickets") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Profile") },
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
                composable("create") { CreateTicketScreen(hiltViewModel(), onCreated = { id -> navController.navigate("ticket/$id") }) }
                composable("tickets") { TicketListScreen(nav = navController, hiltViewModel(), onClick = { id -> navController.navigate("ticket/$id") }) }
                composable("profile") {
                    ProfileScreen(navController, hiltViewModel())
                }
                composable( "ticket/{id}") { backStackEntry ->
                    // 1) вытаскиваем сам id
                    val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                    // 2) сразу передаём в экран чистое число и, при желании, navController
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

