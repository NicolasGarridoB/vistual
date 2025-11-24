package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de solicitud para login en el API REST
 */
@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "correo")
    val correo: String,
    
    @Json(name = "password")
    val password: String
)
