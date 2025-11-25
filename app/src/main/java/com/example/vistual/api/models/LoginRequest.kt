package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de solicitud para login en el API REST de XANO
 */
@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email")
    val correo: String,
    
    @Json(name = "password")
    val password: String
)
