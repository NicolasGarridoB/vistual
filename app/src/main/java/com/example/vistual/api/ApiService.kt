package com.example.vistual.api

import com.example.vistual.api.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface de Retrofit para consumir API REST
 * Define todos los endpoints disponibles para la aplicación
 */
interface ApiService {
    
    /**
     * Endpoint para login de usuario
     * @param request LoginRequest con correo y password
     * @return LoginResponse con información del usuario y token
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    /**
     * Endpoint para registro de nuevo usuario
     * @param request RegisterRequest con nombre, correo y password
     * @return RegisterResponse con información del usuario registrado
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    /**
     * Endpoint para obtener todas las prendas de un usuario
     * @param usuarioId ID del usuario
     * @param token Token de autenticación
     * @return PrendasResponse con lista de prendas
     */
    @GET("prendas/usuario/{usuarioId}")
    suspend fun getPrendasByUsuario(
        @Path("usuarioId") usuarioId: Int,
        @Header("Authorization") token: String
    ): Response<PrendasResponse>
    
    /**
     * Endpoint para crear una nueva prenda
     * @param prenda PrendaDto con información de la prenda
     * @param token Token de autenticación
     * @return Response con la prenda creada
     */
    @POST("prendas")
    suspend fun createPrenda(
        @Body prenda: PrendaDto,
        @Header("Authorization") token: String
    ): Response<PrendaDto>
    
    /**
     * Endpoint para eliminar una prenda
     * @param prendaId ID de la prenda a eliminar
     * @param token Token de autenticación
     * @return Response indicando éxito o error
     */
    @DELETE("prendas/{prendaId}")
    suspend fun deletePrenda(
        @Path("prendaId") Int: Int,
        @Header("Authorization") token: String
    ): Response<Unit>
}
