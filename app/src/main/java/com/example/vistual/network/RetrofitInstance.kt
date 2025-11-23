package com.example.vistual.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton que configura y proporciona la instancia de Retrofit
 * para realizar peticiones HTTP a la API REST (BD externa).
 * 
 * Características:
 * - Configuración de timeout
 * - Logging de peticiones (útil para debugging)
 * - Conversión automática JSON con Gson
 * - Interceptor para manejar tokens de autenticación
 */
object RetrofitInstance {
    
    /**
     * URL BASE DE LA API
     * 
     * IMPORTANTE: Reemplaza esta URL con la de tu servidor real.
     * 
     * Opciones comunes para desarrollo:
     * - Servidor local: "http://10.0.2.2:8080/" (para emulador Android)
     * - Servidor local: "http://localhost:8080/" (para dispositivo físico en misma red)
     * - Servidor remoto: "https://tu-api.herokuapp.com/"
     * - API de prueba: "https://jsonplaceholder.typicode.com/" (para testing)
     * 
     * Para este proyecto usaremos una API mock/de prueba.
     * En producción, deberás implementar tu propio backend.
     */
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    
    /**
     * Interceptor para logging de peticiones HTTP
     * Útil para debugging y ver qué datos se envían/reciben
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Muestra todo el contenido
    }
    
    /**
     * Cliente HTTP configurado con interceptores y timeouts
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Logging de peticiones
        .connectTimeout(30, TimeUnit.SECONDS) // Timeout de conexión
        .readTimeout(30, TimeUnit.SECONDS) // Timeout de lectura
        .writeTimeout(30, TimeUnit.SECONDS) // Timeout de escritura
        .addInterceptor { chain ->
            // Interceptor para agregar headers comunes a todas las peticiones
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
            
            chain.proceed(requestBuilder.build())
        }
        .build()
    
    /**
     * Instancia de Retrofit configurada y lista para usar
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Conversión JSON
            .build()
    }
    
    /**
     * Instancia del servicio API
     * Esta es la que usarás en los repositorios
     */
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

/**
 * Clase auxiliar para encapsular las respuestas de la API
 * Útil para manejar estados de éxito/error de forma consistente
 */
sealed class ApiResult<out T> {
    /**
     * Respuesta exitosa con datos
     */
    data class Success<T>(val data: T) : ApiResult<T>()
    
    /**
     * Error con mensaje descriptivo
     */
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    
    /**
     * Estado de carga
     */
    object Loading : ApiResult<Nothing>()
}

/**
 * Función de extensión para ejecutar llamadas API de forma segura
 * y convertirlas en ApiResult
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> retrofit2.Response<T>
): ApiResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful && response.body() != null) {
            ApiResult.Success(response.body()!!)
        } else {
            ApiResult.Error(
                message = response.message() ?: "Error desconocido",
                code = response.code()
            )
        }
    } catch (e: Exception) {
        ApiResult.Error(
            message = e.message ?: "Error de conexión",
            code = null
        )
    }
}
