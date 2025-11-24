package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object para Prenda del API REST
 */
@JsonClass(generateAdapter = true)
data class PrendaDto(
    @Json(name = "id")
    val id: Int = 0,
    
    @Json(name = "nombre")
    val nombre: String,
    
    @Json(name = "categoria")
    val categoria: String,
    
    @Json(name = "color")
    val color: String,
    
    @Json(name = "imagen_url")
    val imagenUrl: String? = null,
    
    @Json(name = "usuario_id")
    val usuarioId: Int,
    
    @Json(name = "fecha_creacion")
    val fechaCreacion: String? = null
)
