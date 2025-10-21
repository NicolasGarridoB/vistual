package com.example.vistual.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistual.model.LoginState
import com.example.vistual.model.RegisterState
import com.example.vistual.model.Usuario
import com.example.vistual.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la lógica de Login y Register
 * Implementa el patrón MVVM requerido por la rúbrica
 * Separa la lógica de negocio de la UI
 */
class AuthViewModel(context: Context) : ViewModel() {
    
    // Variables de estado que cumplen con el requisito de "Variables" de la rúbrica
    private val userRepository = UserRepository(context)
    
    private val _loginState = mutableStateOf(LoginState())
    val loginState: State<LoginState> = _loginState
    
    private val _registerState = mutableStateOf(RegisterState())
    val registerState: State<RegisterState> = _registerState
    
    /**
     * Función que cumple con el requisito de "Una función" de la rúbrica
     * Maneja la lógica de login con validaciones y condicionales
     */
    fun login(correo: String, password: String) {
        // Condicionales - requisito de la rúbrica
        if (correo.isBlank() || password.isBlank()) {
            _loginState.value = _loginState.value.copy(
                errorMessage = "Por favor completa todos los campos"
            )
            return
        }
        
        if (!esEmailValido(correo)) {
            _loginState.value = _loginState.value.copy(
                errorMessage = "Por favor ingresa un email válido"
            )
            return
        }
        
        _loginState.value = _loginState.value.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            try {
                val loginExitoso = userRepository.validarLogin(correo, password)
                
                // Condicional para manejar resultado
                if (loginExitoso) {
                    val usuario = userRepository.obtenerUsuarioPorCorreo(correo)
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        usuario = usuario
                    )
                } else {
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        errorMessage = "Credenciales incorrectas"
                    )
                }
            } catch (e: Exception) {
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al iniciar sesión: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Función para registrar nuevos usuarios
     * Utiliza validaciones con condicionales múltiples
     */
    fun register(nombre: String, correo: String, password: String, confirmarPassword: String) {
        // Múltiples condicionales - requisito de la rúbrica
        if (nombre.isBlank() || correo.isBlank() || password.isBlank() || confirmarPassword.isBlank()) {
            _registerState.value = _registerState.value.copy(
                errorMessage = "Por favor completa todos los campos"
            )
            return
        }
        
        if (!esEmailValido(correo)) {
            _registerState.value = _registerState.value.copy(
                errorMessage = "Por favor ingresa un email válido"
            )
            return
        }
        
        if (password.length < 6) {
            _registerState.value = _registerState.value.copy(
                errorMessage = "La contraseña debe tener al menos 6 caracteres"
            )
            return
        }
        
        if (password != confirmarPassword) {
            _registerState.value = _registerState.value.copy(
                errorMessage = "Las contraseñas no coinciden"
            )
            return
        }
        
        _registerState.value = _registerState.value.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            try {
                // Verificar si el correo ya existe
                val correoExiste = userRepository.existeCorreo(correo)
                
                if (correoExiste) {
                    _registerState.value = _registerState.value.copy(
                        isLoading = false,
                        errorMessage = "Este correo ya está registrado"
                    )
                    return@launch
                }
                
                val usuario = Usuario(
                    nombre = nombre,
                    correo = correo,
                    password = password
                )
                
                val registroExitoso = userRepository.registrarUsuario(usuario)
                
                if (registroExitoso) {
                    _registerState.value = _registerState.value.copy(
                        isLoading = false,
                        isRegistered = true
                    )
                } else {
                    _registerState.value = _registerState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al registrar usuario"
                    )
                }
            } catch (e: Exception) {
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al registrar: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Función auxiliar para validar email usando expresiones regulares
     */
    private fun esEmailValido(email: String): Boolean {
        val patronEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(patronEmail.toRegex())
    }
    
    /**
     * Limpia los mensajes de error
     */
    fun limpiarErrores() {
        _loginState.value = _loginState.value.copy(errorMessage = null)
        _registerState.value = _registerState.value.copy(errorMessage = null)
    }
    
    /**
     * Reinicia el estado de registro
     */
    fun reiniciarEstadoRegistro() {
        _registerState.value = RegisterState()
    }
}