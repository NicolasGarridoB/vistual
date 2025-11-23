package com.example.vistual.repository

import android.content.SharedPreferences
import com.example.vistual.db.UsuarioDao
import com.example.vistual.model.Usuario

class UserRepository(
    private val usuarioDao: UsuarioDao,
    private val sharedPreferences: SharedPreferences
) {

    suspend fun registrarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            val existingUser = usuarioDao.getUsuarioByCorreo(usuario.correo)
            if (existingUser != null) {
                Result.failure(Exception("El correo ya está registrado."))
            } else {
                usuarioDao.insertUsuario(usuario)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun validarCredenciales(correo: String, password: String): Result<Usuario> {
        return try {
            val usuario = usuarioDao.validarCredenciales(correo, password)
            if (usuario != null) {
                saveUserSession(usuario)
                Result.success(usuario)
            } else {
                Result.failure(Exception("Credenciales incorrectas."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        with(sharedPreferences.edit()) {
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            apply()
        }
    }

    fun getLoggedInUser(): Usuario? {
        val userId = sharedPreferences.getInt(KEY_USER_ID, -1)
        val userEmail = sharedPreferences.getString(KEY_USER_EMAIL, null)
        if (userId == -1 || userEmail == null) {
            return null
        }
        // No tenemos el nombre ni el password, pero para la sesión nos vale con el ID y el correo.
        return Usuario(id = userId, correo = userEmail, nombre = "", password = "")
    }

    private fun saveUserSession(usuario: Usuario) {
        with(sharedPreferences.edit()) {
            putInt(KEY_USER_ID, usuario.id)
            putString(KEY_USER_EMAIL, usuario.correo)
            apply()
        }
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
    }
}
