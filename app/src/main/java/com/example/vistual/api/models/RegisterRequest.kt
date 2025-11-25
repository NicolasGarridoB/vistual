package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de solicitud para registro en el API REST de XANO
 */
@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "name")
    val nombre: String,
    
    @Json(name = "email")
    val correo: String,
    
    @Json(name = "password")
    val password: String
)
