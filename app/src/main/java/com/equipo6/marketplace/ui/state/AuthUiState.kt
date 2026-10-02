package com.equipo6.marketplace.ui.state

import com.equipo6.marketplace.data.network.dto.AuthResponse

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val data: AuthResponse) : AuthUiState
    data class Error(val message: String) : AuthUiState
}