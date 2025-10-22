package com.example.vistual.models

// Alias para mantener compatibilidad con el código existente
typealias ClothingItem = Prenda

// Funciones de extensión para ClothingItem/Prenda
fun ClothingItem.getClothingType(): ClothingType {
    return ClothingType.fromString(this.tipo)
}

fun ClothingItem.isTop(): Boolean {
    return getClothingType() == ClothingType.TOP
}

fun ClothingItem.isBottom(): Boolean {
    return getClothingType() == ClothingType.BOTTOM
}