package com.example.vistual.model

import androidx.room.TypeConverter

class PrendaIdConverter {
    @TypeConverter
    fun fromPrendaIdList(prendaIds: List<Int>): String {
        return prendaIds.joinToString(",")
    }

    @TypeConverter
    fun toPrendaIdList(data: String): List<Int> {
        return data.split(",".toRegex()).map { it.toInt() }
    }
}
