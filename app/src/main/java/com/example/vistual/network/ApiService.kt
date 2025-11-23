package com.example.vistual.network

import com.example.vistual.network.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface que define los endpoints de la API REST para la BD externa.
 * Utiliza Retrofit para consumir servicios web.
 * 
 * IMPORTANTE: Esta es una API de ejemplo. Deberás reemplazar la URL base
 * con la de tu servidor real en RetrofitInstance.kt
 */
interface ApiService {
    
    // ==================== ENDPOINTS DE USUARIOS ====================
    
    /**
     * Registra un nuevo usuario en el servidor
     * @param request Datos del usuario a registrar
     * @return Respuesta con el usuario creado
     */
    @POST("api/usuarios/registro")
    suspend fun registrarUsuario(
        @Body request: RegistroRequest
    ): Response<UsuarioResponse>
    
    /**
     * Valida las credenciales del usuario
     * @param request Correo y contraseña del usuario
     * @return Respuesta con token de autenticación y datos del usuario
     */
    @POST("api/usuarios/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
    
    /**
     * Obtiene los datos del usuario autenticado
     * @param token Token de autenticación
     * @param userId ID del usuario
     * @return Datos del usuario
     */
    @GET("api/usuarios/{userId}")
    suspend fun obtenerUsuario(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<UsuarioResponse>
    
    // ==================== ENDPOINTS DE PRENDAS ====================
    
    /**
     * Obtiene todas las prendas del usuario desde el servidor
     * @param token Token de autenticación
     * @param userId ID del usuario
     * @return Lista de prendas
     */
    @GET("api/prendas/usuario/{userId}")
    suspend fun obtenerPrendas(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<PrendaResponse>>
    
    /**
     * Crea una nueva prenda en el servidor
     * @param token Token de autenticación
     * @param request Datos de la prenda
     * @return Prenda creada con su ID del servidor
     */
    @POST("api/prendas")
    suspend fun crearPrenda(
        @Header("Authorization") token: String,
        @Body request: PrendaRequest
    ): Response<PrendaResponse>
    
    /**
     * Actualiza una prenda existente en el servidor
     * @param token Token de autenticación
     * @param prendaId ID de la prenda
     * @param request Datos actualizados
     * @return Prenda actualizada
     */
    @PUT("api/prendas/{prendaId}")
    suspend fun actualizarPrenda(
        @Header("Authorization") token: String,
        @Path("prendaId") prendaId: Int,
        @Body request: PrendaRequest
    ): Response<PrendaResponse>
    
    /**
     * Elimina una prenda del servidor
     * @param token Token de autenticación
     * @param prendaId ID de la prenda a eliminar
     * @return Mensaje de confirmación
     */
    @DELETE("api/prendas/{prendaId}")
    suspend fun eliminarPrenda(
        @Header("Authorization") token: String,
        @Path("prendaId") prendaId: Int
    ): Response<MessageResponse>
    
    // ==================== ENDPOINTS DE OUTFITS ====================
    
    /**
     * Obtiene todos los outfits del usuario desde el servidor
     * @param token Token de autenticación
     * @param userId ID del usuario
     * @return Lista de outfits
     */
    @GET("api/outfits/usuario/{userId}")
    suspend fun obtenerOutfits(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<OutfitResponse>>
    
    /**
     * Crea un nuevo outfit en el servidor
     * @param token Token de autenticación
     * @param request Datos del outfit
     * @return Outfit creado
     */
    @POST("api/outfits")
    suspend fun crearOutfit(
        @Header("Authorization") token: String,
        @Body request: OutfitRequest
    ): Response<OutfitResponse>
    
    /**
     * Elimina un outfit del servidor
     * @param token Token de autenticación
     * @param outfitId ID del outfit
     * @return Mensaje de confirmación
     */
    @DELETE("api/outfits/{outfitId}")
    suspend fun eliminarOutfit(
        @Header("Authorization") token: String,
        @Path("outfitId") outfitId: Int
    ): Response<MessageResponse>
    
    // ==================== ENDPOINT DE SINCRONIZACIÓN ====================
    
    /**
     * Sincroniza todos los datos del usuario con el servidor
     * Este endpoint permite subir múltiples prendas y outfits de una sola vez
     * @param token Token de autenticación
     * @param request Datos de sincronización
     * @return Estado de la sincronización
     */
    @POST("api/sync")
    suspend fun sincronizarDatos(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): Response<SyncResponse>
}
