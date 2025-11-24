package com.example.vistual.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Data class que representa una prenda de vestir, ahora como una entidad de Room.
 * Cumple con el requisito de "Una clase" de la rúbrica.
 */
@Entity(tableName = "prendas")
@TypeConverters(EnumConverter::class)
data class Prenda(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val categoria: CategoriaPrenda,
    val color: ColorPrenda,
    val imagenPath: String,
    val usuarioId: Int,
    val fechaCreacion: String = System.currentTimeMillis().toString()
)

/**
 * Estados para el manejo de prendas en ViewModels
 */
data class PrendasState(
    val isLoading: Boolean = false,
    val prendas: List<Prenda> = emptyList(),
    val errorMessage: String? = null
)

/**
 * Estado para agregar una nueva prenda
 */
data class AgregarPrendaState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val imagenPath: String? = null
)

/**
 * Enum para las categorías de prendas
 */
enum class CategoriaPrenda(val displayName: String, val seccion: SeccionOutfit) {
    PARTE_SUPERIOR("Parte Superior", SeccionOutfit.PARTE_SUPERIOR),
    PARTE_INFERIOR("Parte Inferior", SeccionOutfit.PARTE_INFERIOR),
    ZAPATOS("Zapatos", SeccionOutfit.ZAPATOS)
}

/**
 * Enum para las secciones de outfit (para vista carrusel)
 */
enum class SeccionOutfit(val displayName: String) {
    PARTE_SUPERIOR("Parte Superior"),
    PARTE_INFERIOR("Parte Inferior"),
    ZAPATOS("Zapatos")
}

/**
 * Enum para los colores de prendas
 */
enum class ColorPrenda(val displayName: String, val hexColor: String) {
    NEGRO("Negro", "#000000"),
    BLANCO("Blanco", "#FFFFFF"),
    AZUL("Azul", "#0000FF"),
    ROJO("Rojo", "#FF0000"),
    VERDE("Verde", "#00FF00"),
    AMARILLO("Amarillo", "#FFFF00"),
    ROSA("Rosa", "#FFC0CB"),
    GRIS("Gris", "#808080"),
    MARRON("Marrón", "#8B4513"),
    MORADO("Morado", "#800080")
}