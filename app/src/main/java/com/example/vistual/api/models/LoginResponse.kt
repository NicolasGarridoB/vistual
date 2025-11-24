package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de respuesta para login en el API REST
 */
@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "success")
    val success: Boolean,
    
    @Json(name = "message")
    val message: String? = null,
    
    @Json(name = "user")
    val user: UserDto? = null,
    
    @Json(name = "token")
    val token: String? = null
)
