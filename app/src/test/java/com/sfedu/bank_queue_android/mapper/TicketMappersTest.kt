package com.sfedu.bank_queue_android.mapper

import com.sfedu.bank_queue_android.model.Ticket
import com.sfedu.bank_queue_android.network.dto.TicketResponseDto
import org.junit.Test
import org.junit.Assert.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class TicketMappersTest {

    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @Test
    fun `toDomain parses scheduledAt and maps all fields`() {
        val iso = "2025-06-26T12:34:56+03:00"
        val dto = TicketResponseDto(
            id = 7L,
            userId = 42L,
            address = "ул. Ленина, 1",
            ticketType = "standard",
            ticket = "A100",
            scheduledAt = iso
        )

        val domain = dto.toDomain()

        assertEquals(7L, domain.id)
        assertEquals(42L, domain.userId)
        assertEquals("ул. Ленина, 1", domain.address)
        assertEquals("standard", domain.ticketType)
        assertEquals("A100", domain.ticket)
        assertEquals(OffsetDateTime.parse(iso, formatter), domain.scheduledAt)
    }

    @Test
    fun `toCreateDto formats scheduledAt and copies fields`() {
        val dt = OffsetDateTime.now()
        val domain = Ticket(
            id = null,
            userId = 0L,
            address = "пр. Мира, 5",
            ticketType = "vip",
            ticket = "B200",
            scheduledAt = dt
        )

        val dto = domain.toCreateDto()

        assertEquals("пр. Мира, 5", dto.address)
        assertEquals("vip", dto.ticketType)
        assertEquals(dt.format(formatter), dto.scheduledAt)
    }

    @Test
    fun `toUpdateDto formats scheduledAt and copies fields`() {
        val dt = OffsetDateTime.parse("2025-01-01T00:00:00Z", formatter)
        val domain = Ticket(
            id = 9L,
            userId = 1L,
            address = "ул. Пушкина, 10",
            ticketType = "standard",
            ticket = "C300",
            scheduledAt = dt
        )

        val dto = domain.toUpdateDto()

        assertEquals("ул. Пушкина, 10", dto.address)
        assertEquals("standard", dto.ticketType)
        assertEquals("2025-01-01T00:00:00Z", dto.scheduledAt)
    }
}
