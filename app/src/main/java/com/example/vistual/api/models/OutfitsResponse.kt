package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de respuesta para lista de outfits del API REST
 */
@JsonClass(generateAdapter = true)
data class OutfitsResponse(
    @Json(name = "success")
    val success: Boolean,
    
    @Json(name = "message")
    val message: String? = null,
    
    @Json(name = "outfits")
    val outfits: List<OutfitDto>? = null
)
