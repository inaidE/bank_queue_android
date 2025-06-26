package com.sfedu.bank_queue_android.viewmodel

import com.sfedu.bank_queue_android.model.AuthRequest
import com.sfedu.bank_queue_android.model.AuthResponse
import com.sfedu.bank_queue_android.network.RemoteDataSource
import com.sfedu.bank_queue_android.repository.AuthRepository
import com.sfedu.bank_queue_android.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AuthViewModel

    private val authRepo = mockk<AuthRepository>()
    private val userRepo = mockk<UserRepository>()
    private val remoteDataSource = mockk<RemoteDataSource>()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = AuthViewModel(authRepo, userRepo, remoteDataSource)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_success_sets_Success_state() = runTest {
        // Arrange
        val token = "tok123"
        coEvery { remoteDataSource.login(AuthRequest("u", "p")) } returns AuthResponse(token)
        coEvery { authRepo.login("u", "p") } returns Result.success(token)

        // Act
        viewModel.login("u", "p")
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState is AuthUiState.Success)
    }

    @Test
    fun login_failure_sets_Error_state_with_message() = runTest {
        // Arrange
        coEvery { remoteDataSource.login(any()) } throws RuntimeException("bad creds")

        // Act
        viewModel.login("user", "pass")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState
        assertTrue(state is AuthUiState.Error)
        assertEquals("bad creds", (state as AuthUiState.Error).message)
    }

    @Test
    fun register_success_sets_Success_state() = runTest {
        // Arrange
        coEvery { userRepo.register("Name", "login", "e@mail", "pwd", "123") } returns Result.success(Unit)

        // Act
        viewModel.register("Name", "login", "e@mail", "pwd", "123")
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState is AuthUiState.Success)
    }

    @Test
    fun register_failure_sets_Error_state_with_message() = runTest {
        // Arrange
        coEvery { userRepo.register(any(), any(), any(), any(), any()) } returns Result.failure(Exception("no register"))

        // Act
        viewModel.register("N", "L", "E", "P", "M")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState
        assertTrue(state is AuthUiState.Error)
        assertEquals("no register", (state as AuthUiState.Error).message)
    }

    @Test
    fun logout_resets_to_Idle_state() = runTest {
        // Arrange
        coEvery { authRepo.logout() } returns Result.success(Unit)
        viewModel.uiState = AuthUiState.Success

        // Act
        viewModel.logout()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState is AuthUiState.Idle)
        coVerify(exactly = 1) { authRepo.logout() }
    }
}
