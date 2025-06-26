package com.sfedu.bank_queue_android.viewmodel

import com.sfedu.bank_queue_android.model.Ticket
import com.sfedu.bank_queue_android.repository.TicketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import java.time.OffsetDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class TicketViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var vm: TicketViewModel
    private val repo = mockk<TicketRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        vm = TicketViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTickets success updates tickets and resets loading`() = runTest {
        val now = OffsetDateTime.parse("2025-01-01T10:00:00Z")
        val items = listOf(
            Ticket(
                id         = 1L,
                userId     = 42L,
                address    = "ул. Ленина, 1",
                ticketType = "standard",
                ticket     = "A123",
                scheduledAt= now
            )
        )
        coEvery { repo.getAll() } returns items

        vm.loadTickets()
        advanceUntilIdle()

        assertEquals(false, vm.isLoadingList)
        assertEquals(items, vm.tickets)
        assertEquals(null, vm.errorMessage)
    }

    @Test
    fun `loadTickets failure sets errorMessage`() = runTest {
        coEvery { repo.getAll() } throws RuntimeException("network error")

        vm.loadTickets()
        advanceUntilIdle()

        assertEquals(false, vm.isLoadingList)
        assertEquals("network error", vm.errorMessage)
    }

    @Test
    fun `loadDetail success updates selected and resets loadingDetail`() = runTest {
        val now = OffsetDateTime.parse("2025-02-02T12:00:00Z")
        val ticket = Ticket(
            id          = 2L,
            userId      = 7L,
            address     = "пр. Мира, 5",
            ticketType  = "vip",
            ticket      = "B456",
            scheduledAt = now
        )
        coEvery { repo.getById(2) } returns ticket

        vm.loadDetail(2)
        advanceUntilIdle()

        assertFalse(vm.isLoadingDetail)
        assertEquals(ticket, vm.selected)
        assertNull(vm.errorMessage)
    }

    @Test
    fun `loadDetail failure sets errorMessage`() = runTest {
        coEvery { repo.getById(5) } throws RuntimeException("not found")

        vm.loadDetail(5)
        advanceUntilIdle()

        assertFalse(vm.isLoadingDetail)
        assertEquals("not found", vm.errorMessage)
    }

    @Test
    fun `create success invokes callback and resets processing`() = runTest {
        val nowStr = "2025-03-03T15:00:00Z"
        val created = Ticket(
            id          = 3L,
            userId      = 8L,
            address     = "ул. Пушкина, 10",
            ticketType  = "standard",
            ticket      = "C789",
            scheduledAt = OffsetDateTime.parse(nowStr)
        )
        coEvery { repo.create("ул. Пушкина, 10", "standard", nowStr) } returns Result.success(created)

        var callbackResult: Result<Ticket>? = null
        vm.create("ул. Пушкина, 10", "standard", nowStr) {
            callbackResult = it
        }
        advanceUntilIdle()

        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isSuccess)
        assertEquals(created, callbackResult!!.getOrNull())
    }

    @Test
    fun `create failure invokes callback with failure and resets processing`() = runTest {
        val nowStr = "2025-04-04T16:00:00Z"
        coEvery { repo.create(any(), any(), any()) } returns Result.failure(Exception("create fail"))

        var callbackResult: Result<Ticket>? = null
        vm.create("x", "y", nowStr) {
            callbackResult = it
        }
        advanceUntilIdle()

        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isFailure)
        assertEquals("create fail", callbackResult!!.exceptionOrNull()?.message)
    }

    @Test
    fun `update success invokes callback and resets processing`() = runTest {
        val nowStr = "2025-05-05T17:00:00Z"
        val updated = Ticket(
            id          = 4L,
            userId      = 9L,
            address     = "ул. Лермонтова, 15",
            ticketType  = "vip",
            ticket      = "D012",
            scheduledAt = OffsetDateTime.parse(nowStr)
        )
        coEvery { repo.update(4, "ул. Лермонтова, 15", "vip", nowStr) } returns Result.success(updated)

        var callbackResult: Result<Ticket>? = null
        vm.update(4, "ул. Лермонтова, 15", "vip", nowStr) {
            callbackResult = it
        }
        advanceUntilIdle()

        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isSuccess)
        assertEquals(updated, callbackResult!!.getOrNull())
    }

    @Test
    fun `update failure invokes callback with failure and resets processing`() = runTest {
        coEvery { repo.update(any(), any(), any(), any()) } returns Result.failure(Exception("update fail"))

        var callbackResult: Result<Ticket>? = null
        vm.update(1, "a", "b", "c") {
            callbackResult = it
        }
        advanceUntilIdle()

        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isFailure)
        assertEquals("update fail", callbackResult!!.exceptionOrNull()?.message)
    }

    @Test
    fun `delete success invokes callback and resets processing`() = runTest {
        coEvery { repo.delete(10) } returns Result.success(Unit)

        var callbackResult: Result<Unit>? = null
        vm.delete(10) {
            callbackResult = it
        }
        advanceUntilIdle()

        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isSuccess)
    }

    @Test
    fun `delete failure invokes callback with failure and resets processing`() = runTest {
        coEvery { repo.delete(any()) } returns Result.failure(Exception("delete fail"))

        var callbackResult: Result<Unit>? = null
        vm.delete(20) {
            callbackResult = it
        }
        advanceUntilIdle()

        assertFalse(vm.isProcessing)
        assertTrue(callbackResult!!.isFailure)
        assertEquals("delete fail", callbackResult!!.exceptionOrNull()?.message)
    }
}
