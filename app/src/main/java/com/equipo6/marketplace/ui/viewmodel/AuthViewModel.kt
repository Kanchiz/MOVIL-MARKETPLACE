package com.equipo6.marketplace.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.equipo6.marketplace.data.network.dto.LoginRequest
import com.equipo6.marketplace.data.network.dto.RegisterRequest
import com.equipo6.marketplace.data.repository.AuthRepository
import com.equipo6.marketplace.ui.state.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Todos los campos son obligatorios")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.login(LoginRequest(email.trim(), pass)).fold(
                onSuccess = { _uiState.value = AuthUiState.Success(it) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    fun register(name: String, email: String, pass: String, phone: String, role: String) {
        if (name.isBlank() || email.isBlank() || pass.length < 8) {
            _uiState.value = AuthUiState.Error("Datos invalidos. Contrasena minimo 8 caracteres")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val req = RegisterRequest(
                name = name.trim(),
                email = email.trim(),
                password = pass,
                phone = phone.ifBlank { null },
                role = role
            )
            repository.register(req).fold(
                onSuccess = { _uiState.value = AuthUiState.Success(it) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

class AuthViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}