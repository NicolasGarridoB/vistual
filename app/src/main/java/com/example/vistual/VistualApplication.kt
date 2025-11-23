package com.example.vistual

import android.app.Application
import android.content.Context
import com.example.vistual.db.AppDatabase
import com.example.vistual.repository.OutfitRepository
import com.example.vistual.repository.PrendaRepository
import com.example.vistual.repository.UserRepository

/**
 * Clase Application de la app.
 * Punto de entrada para inicializar componentes singleton como:
 * - Base de datos Room (BD Interna)
 * - SharedPreferences (sesión de usuario)
 * - Repositorios (capa de datos siguiendo MVVM)
 * 
 * Se ejecuta antes que cualquier Activity/Compose.
 */
class VistualApplication : Application() {

    /**
     * Instancia de la base de datos Room (BD Interna)
     * Lazy initialization: se crea solo cuando se necesita
     */
    private val database by lazy { AppDatabase.getDatabase(this) }
    
    /**
     * SharedPreferences para guardar sesión y preferencias del usuario
     */
    private val sharedPreferences by lazy {
        getSharedPreferences("VistualSession", Context.MODE_PRIVATE)
    }

    /**
     * Repositorios singleton que gestionan las fuentes de datos
     * Inyección manual de dependencias (en una app más grande usarías Hilt/Koin)
     */
    
    // Repository de prendas: BD interna (Room) + BD externa (Retrofit)
    val prendaRepository by lazy { 
        PrendaRepository(database.prendaDao(), sharedPreferences) 
    }
    
    // Repository de outfits: BD interna (Room) + BD externa (Retrofit)
    val outfitRepository by lazy { 
        OutfitRepository(database.outfitDao(), sharedPreferences) 
    }
    
    // Repository de usuarios: BD interna (Room) + BD externa (Retrofit) + SharedPreferences
    val userRepository by lazy { 
        UserRepository(database.usuarioDao(), sharedPreferences) 
    }
}
