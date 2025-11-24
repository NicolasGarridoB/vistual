package com.example.vistual.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente singleton de Retrofit para consumir API REST
 * Configura OkHttp con interceptores de logging y timeouts
 */
object RetrofitClient {
    
    // URL base de la API REST
    // IMPORTANTE: Cambiar esta URL por la de tu servidor real
    // Para emulador Android: usa 10.0.2.2 en lugar de localhost
    // Para dispositivo físico: usa la IP de tu computadora en la red local
    private const val BASE_URL = "https://api.vistual.example.com/"
    
    // Configuración de Moshi para serialización JSON
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    // Interceptor para logging de peticiones HTTP (útil para debug)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // Cliente OkHttp configurado con timeouts e interceptores
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Instancia de Retrofit
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    
    // Instancia del servicio API
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
