package com.sfedu.bank_queue_android.repository

import kotlinx.coroutines.flow.Flow

/** Интерфейс для авторизации и управления токеном */
interface AuthRepository {
    /** Авторизовать пользователя и сохранить токен */
    suspend fun login(username: String, password: String): Result<Unit>

    /** Удалить локально сохранённый токен (logout) */
    suspend fun logout(): Result<Unit>

    /** Получить текущий токен, если есть */
    fun getToken(): Flow<String?>
}