package com.sfedu.bank_queue_android.ui.ticket

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.sfedu.bank_queue_android.viewmodel.TicketViewModel

@Composable
fun TicketDetailScreen(
    id: Int,
    nav: NavController,
    vm: TicketViewModel = hiltViewModel()
) {
    val ticket = vm.selected
    LaunchedEffect(id) { vm.loadDetail(id) }

    var address by remember { mutableStateOf(ticket?.address.orEmpty()) }
    var type by remember { mutableStateOf(ticket?.ticketType.orEmpty()) }
    var scheduledAt by remember { mutableStateOf(ticket?.scheduledAt.toString()) }

    var isProcessing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    if (vm.isLoadingDetail) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    ticket?.let { t ->
        Column(Modifier.padding(16.dp)) {
            Text("Ticket № ${t.ticket}", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Type") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = scheduledAt,
                onValueChange = { scheduledAt = it },
                label = { Text("Scheduled At") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    isProcessing = true
                    error = null
                    vm.update(
                        id = t.id!!.toInt(),
                        address = address,
                        ticketType = type,
                        scheduledAt = scheduledAt
                    ) { result ->
                        result
                            .onSuccess {
                                // перезагрузим детали
                                vm.loadDetail(t.id.toInt())
                            }
                            .onFailure {
                                error = it.message
                            }
                        isProcessing = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Update")
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    vm.delete(t.id!!.toInt()) { result ->
                        result
                            .onSuccess { nav.popBackStack() }
                            .onFailure { error = it.message }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete", color = MaterialTheme.colorScheme.onError)
            }

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
