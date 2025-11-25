package com.example.vistual.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object para Usuario del API REST
 * Coincide con la tabla 'user' de XANO
 */
@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id")
    val id: Int,
    
    @Json(name = "name")
    val nombre: String,
    
    @Json(name = "email")
    val correo: String,
    
    @Json(name = "account_id")
    val accountId: Int? = null,
    
    @Json(name = "role")
    val role: String? = null,
    
    @Json(name = "created_at")
    val createdAt: Long? = null,
    
    @Json(name = "updated_at")
    val updatedAt: Long? = null
)
