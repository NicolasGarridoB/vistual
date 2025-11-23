package com.example.vistual.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "outfits")
@TypeConverters(PrendaIdConverter::class)
data class Outfit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val prendasIds: List<Int>, // Almacenará los IDs de las prendas que componen el outfit
    val usuarioId: Int = 0 // Usuario dueño del outfit
)
