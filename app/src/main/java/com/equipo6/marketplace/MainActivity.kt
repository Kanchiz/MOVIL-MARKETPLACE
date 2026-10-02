package com.equipo6.marketplace

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo6.marketplace.data.local.TokenManager
import com.equipo6.marketplace.data.network.RetrofitClient
import com.equipo6.marketplace.data.repository.AuthRepository
import com.equipo6.marketplace.ui.screens.HomeScreen
import com.equipo6.marketplace.ui.screens.LoginScreen
import com.equipo6.marketplace.ui.screens.RegisterScreen
import com.equipo6.marketplace.ui.theme.MarketplaceEquipo6Theme
import com.equipo6.marketplace.ui.viewmodel.AuthViewModel
import com.equipo6.marketplace.ui.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(applicationContext)
        val repository = AuthRepository(RetrofitClient.apiService, tokenManager)
        val factory = AuthViewModelFactory(repository)

        setContent {
            MarketplaceEquipo6Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = viewModel(factory = factory)

                    // Si ya existe token guardado inicia en home, sino en login
                    var currentScreen by remember {
                        mutableStateOf(if (tokenManager.getToken() != null) "home" else "login")
                    }

                    when (currentScreen) {
                        "login" -> {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateToRegister = { currentScreen = "register" },
                                onLoginSuccess = {
                                    Toast.makeText(applicationContext, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show()
                                    currentScreen = "home"
                                }
                            )
                        }
                        "register" -> {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onNavigateToLogin = { currentScreen = "login" },
                                onRegisterSuccess = {
                                    Toast.makeText(applicationContext, "¡Registro completado!", Toast.LENGTH_SHORT).show()
                                    currentScreen = "login"
                                }
                            )
                        }
                        "home" -> {
                            HomeScreen(
                                onLogout = {
                                    tokenManager.clearToken()
                                    Toast.makeText(applicationContext, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                                    currentScreen = "login"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}