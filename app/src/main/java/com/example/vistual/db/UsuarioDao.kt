package com.example.vistual.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vistual.model.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsuario(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getUsuarioByCorreo(correo: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND password = :password LIMIT 1")
    suspend fun validarCredenciales(correo: String, password: String): Usuario?
}
