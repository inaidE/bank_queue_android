package com.sfedu.bank_queue_android.network

import com.sfedu.bank_queue_android.model.AuthRequest
import com.sfedu.bank_queue_android.model.AuthResponse
import com.sfedu.bank_queue_android.model.Ticket
import com.sfedu.bank_queue_android.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): AuthResponse

    /** Регистрация нового пользователя */
    @POST("auth/register")
    suspend fun register(
        @Body request: AuthRequest
    ): AuthResponse

    // --- ТИКЕТЫ ---
    /** Получить список всех тикетов */
    @GET("tickets")
    suspend fun getTickets(): List<Ticket>

    /** Получить детали одного тикета по ID */
    @GET("tickets/{id}")
    suspend fun getTicket(
        @Path("id") id: Int
    ): Ticket

    // --- ПОЛЬЗОВАТЕЛИ (опционально) ---
    /**
     * Если у вас есть контроллер users, например:
     * @GetMapping("/users")
     * fun listUsers(): List<User>
     */
    @GET("users")
    suspend fun getUsers(): List<User>

    /**
     * Или добавление нового пользователя:
     * @PostMapping("/users")
     * fun createUser(@RequestBody user: User): User
     */
    @POST("users")
    suspend fun createUser(
        @Body user: User
    ): User
}