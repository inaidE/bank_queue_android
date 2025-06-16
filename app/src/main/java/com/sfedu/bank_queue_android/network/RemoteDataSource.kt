package com.sfedu.bank_queue_android.network

import com.sfedu.bank_queue_android.model.AuthRequest
import com.sfedu.bank_queue_android.model.AuthResponse
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: ApiService
) {
    suspend fun login(req: AuthRequest): AuthResponse =
        api.login(req)

    suspend fun register(req: AuthRequest): AuthResponse =
        api.register(req)

    // …
}