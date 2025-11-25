package com.example.vistual.api

import com.example.vistual.api.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface de Retrofit para consumir API REST de XANO (Vistual API)
 * Define endpoints para prendas y outfits
 * Base URL: https://x8ki-letl-twmt.n7.xano.io/api:G1UzV9hT/
 * 
 * Nota: Los endpoints de autenticación están en AuthApiService
 */
interface ApiService {
    
    // ==================== PRENDAS ====================
    
    /**
     * GET prenda (#3211019)
     * Obtener todas las prendas (query all prenda records)
     * @param token Token de autenticación (formato: "Bearer {token}")
     * @return Lista de PrendaDto
     */
    @GET("prenda")
    suspend fun getAllPrendas(
        @Header("Authorization") token: String
    ): Response<List<PrendaDto>>
    
    /**
     * GET prenda/{prenda_id} (#3211018)
     * Obtener una prenda específica por ID
     * @param prendaId ID de la prenda
     * @param token Token de autenticación (formato: "Bearer {token}")
     * @return PrendaDto con los datos de la prenda
     */
    @GET("prenda/{prenda_id}")
    suspend fun getPrendaById(
        @Path("prenda_id") prendaId: Int,
        @Header("Authorization") token: String
    ): Response<PrendaDto>
    
    /**
     * POST prenda (#3211020)
     * Crear una nueva prenda (add prenda record)
     * @param prenda PrendaDto con información de la prenda (nombre, categoria, color, imagen_url, user_id)
     * @param token Token de autenticación (formato: "Bearer {token}")
     * @return Response con la prenda creada incluyendo su ID generado
     */
    @POST("prenda")
    suspend fun createPrenda(
        @Body prenda: PrendaDto,
        @Header("Authorization") token: String
    ): Response<PrendaDto>
    
    /**
     * PATCH prenda/{prenda_id} (#3211021)
     * Editar una prenda existente
     * @param prendaId ID de la prenda a editar
     * @param prenda PrendaDto con los datos actualizados
     * @param token Token de autenticación (formato: "Bearer {token}")
     * @return Response con la prenda actualizada
     */
    @PATCH("prenda/{prenda_id}")
    suspend fun updatePrenda(
        @Path("prenda_id") prendaId: Int,
        @Body prenda: PrendaDto,
        @Header("Authorization") token: String
    ): Response<PrendaDto>
    
    /**
     * DELETE prenda/{prenda_id} (#3211017)
     * Eliminar una prenda (delete prenda record)
     * @param prendaId ID de la prenda a eliminar
     * @param token Token de autenticación (formato: "Bearer {token}")
     * @return Response vacío indicando éxito o error
     */
    @DELETE("prenda/{prenda_id}")
    suspend fun deletePrenda(
        @Path("prenda_id") prendaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>
    
    // ==================== OUTFITS ====================
    
    /**
     * GET outfit (#3211014)
     * Obtener todos los outfits (query all outfit records)
     * @param token Token de autenticación (formato: "Bearer {token}")
     * @return Lista de OutfitDto
     */
    @GET("outfit")
    suspend fun getAllOutfits(
        @Header("Authorization") token: String
    ): Response<List<OutfitDto>>
    
    /**
     * GET outfit/{outfit_id} (#3211013)
     * Obtener un outfit específico por ID
     * @param outfitId ID del outfit
     * @param token Token de autenticación (formato: "Bearer {token}")
     * @return OutfitDto con los datos del outfit
     */
    @GET("outfit/{outfit_id}")
    suspend fun getOutfitById(
        @Path("outfit_id") outfitId: Int,
        @Header("Authorization") token: String
    ): Response<OutfitDto>
    
    /**
     * POST outfit (#3211015)
     * Crear un nuevo outfit (add outfit record)
     * @param outfit OutfitDto con información del outfit (nombre, user_id, ids de prendas)
     * @param token Token de autenticación (formato: "Bearer {token}")
     * @return Response con el outfit creado incluyendo su ID generado
     */
    @POST("outfit")
    suspend fun createOutfit(
        @Body outfit: OutfitDto,
        @Header("Authorization") token: String
    ): Response<OutfitDto>
    
    /**
     * PATCH outfit/{outfit_id} (#3211016)
     * Editar un outfit existente
     * @param outfitId ID del outfit a editar
     * @param outfit OutfitDto con los datos actualizados
     * @param token Token de autenticación (formato: "Bearer {token}")
     * @return Response con el outfit actualizado
     */
    @PATCH("outfit/{outfit_id}")
    suspend fun updateOutfit(
        @Path("outfit_id") outfitId: Int,
        @Body outfit: OutfitDto,
        @Header("Authorization") token: String
    ): Response<OutfitDto>
    
    /**
     * DELETE outfit/{outfit_id} (#3211012)
     * Eliminar un outfit (delete outfit record)
     * @param outfitId ID del outfit a eliminar
     * @param token Token de autenticación (formato: "Bearer {token}")
     * @return Response vacío indicando éxito o error
     */
    @DELETE("outfit/{outfit_id}")
    suspend fun deleteOutfit(
        @Path("outfit_id") outfitId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>
}
