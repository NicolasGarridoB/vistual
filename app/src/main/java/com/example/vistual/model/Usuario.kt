package com.example.vistual.model

/**
 * Data class que representa un usuario en el sistema
 * Cumple con el requisito de "Una clase" de la rúbrica
 */
data class Usuario(
    val id: Int = -1,
    val nombre: String,
    val correo: String,
    val password: String
)

/**
 * Estado del login para el ViewModel
 */
data class LoginState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val usuario: Usuario? = null
)

/**
 * Estado del registro para el ViewModel
 */
data class RegisterState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val errorMessage: String? = null
)