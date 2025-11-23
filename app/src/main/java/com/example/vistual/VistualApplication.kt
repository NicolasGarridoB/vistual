package com.example.vistual

import android.app.Application
import android.content.Context
import com.example.vistual.db.AppDatabase
import com.example.vistual.repository.OutfitRepository
import com.example.vistual.repository.PrendaRepository
import com.example.vistual.repository.UserRepository

class VistualApplication : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val sharedPreferences by lazy {
        getSharedPreferences("VistualSession", Context.MODE_PRIVATE)
    }

    // Repositorios que usan la base de datos y/o SharedPreferences
    val prendaRepository by lazy { PrendaRepository(database.prendaDao()) }
    val outfitRepository by lazy { OutfitRepository(database.outfitDao()) }
    val userRepository by lazy { UserRepository(database.usuarioDao(), sharedPreferences) }
}
