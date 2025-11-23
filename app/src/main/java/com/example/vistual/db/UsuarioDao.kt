package com.example.vistual.db

import androidx.room.*
import com.example.vistual.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la entidad Usuario.
 * Define las operaciones de base de datos para usuarios (BD Interna con Room).
 * 
 * Todas las funciones son suspend para ejecutarse en coroutines de forma asíncrona.
 */
@Dao
interface UsuarioDao {

    /**
     * Inserta un nuevo usuario en la base de datos local
     * @param usuario Usuario a insertar
     * @return ID del usuario insertado (Long), -1 si falla
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsuario(usuario: Usuario): Long

    /**
     * Actualiza los datos de un usuario existente
     * @param usuario Usuario con datos actualizados
     */
    @Update
    suspend fun updateUsuario(usuario: Usuario)

    /**
     * Elimina un usuario de la base de datos
     * @param usuario Usuario a eliminar
     */
    @Delete
    suspend fun deleteUsuario(usuario: Usuario)

    /**
     * Obtiene un usuario por su correo electrónico
     * @param correo Correo del usuario
     * @return Usuario encontrado o null
     */
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getUsuarioByCorreo(correo: String): Usuario?

    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return Usuario encontrado o null
     */
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getUsuarioById(id: Int): Usuario?

    /**
     * Valida las credenciales de login de un usuario
     * @param correo Correo del usuario
     * @param password Contraseña del usuario
     * @return Usuario si las credenciales son válidas, null si no
     */
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND password = :password LIMIT 1")
    suspend fun validarCredenciales(correo: String, password: String): Usuario?

    /**
     * Obtiene todos los usuarios (útil para testing/admin)
     * @return Flow con la lista de todos los usuarios
     */
    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): Flow<List<Usuario>>

    /**
     * Verifica si existe un usuario con un correo específico
     * @param correo Correo a verificar
     * @return true si existe, false si no
     */
    @Query("SELECT COUNT(*) > 0 FROM usuarios WHERE correo = :correo")
    suspend fun existeUsuarioConCorreo(correo: String): Boolean
}
