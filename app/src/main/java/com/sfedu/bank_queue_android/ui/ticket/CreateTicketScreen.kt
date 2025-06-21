package com.sfedu.bank_queue_android.ui.ticket

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sfedu.bank_queue_android.viewmodel.TicketViewModel

@Composable
fun CreateTicketScreen(
    viewModel: TicketViewModel = hiltViewModel(),
    onCreated: (Int) -> Unit
) {
    var address by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var scheduledAt by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // берём флаг обработки из ViewModel
    val isProcessing by remember { derivedStateOf { viewModel.isProcessing } }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Text("Create Ticket", style = MaterialTheme.typography.titleLarge)
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
            label = { Text("Ticket Type") },
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
                error = null
                viewModel.create(address, type, scheduledAt) { result ->
                    result.fold(
                        onSuccess = { ticket ->
                            ticket.id?.let { onCreated(it.toInt()) }
                        },
                        onFailure = {
                            error = it.message
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isProcessing
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
                Text("Creating…")
            } else {
                Text("Create")
            }
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}