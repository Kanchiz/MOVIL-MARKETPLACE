package com.equipo6.marketplace.data.network.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null,
    val role: String = "buyer"
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val role: String,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class AuthResponse(
    val message: String,
    val user: UserDto,
    val token: String,
    val tokenType: String
)