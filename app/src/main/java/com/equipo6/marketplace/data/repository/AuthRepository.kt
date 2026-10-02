package com.equipo6.marketplace.data.repository

import com.equipo6.marketplace.data.local.TokenManager
import com.equipo6.marketplace.data.network.AuthApiService
import com.equipo6.marketplace.data.network.dto.AuthResponse
import com.equipo6.marketplace.data.network.dto.LoginRequest
import com.equipo6.marketplace.data.network.dto.RegisterRequest
import org.json.JSONObject
import retrofit2.Response

class AuthRepository(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return safeApiCall { apiService.login(request) }
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return safeApiCall { apiService.register(request) }
    }

    private suspend fun safeApiCall(apiCall: suspend () -> Response<AuthResponse>): Result<AuthResponse> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenManager.saveToken(body.token)
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val errorMsg = parseErrorBody(response.errorBody()?.string(), response.code())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: no hay conexion con el servidor local"))
        }
    }

    private fun parseErrorBody(errorBody: String?, code: Int): String {
        if (errorBody.isNullOrEmpty()) return "Error HTTP $code"
        return try {
            val jsonObject = JSONObject(errorBody)
            when (code) {
                422 -> jsonObject.optString("message", "Error de validacion")
                401 -> "Credenciales incorrectas"
                409 -> "El correo ya esta registrado"
                else -> jsonObject.optString("message", "Error HTTP $code")
            }
        } catch (e: Exception) {
            "Error HTTP $code"
        }
    }
}