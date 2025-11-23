package com.example.vistual.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.vistual.db.UsuarioDao
import com.example.vistual.model.Usuario
import com.example.vistual.network.ApiResult
import com.example.vistual.network.RetrofitInstance
import com.example.vistual.network.models.LoginRequest
import com.example.vistual.network.models.RegistroRequest
import com.example.vistual.network.safeApiCall

/**
 * Repository para gestionar usuarios.
 * Implementa el patrón Repository siguiendo MVVM.
 * 
 * Características:
 * - Gestiona BD interna (Room) para persistencia local
 * - Gestiona BD externa (Retrofit) para sincronización con servidor
 * - Maneja sesión de usuario con SharedPreferences
 * - Proporciona capa de abstracción entre ViewModels y fuentes de datos
 */
class UserRepository(
    private val usuarioDao: UsuarioDao,
    private val sharedPreferences: SharedPreferences
) {
    
    private val TAG = "UserRepository"
    
    // ==================== OPERACIONES DE REGISTRO ====================
    
    /**
     * Registra un nuevo usuario tanto en BD local como externa
     * 1. Valida que el correo no exista localmente
     * 2. Registra en el servidor (BD externa)
     * 3. Si tiene éxito, guarda en BD local
     * 
     * @param usuario Usuario a registrar
     * @return Result con éxito o error
     */
    suspend fun registrarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            // Verificar si el usuario ya existe localmente
            val existingUser = usuarioDao.getUsuarioByCorreo(usuario.correo)
            if (existingUser != null) {
                return Result.failure(Exception("El correo ya está registrado localmente."))
            }
            
            // Intentar registrar en el servidor (BD Externa)
            val apiResult = safeApiCall {
                RetrofitInstance.apiService.registrarUsuario(
                    RegistroRequest(
                        nombre = usuario.nombre,
                        correo = usuario.correo,
                        password = usuario.password
                    )
                )
            }
            
            when (apiResult) {
                is ApiResult.Success -> {
                    // Si el servidor responde bien, guardar localmente
                    val usuarioConId = usuario.copy(
                        id = apiResult.data.id // Usar ID del servidor
                    )
                    usuarioDao.insertUsuario(usuarioConId)
                    Log.d(TAG, "Usuario registrado exitosamente: ${usuario.correo}")
                    Result.success(Unit)
                }
                is ApiResult.Error -> {
                    // Si falla el servidor, solo guardar localmente
                    Log.w(TAG, "Error en servidor, guardando solo localmente: ${apiResult.message}")
                    usuarioDao.insertUsuario(usuario)
                    Result.success(Unit) // Aún así consideramos éxito (modo offline)
                }
                else -> Result.failure(Exception("Error inesperado en el registro"))
            }
        } catch (e: Exception) {
            // Si todo falla, intentar guardar solo localmente
            Log.e(TAG, "Error en registro, guardando solo localmente", e)
            try {
                usuarioDao.insertUsuario(usuario)
                Result.success(Unit)
            } catch (localError: Exception) {
                Result.failure(localError)
            }
        }
    }

    // ==================== OPERACIONES DE LOGIN ====================
    
    /**
     * Valida credenciales de usuario
     * 1. Intenta validar contra BD externa (servidor)
     * 2. Si falla, valida contra BD local
     * 3. Si tiene éxito, guarda la sesión
     * 
     * @param correo Correo del usuario
     * @param password Contraseña del usuario
     * @return Result con el Usuario si es válido, o error
     */
    suspend fun validarCredenciales(correo: String, password: String): Result<Usuario> {
        return try {
            // Intentar login con el servidor primero (BD Externa)
            val apiResult = safeApiCall {
                RetrofitInstance.apiService.login(
                    LoginRequest(correo = correo, password = password)
                )
            }
            
            when (apiResult) {
                is ApiResult.Success -> {
                    // Login exitoso con servidor
                    val usuarioRemoto = apiResult.data.usuario
                    val usuario = Usuario(
                        id = usuarioRemoto.id,
                        nombre = usuarioRemoto.nombre,
                        correo = usuarioRemoto.correo,
                        password = password
                    )
                    
                    // Guardar/actualizar en BD local
                    val localUser = usuarioDao.getUsuarioByCorreo(correo)
                    if (localUser == null) {
                        usuarioDao.insertUsuario(usuario)
                    } else {
                        usuarioDao.updateUsuario(usuario)
                    }
                    
                    // Guardar token de autenticación para futuras peticiones
                    saveAuthToken(apiResult.data.token)
                    
                    // Guardar sesión
                    saveUserSession(usuario)
                    Log.d(TAG, "Login exitoso con servidor: $correo")
                    Result.success(usuario)
                }
                is ApiResult.Error -> {
                    // Si falla el servidor, intentar login local
                    Log.w(TAG, "Servidor no disponible, intentando login local")
                    validarCredencialesLocal(correo, password)
                }
                else -> validarCredencialesLocal(correo, password)
            }
        } catch (e: Exception) {
            // Si hay error, intentar login local
            Log.e(TAG, "Error en login, intentando local", e)
            validarCredencialesLocal(correo, password)
        }
    }
    
    /**
     * Valida credenciales solo contra BD local
     */
    private suspend fun validarCredencialesLocal(correo: String, password: String): Result<Usuario> {
        return try {
            val usuario = usuarioDao.validarCredenciales(correo, password)
            if (usuario != null) {
                saveUserSession(usuario)
                Log.d(TAG, "Login local exitoso: $correo")
                Result.success(usuario)
            } else {
                Result.failure(Exception("Credenciales incorrectas."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== GESTIÓN DE SESIÓN ====================
    
    /**
     * Cierra la sesión del usuario
     * Limpia SharedPreferences y token
     */
    fun logout() {
        with(sharedPreferences.edit()) {
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_NAME)
            remove(KEY_AUTH_TOKEN)
            apply()
        }
        Log.d(TAG, "Sesión cerrada")
    }

    /**
     * Obtiene el usuario actualmente logueado desde SharedPreferences
     * @return Usuario logueado o null si no hay sesión activa
     */
    fun getLoggedInUser(): Usuario? {
        val userId = sharedPreferences.getInt(KEY_USER_ID, -1)
        val userEmail = sharedPreferences.getString(KEY_USER_EMAIL, null)
        val userName = sharedPreferences.getString(KEY_USER_NAME, null)
        
        if (userId == -1 || userEmail == null) {
            return null
        }
        
        return Usuario(
            id = userId, 
            correo = userEmail, 
            nombre = userName ?: "", 
            password = ""
        )
    }
    
    /**
     * Obtiene el ID del usuario logueado
     */
    fun getLoggedInUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }
    
    /**
     * Verifica si hay un usuario logueado
     */
    fun isUserLoggedIn(): Boolean {
        return getLoggedInUserId() != -1
    }
    
    /**
     * Obtiene el token de autenticación guardado
     */
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    // ==================== MÉTODOS PRIVADOS ====================
    
    /**
     * Guarda la sesión del usuario en SharedPreferences
     */
    private fun saveUserSession(usuario: Usuario) {
        with(sharedPreferences.edit()) {
            putInt(KEY_USER_ID, usuario.id)
            putString(KEY_USER_EMAIL, usuario.correo)
            putString(KEY_USER_NAME, usuario.nombre)
            apply()
        }
    }
    
    /**
     * Guarda el token de autenticación
     */
    private fun saveAuthToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_AUTH_TOKEN, "Bearer $token")
            apply()
        }
    }

    // ==================== CONSTANTES ====================
    
    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
