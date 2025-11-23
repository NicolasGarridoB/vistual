package com.example.vistual.network.models

import com.google.gson.annotations.SerializedName

/**
 * Modelos de datos para la comunicación con la API REST (BD Externa).
 * Estos modelos representan los JSON que se envían y reciben del servidor.
 * 
 * @SerializedName permite mapear nombres de campos del JSON a propiedades de Kotlin
 */

// ==================== MODELOS DE USUARIO ====================

/**
 * Request para registro de usuario
 */
data class RegistroRequest(
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("correo")
    val correo: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * Request para login
 */
data class LoginRequest(
    @SerializedName("correo")
    val correo: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * Response de login con token de autenticación
 */
data class LoginResponse(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("usuario")
    val usuario: UsuarioResponse,
    
    @SerializedName("mensaje")
    val mensaje: String? = null
)

/**
 * Response con datos del usuario
 */
data class UsuarioResponse(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("correo")
    val correo: String,
    
    @SerializedName("fechaCreacion")
    val fechaCreacion: String? = null
)

// ==================== MODELOS DE PRENDA ====================

/**
 * Request para crear/actualizar prenda
 */
data class PrendaRequest(
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("categoria")
    val categoria: String,
    
    @SerializedName("color")
    val color: String,
    
    @SerializedName("imagenPath")
    val imagenPath: String,
    
    @SerializedName("usuarioId")
    val usuarioId: Int
)

/**
 * Response de prenda desde el servidor
 */
data class PrendaResponse(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("categoria")
    val categoria: String,
    
    @SerializedName("color")
    val color: String,
    
    @SerializedName("imagenPath")
    val imagenPath: String,
    
    @SerializedName("usuarioId")
    val usuarioId: Int,
    
    @SerializedName("fechaCreacion")
    val fechaCreacion: String,
    
    @SerializedName("sincronizado")
    val sincronizado: Boolean = true
)

// ==================== MODELOS DE OUTFIT ====================

/**
 * Request para crear outfit
 */
data class OutfitRequest(
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("prendasIds")
    val prendasIds: List<Int>,
    
    @SerializedName("usuarioId")
    val usuarioId: Int
)

/**
 * Response de outfit desde el servidor
 */
data class OutfitResponse(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("prendasIds")
    val prendasIds: List<Int>,
    
    @SerializedName("usuarioId")
    val usuarioId: Int,
    
    @SerializedName("fechaCreacion")
    val fechaCreacion: String
)

// ==================== MODELOS DE SINCRONIZACIÓN ====================

/**
 * Request para sincronizar todos los datos del usuario
 */
data class SyncRequest(
    @SerializedName("usuarioId")
    val usuarioId: Int,
    
    @SerializedName("prendas")
    val prendas: List<PrendaRequest>,
    
    @SerializedName("outfits")
    val outfits: List<OutfitRequest>,
    
    @SerializedName("ultimaSync")
    val ultimaSync: String
)

/**
 * Response de sincronización
 */
data class SyncResponse(
    @SerializedName("exito")
    val exito: Boolean,
    
    @SerializedName("mensaje")
    val mensaje: String,
    
    @SerializedName("prendasSincronizadas")
    val prendasSincronizadas: Int,
    
    @SerializedName("outfitsSincronizados")
    val outfitsSincronizados: Int,
    
    @SerializedName("fechaSync")
    val fechaSync: String
)

// ==================== MODELOS GENÉRICOS ====================

/**
 * Response genérica para mensajes
 */
data class MessageResponse(
    @SerializedName("mensaje")
    val mensaje: String,
    
    @SerializedName("exito")
    val exito: Boolean = true
)

/**
 * Response genérica para errores
 */
data class ErrorResponse(
    @SerializedName("error")
    val error: String,
    
    @SerializedName("codigo")
    val codigo: Int,
    
    @SerializedName("detalles")
    val detalles: String? = null
)
