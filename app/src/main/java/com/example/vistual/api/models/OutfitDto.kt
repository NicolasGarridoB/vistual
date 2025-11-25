package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object para Outfit del API REST
 * Coincide con la tabla 'outfit' de XANO
 */
@JsonClass(generateAdapter = true)
data class OutfitDto(
    @Json(name = "id")
    val id: Int = 0,
    
    @Json(name = "nombre")
    val nombre: String,
    
    @Json(name = "user_id")
    val usuarioId: Int,
    
    @Json(name = "prenda_superior_id")
    val prendaSuperiorId: Int? = null,
    
    @Json(name = "prenda_inferior_id")
    val prendaInferiorId: Int? = null,
    
    @Json(name = "zapatos_id")
    val zapatosId: Int? = null,
    
    @Json(name = "created_at")
    val createdAt: Long? = null,
    
    @Json(name = "updated_at")
    val updatedAt: Long? = null
)
