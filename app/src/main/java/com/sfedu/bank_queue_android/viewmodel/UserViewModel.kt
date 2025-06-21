package com.sfedu.bank_queue_android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.bank_queue_android.model.User
import com.sfedu.bank_queue_android.repository.AuthRepository
import com.sfedu.bank_queue_android.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UserRepository,
    private val auth: AuthRepository
) : ViewModel() {

    /** профиль текущего пользователя */
    var profile by mutableStateOf<User?>(null)
        private set

    /** флаги загрузки и ошибок */
    var isLoadingProfile by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** загрузить профиль */
    fun loadProfile() {
        viewModelScope.launch {
            isLoadingProfile = true
            runCatching { repo.getProfile().getOrThrow() }
                .onSuccess { profile = it }
                .onFailure { errorMessage = it.message }
            isLoadingProfile = false
        }
    }

    /** разлогиниться */
    fun logout(onDone: ()->Unit) {
        viewModelScope.launch {
            auth.logout()
            onDone()
        }
    }
}