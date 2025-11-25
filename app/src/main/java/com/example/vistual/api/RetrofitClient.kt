package com.example.vistual.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente singleton de Retrofit para consumir API REST de XANO
 * Configura OkHttp con interceptores de logging y timeouts
 * 
 * XANO usa dos URLs diferentes:
 * - AUTH_BASE_URL para autenticación (login, signup)
 * - BASE_URL para todo lo demás (prendas, outfits)
 */
object RetrofitClient {
    
    // Configuración de Moshi para serialización JSON
    // Moshi convierte automáticamente entre objetos Kotlin y JSON
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    // Interceptor para logging de peticiones HTTP (útil para debug)
    // Muestra en Logcat todas las peticiones y respuestas HTTP
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (ApiConfig.ENABLE_LOGGING) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
    
    // Cliente OkHttp configurado con timeouts e interceptores
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()
    
    // Instancia de Retrofit para AUTENTICACIÓN (login, signup)
    private val authRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.AUTH_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    
    // Instancia de Retrofit para API GENERAL (prendas, outfits)
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    
    // Servicio de autenticación (login, signup, me)
    val authService: AuthApiService = authRetrofit.create(AuthApiService::class.java)
    
    // Servicio del API general (prendas, outfits)
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
