package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de respuesta para lista de prendas del API REST
 */
@JsonClass(generateAdapter = true)
data class PrendasResponse(
    @Json(name = "success")
    val success: Boolean,
    
    @Json(name = "message")
    val message: String? = null,
    
    @Json(name = "prendas")
    val prendas: List<PrendaDto>? = null
)
