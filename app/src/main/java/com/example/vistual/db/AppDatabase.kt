package com.example.vistual.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vistual.model.Outfit
import com.example.vistual.model.Prenda
import com.example.vistual.model.PrendaIdConverter
import com.example.vistual.model.Usuario

@Database(entities = [Prenda::class, Outfit::class, Usuario::class], version = 2, exportSchema = false) // Incrementar versión por cambio de schema
@TypeConverters(PrendaIdConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun prendaDao(): PrendaDao
    abstract fun outfitDao(): OutfitDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vistual_database"
                )
                .fallbackToDestructiveMigration() // Añadido para manejar la migración de forma simple
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
