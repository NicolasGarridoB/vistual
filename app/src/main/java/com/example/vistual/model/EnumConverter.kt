package com.example.vistual.model

import androidx.room.TypeConverter

class EnumConverter {
    @TypeConverter
    fun fromCategoriaPrenda(value: CategoriaPrenda): String {
        return value.name
    }

    @TypeConverter
    fun toCategoriaPrenda(value: String): CategoriaPrenda {
        return CategoriaPrenda.valueOf(value)
    }

    @TypeConverter
    fun fromColorPrenda(value: ColorPrenda): String {
        return value.name
    }

    @TypeConverter
    fun toColorPrenda(value: String): ColorPrenda {
        return ColorPrenda.valueOf(value)
    }
}
