package com.sfedu.bank_queue_android.viewmodel

import com.sfedu.bank_queue_android.model.User
import com.sfedu.bank_queue_android.repository.AuthRepository
import com.sfedu.bank_queue_android.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var vm: UserViewModel
    private val userRepo = mockk<UserRepository>()
    private val authRepo = mockk<AuthRepository>()

    @Before
    fun setup() {
        // Подменяем главный диспетчер
        Dispatchers.setMain(dispatcher)
        // Мокаем flow с токеном
        every { authRepo.getToken() } returns flowOf("token123")
        // Создаём ViewModel — в init сразу подпишется на getToken()
        vm = UserViewModel(userRepo, authRepo)
        // Прокручиваем корутины, чтобы init-запрос токена успел выполниться
        runTest { advanceUntilIdle() }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `init collects token`() {
        assertEquals("token123", vm.token)
    }

    @Test
    fun `loadProfile success sets profile and clears loading`() = runTest {
        val user = User(7L, "Ivan", "ivan2004", "ivan@gmail.com", "+79001233456")
        coEvery { userRepo.getProfile() } returns Result.success(user)

        vm.loadProfile()
        advanceUntilIdle()

        assertFalse(vm.isLoadingProfile)
        assertEquals(user, vm.profile)
        assertNull(vm.errorMessage)
    }

    @Test
    fun `loadProfile unauthorized triggers logout`() = runTest {
        // Simulate 401 error
        val httpEx = mockk<retrofit2.HttpException>()
        every { httpEx.code() } returns 401
        coEvery { userRepo.getProfile() } throws httpEx
        // Теперь мокируем suspend logout()
        coEvery { authRepo.logout() } returns Result.success(Unit)

        vm.loadProfile()
        advanceUntilIdle()

        assertFalse(vm.isLoadingProfile)
        assertNull(vm.profile)
        // Проверяем, что logout() действительно вызвался
        coVerify(exactly = 1) { authRepo.logout() }
    }

    @Test
    fun `loadProfile other failure sets errorMessage`() = runTest {
        coEvery { userRepo.getProfile() } throws RuntimeException("oh no")

        vm.loadProfile()
        advanceUntilIdle()

        assertFalse(vm.isLoadingProfile)
        assertEquals("oh no", vm.errorMessage)
    }

    @Test
    fun `updateProfile success invokes callback and resets processing`() = runTest {
        val updatedUser = User(8L, "Пётр", "pert", "pert@yandex.ru", "+79000000000")
        coEvery { userRepo.updateProfile("Пётр", "pt@example.com", "67890") } returns Result.success(updatedUser)

        var result: Result<User>? = null
        vm.updateProfile("Пётр", "pt@example.com", "67890") { result = it }
        advanceUntilIdle()

        assertFalse(vm.isProcessing)
        assertTrue(result!!.isSuccess)
        assertEquals(updatedUser, result!!.getOrNull())
    }

    @Test
    fun `updateProfile failure invokes callback with error and resets processing`() = runTest {
        coEvery { userRepo.updateProfile(any(), any(), any()) } returns Result.failure(Exception("bad update"))

        var result: Result<User>? = null
        vm.updateProfile("A", "b@e", "c") { result = it }
        advanceUntilIdle()

        assertFalse(vm.isProcessing)
        assertTrue(result!!.isFailure)
        assertEquals("bad update", result!!.exceptionOrNull()?.message)
    }

    @Test
    fun `changePassword success invokes callback and resets processing`() = runTest {
        coEvery { userRepo.changePassword("old", "new", "new") } returns Result.success(Unit)

        var result: Result<Unit>? = null
        vm.changePassword("old", "new", "new") { result = it }
        advanceUntilIdle()

        assertFalse(vm.isProcessing)
        assertTrue(result!!.isSuccess)
    }

    @Test
    fun `changePassword failure invokes callback with error and resets processing`() = runTest {
        coEvery { userRepo.changePassword(any(), any(), any()) } returns Result.failure(Exception("pw fail"))

        var result: Result<Unit>? = null
        vm.changePassword("x", "y", "z") { result = it }
        advanceUntilIdle()

        assertFalse(vm.isProcessing)
        assertTrue(result!!.isFailure)
        assertEquals("pw fail", result!!.exceptionOrNull()?.message)
    }

    @Test
    fun `logout invokes auth logout and clears profile`() = runTest {
        // Мокаем suspend logout()
        coEvery { authRepo.logout() } returns Result.success(Unit)
        // Предварительно ставим профиль
        vm.profile = User(7L, "Ivan", "ivan2004", "ivan@gmail.com", "+79001233456")

        var wasCalled = false
        vm.logout { wasCalled = true }
        advanceUntilIdle()

        coVerify { authRepo.logout() }
        assertNull(vm.profile)
        assertTrue(wasCalled)
    }
}
