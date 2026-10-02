package com.equipo6.marketplace.data.network

import com.equipo6.marketplace.data.network.dto.AuthResponse
import com.equipo6.marketplace.data.network.dto.LoginRequest
import com.equipo6.marketplace.data.network.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}