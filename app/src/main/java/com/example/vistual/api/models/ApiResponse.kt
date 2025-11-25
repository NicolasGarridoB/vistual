package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de respuesta genérico para operaciones exitosas
 */
@JsonClass(generateAdapter = true)
data class ApiResponse(
    @Json(name = "success")
    val success: Boolean,
    
    @Json(name = "message")
    val message: String? = null
)
