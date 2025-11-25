package com.example.vistual.api

import com.example.vistual.api.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface de Retrofit para endpoints de AUTENTICACIÓN de XANO
 * Base URL: https://x8ki-letl-twmt.n7.xano.io/api:bOk8Zi6W/
 * 
 * Endpoints según XANO:
 * - POST auth/login (#3210995) - Login y obtener token
 * - POST auth/signup (#3210996) - Registro y obtener token
 * - GET auth/me (#3210997) - Obtener usuario del token
 */
interface AuthApiService {
    
    /**
     * POST auth/signup (#3210996)
     * Registro de nuevo usuario y obtener token de autenticación
     * @param request RegisterRequest con name, email y password
     * @return RegisterResponse con authToken y datos del usuario
     */
    @POST("auth/signup")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    /**
     * POST auth/login (#3210995)
     * Login de usuario y obtener token de autenticación
     * @param request LoginRequest con email y password
     * @return LoginResponse con authToken y datos del usuario
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    /**
     * GET auth/me (#3210997)
     * Obtener información del usuario autenticado desde el token
     * @param token Token de autenticación (formato: "Bearer {token}")
     * @return UserDto con información del usuario actual
     */
    @GET("auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<UserDto>
}
