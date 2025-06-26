package com.sfedu.bank_queue_android.mapper

import com.sfedu.bank_queue_android.model.User
import com.sfedu.bank_queue_android.network.dto.UserResponseDto
import org.junit.Test
import org.junit.Assert.*

class UserMappersTest {

    @Test
    fun `toDomain maps all fields correctly`() {
        val dto = UserResponseDto(
            id = 10,
            name = "Иван",
            login = "ivan10",
            email = "ivan@example.com",
            phoneNumber = "+70000000000"
        )

        val domain = dto.toDomain()

        assertEquals(10, domain.id)
        assertEquals("Иван", domain.name)
        assertEquals("ivan10", domain.login)
        assertEquals("ivan@example.com", domain.email)
        assertEquals("+70000000000", domain.phoneNumber)
    }

    @Test
    fun `toCreateDto includes password and copies fields`() {
        val user = User(
            id = 0,
            name = "Пётр",
            login = "petr5",
            email = "petr@example.com",
            phoneNumber = "+71111111111"
        )
        val pwd = "secret123"

        val dto = user.toCreateDto(password = pwd)

        assertEquals("Пётр", dto.name)
        assertEquals("petr5", dto.login)
        assertEquals("petr@example.com", dto.email)
        assertEquals("secret123", dto.password)
        assertEquals("+71111111111", dto.phoneNumber)
    }

    @Test
    fun `toUpdateDto copies only name email and phone`() {
        val user = User(
            id = 5,
            name = "Ольга",
            login = "olga",
            email = "olga@example.com",
            phoneNumber = "+72222222222"
        )

        val dto = user.toUpdateDto()

        assertEquals("Ольга", dto.name)
        assertEquals("olga@example.com", dto.email)
        assertEquals("+72222222222", dto.phoneNumber)
    }
}
