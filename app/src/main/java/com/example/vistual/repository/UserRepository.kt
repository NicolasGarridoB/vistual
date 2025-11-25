package com.example.vistual.repository

import android.content.SharedPreferences
import com.example.vistual.api.AuthApiService
import com.example.vistual.api.models.LoginRequest
import com.example.vistual.api.models.RegisterRequest
import com.example.vistual.db.UsuarioDao
import com.example.vistual.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository para gestionar datos de usuarios
 * Implementa patrón Repository de MVVM
 * Consume datos de:
 * - API REST (externa) mediante Retrofit - AuthApiService
 * - Room Database (interna) para persistencia local
 */
class UserRepository(
    private val usuarioDao: UsuarioDao,
    private val authService: AuthApiService,
    private val sharedPreferences: SharedPreferences
) {

    /**
     * Registra un nuevo usuario
     * 1. Intenta registrar en el API REST
     * 2. Si tiene éxito, guarda también en la base de datos local (Room)
     */
    suspend fun registrarUsuario(usuario: Usuario): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // 1. Intentar registro en API REST
            val request = RegisterRequest(
                nombre = usuario.nombre,
                correo = usuario.correo,
                password = usuario.password
            )
            val response = authService.register(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                // 2. Guardar también en base de datos local
                val userFromApi = response.body()?.user
                if (userFromApi != null) {
                    val localUsuario = Usuario(
                        id = userFromApi.id,
                        nombre = userFromApi.nombre,
                        correo = userFromApi.correo,
                        password = usuario.password // No viene en la respuesta por seguridad
                    )
                    usuarioDao.insertUsuario(localUsuario)
                }
                Result.success(Unit)
            } else {
                // Si falla el API, intentar guardar solo en local como fallback
                val existingUser = usuarioDao.getUsuarioByCorreo(usuario.correo)
                if (existingUser != null) {
                    Result.failure(Exception("El correo ya está registrado."))
                } else {
                    usuarioDao.insertUsuario(usuario)
                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            // Fallback: si no hay conexión, guardar solo en local
            try {
                val existingUser = usuarioDao.getUsuarioByCorreo(usuario.correo)
                if (existingUser != null) {
                    Result.failure(Exception("El correo ya está registrado."))
                } else {
                    usuarioDao.insertUsuario(usuario)
                    Result.success(Unit)
                }
            } catch (dbError: Exception) {
                Result.failure(Exception("Error al registrar usuario: ${e.message}"))
            }
        }
    }

    /**
     * Valida credenciales de usuario
     * 1. Intenta validar contra el API REST
     * 2. Si falla, valida contra la base de datos local (Room)
     */
    suspend fun validarCredenciales(correo: String, password: String): Result<Usuario> = withContext(Dispatchers.IO) {
        return@withContext try {
            // 1. Intentar login en API REST
            val request = LoginRequest(correo = correo, password = password)
            val response = authService.login(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val userFromApi = response.body()?.user
                val token = response.body()?.token
                
                if (userFromApi != null) {
                    // Guardar token si existe
                    token?.let { saveAuthToken(it) }
                    
                    // Sincronizar con base de datos local
                    val localUsuario = Usuario(
                        id = userFromApi.id,
                        nombre = userFromApi.nombre,
                        correo = userFromApi.correo,
                        password = password
                    )
                    
                    // Actualizar o insertar en base de datos local
                    val existing = usuarioDao.getUsuarioByCorreo(correo)
                    if (existing == null) {
                        usuarioDao.insertUsuario(localUsuario)
                    }
                    
                    saveUserSession(localUsuario)
                    Result.success(localUsuario)
                } else {
                    Result.failure(Exception("Credenciales incorrectas."))
                }
            } else {
                // Fallback: validar contra base de datos local
                val usuario = usuarioDao.validarCredenciales(correo, password)
                if (usuario != null) {
                    saveUserSession(usuario)
                    Result.success(usuario)
                } else {
                    Result.failure(Exception("Credenciales incorrectas."))
                }
            }
        } catch (e: Exception) {
            // Fallback: si no hay conexión, validar solo contra local
            try {
                val usuario = usuarioDao.validarCredenciales(correo, password)
                if (usuario != null) {
                    saveUserSession(usuario)
                    Result.success(usuario)
                } else {
                    Result.failure(Exception("Credenciales incorrectas."))
                }
            } catch (dbError: Exception) {
                Result.failure(Exception("Error al validar credenciales: ${e.message}"))
            }
        }
    }

    fun logout() {
        with(sharedPreferences.edit()) {
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            remove(KEY_AUTH_TOKEN)
            apply()
        }
    }

    fun getLoggedInUser(): Usuario? {
        val userId = sharedPreferences.getInt(KEY_USER_ID, -1)
        val userEmail = sharedPreferences.getString(KEY_USER_EMAIL, null)
        if (userId == -1 || userEmail == null) {
            return null
        }
        return Usuario(id = userId, correo = userEmail, nombre = "", password = "")
    }
    
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    private fun saveUserSession(usuario: Usuario) {
        with(sharedPreferences.edit()) {
            putInt(KEY_USER_ID, usuario.id)
            putString(KEY_USER_EMAIL, usuario.correo)
            apply()
        }
    }
    
    private fun saveAuthToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_AUTH_TOKEN, token)
            apply()
        }
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
