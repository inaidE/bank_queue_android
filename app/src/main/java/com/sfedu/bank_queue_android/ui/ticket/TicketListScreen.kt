package com.sfedu.bank_queue_android.ui.ticket

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sfedu.bank_queue_android.viewmodel.TicketViewModel

@Composable
fun TicketListScreen(
    nav: NavController,
    vm: TicketViewModel = hiltViewModel(),
    onClick: (Int) -> Unit
) {
    val tickets = vm.tickets
    LaunchedEffect(Unit) { vm.loadTickets() }

    if (tickets.isEmpty())
        Text("У вас нет тикетов", textAlign = TextAlign.Center)

    LazyColumn(Modifier.fillMaxSize()) {
        items(tickets) { t ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { nav.navigate("ticket/${t.id}") }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("№ ${t.ticket}", style = MaterialTheme.typography.titleMedium)
                    Text("Тип операции: ${t.ticketType}")
                    Text("Адрес отделения: ${t.address}")
                    Text("Дата: ${t.scheduledAt}")
                }
            }
        }
    }
}