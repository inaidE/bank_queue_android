package com.sfedu.bank_queue_android.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.sfedu.bank_queue_android.network.dto.TicketCreateDto
import com.sfedu.bank_queue_android.network.dto.TicketResponseDto
import com.sfedu.bank_queue_android.network.dto.TicketUpdateDto
import com.sfedu.bank_queue_android.model.Ticket
import java.time.Instant

// из ResponseDto в Domain
@RequiresApi(Build.VERSION_CODES.O)
fun TicketResponseDto.toDomain() = Ticket(
     id = this.id,
     userId = this.userId,
     address = this.address,
     ticketType = this.ticketType,
     ticket = this.ticket,
     scheduledAt = Instant.parse(scheduledAt),
)

// из Domain в CreateDto
fun Ticket.toCreateDto() = TicketCreateDto(
     address = this.address,
     ticketType = this.ticketType,
     scheduledAt = this.scheduledAt.toString()
)

// из Domain в UpdateDto
fun Ticket.toUpdateDto() = TicketUpdateDto(
     address = this.address,
     ticketType = this.ticketType,
     scheduledAt = this.scheduledAt.toString()
)