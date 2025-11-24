package com.example.vistual.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistual.model.LoginState
import com.example.vistual.model.RegisterState
import com.example.vistual.model.Usuario
import com.example.vistual.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = mutableStateOf(LoginState())
    val loginState: State<LoginState> = _loginState

    private val _registerState = mutableStateOf(RegisterState())
    val registerState: State<RegisterState> = _registerState

    private var _currentUser: Usuario? = userRepository.getLoggedInUser()
    val currentUser: Usuario? get() = _currentUser

    fun login(correo: String, password: String) {
        if (correo.isBlank() || password.isBlank()) {
            _loginState.value = LoginState(errorMessage = "Por favor completa todos los campos")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState(isLoading = true)
            val result = userRepository.validarCredenciales(correo, password)
            result.onSuccess { user ->
                _currentUser = user
                _loginState.value = LoginState(isLoggedIn = true, usuario = user)
            }.onFailure { error ->
                _loginState.value = LoginState(errorMessage = error.message)
            }
        }
    }

    fun register(nombre: String, correo: String, password: String, confirmarPassword: String) {
        if (password != confirmarPassword) {
            _registerState.value = RegisterState(errorMessage = "Las contraseñas no coinciden")
            return
        }
        if (nombre.isBlank() || correo.isBlank() || password.isBlank()) {
            _registerState.value = RegisterState(errorMessage = "Todos los campos son obligatorios")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState(isLoading = true)
            val newUser = Usuario(nombre = nombre, correo = correo, password = password)
            val result = userRepository.registrarUsuario(newUser)
            result.onSuccess {
                _registerState.value = RegisterState(isRegistered = true)
            }.onFailure { error ->
                _registerState.value = RegisterState(errorMessage = error.message)
            }
        }
    }

    fun logout() {
        userRepository.logout()
        _currentUser = null
        _loginState.value = LoginState()
    }

    fun isLoggedIn(): Boolean {
        return _currentUser != null
    }

    fun currentUserEmail(): String {
        return _currentUser?.correo ?: ""
    }
}