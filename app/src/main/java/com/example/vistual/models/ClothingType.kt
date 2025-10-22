package com.example.vistual.models

enum class ClothingType(val displayName: String) {
    TOP("Superior"),
    BOTTOM("Inferior"),
    DRESS("Vestido"),
    SHOES("Zapatos"),
    ACCESSORY("Accesorio");

    companion object {
        fun fromString(value: String): ClothingType {
            return when (value.lowercase()) {
                "superior", "top" -> TOP
                "inferior", "bottom" -> BOTTOM
                "vestido", "dress" -> DRESS
                "zapatos", "shoes" -> SHOES
                "accesorio", "accessory" -> ACCESSORY
                else -> TOP // Default value
            }
        }
    }
}