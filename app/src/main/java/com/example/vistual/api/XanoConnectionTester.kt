package com.example.vistual.api

import android.util.Log
import com.example.vistual.api.models.LoginRequest
import com.example.vistual.api.models.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Clase de utilidad para probar la conexión con el backend de XANO
 * Usar solo para testing y debugging
 */
object XanoConnectionTester {
    
    private const val TAG = "XanoConnectionTester"
    
    /**
     * Prueba la conexión con XANO intentando registrar un usuario de prueba
     * Revisa el Logcat para ver los resultados
     */
    fun testConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "=== Iniciando prueba de conexión con XANO ===")
                Log.d(TAG, "URL Auth: ${ApiConfig.AUTH_BASE_URL}")
                Log.d(TAG, "URL API: ${ApiConfig.BASE_URL}")
                
                // Intentar registro de usuario de prueba
                val testUser = RegisterRequest(
                    nombre = "Usuario Test",
                    correo = "test@vistual.com",
                    password = "test123"
                )
                
                Log.d(TAG, "Enviando petición de registro...")
                val response = RetrofitClient.authService.register(testUser)
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "✅ CONEXIÓN EXITOSA!")
                        Log.d(TAG, "Respuesta: ${response.body()}")
                        Log.d(TAG, "Usuario creado: ${response.body()?.user?.nombre}")
                    } else {
                        Log.e(TAG, "❌ Error en la respuesta")
                        Log.e(TAG, "Código: ${response.code()}")
                        Log.e(TAG, "Mensaje: ${response.message()}")
                        Log.e(TAG, "Body error: ${response.errorBody()?.string()}")
                    }
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "❌ EXCEPCIÓN AL CONECTAR CON XANO")
                    Log.e(TAG, "Error: ${e.message}")
                    Log.e(TAG, "Tipo: ${e.javaClass.simpleName}")
                    e.printStackTrace()
                    
                    // Ayuda para debugging
                    when (e) {
                        is java.net.UnknownHostException -> {
                            Log.e(TAG, "💡 Verifica que la URL de XANO sea correcta en ApiConfig.kt")
                        }
                        is java.net.ConnectException -> {
                            Log.e(TAG, "💡 Verifica tu conexión a internet")
                            Log.e(TAG, "💡 Verifica que XANO esté activo y los endpoints publicados")
                        }
                        is retrofit2.HttpException -> {
                            Log.e(TAG, "💡 El servidor respondió con error")
                            Log.e(TAG, "💡 Verifica que los endpoints en XANO coincidan con ApiService.kt")
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Prueba el login con un usuario existente
     * @param correo Email del usuario
     * @param password Contraseña del usuario
     */
    fun testLogin(correo: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "=== Probando login ===")
                
                val loginRequest = LoginRequest(
                    correo = correo,
                    password = password
                )
                
                val response = RetrofitClient.authService.login(loginRequest)
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "✅ LOGIN EXITOSO!")
                        Log.d(TAG, "Usuario: ${response.body()?.user?.nombre}")
                        Log.d(TAG, "Token: ${response.body()?.token}")
                    } else {
                        Log.e(TAG, "❌ Error en login")
                        Log.e(TAG, "Código: ${response.code()}")
                        Log.e(TAG, "Mensaje: ${response.errorBody()?.string()}")
                    }
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "❌ Error al hacer login: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }
}
