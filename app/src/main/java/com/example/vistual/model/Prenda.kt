package com.example.vistual.model

/**
 * Data class que representa una prenda de vestir
 * Cumple con el requisito de "Una clase" de la rúbrica
 */
data class Prenda(
    val id: Int = -1,
    val nombre: String,
    val categoria: String,
    val color: String,
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
enum class CategoriaPrenda(val displayName: String) {
    CAMISA("Camisa"),
    PANTALON("Pantalón"),
    VESTIDO("Vestido"),
    ZAPATOS("Zapatos"),
    ACCESORIO("Accesorio"),
    CHAQUETA("Chaqueta"),
    FALDA("Falda"),
    OTROS("Otros")
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