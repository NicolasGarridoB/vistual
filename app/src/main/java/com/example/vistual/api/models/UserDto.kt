package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object para Usuario del API REST
 */
@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id")
    val id: Int,
    
    @Json(name = "nombre")
    val nombre: String,
    
    @Json(name = "correo")
    val correo: String
)
