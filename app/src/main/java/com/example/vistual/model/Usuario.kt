package com.example.vistual.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class que representa un usuario en el sistema, ahora como entidad de Room.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    val password: String // En una app real, esto debería ser un hash.
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
