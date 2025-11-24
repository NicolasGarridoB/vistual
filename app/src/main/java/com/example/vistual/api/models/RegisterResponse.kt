package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de respuesta para registro en el API REST
 */
@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "success")
    val success: Boolean,
    
    @Json(name = "message")
    val message: String? = null,
    
    @Json(name = "user")
    val user: UserDto? = null
)
