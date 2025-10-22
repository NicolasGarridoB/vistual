package com.example.vistual.models

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, "closet_virtual.db", null, 1) {

    companion object {
        // Tabla Usuarios
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_USUARIO_ID = "id"
        private const val COL_USUARIO_NOMBRE = "nombre"
        private const val COL_USUARIO_CORREO = "correo"
        private const val COL_USUARIO_PASSWORD = "password"

        // Tabla Prendas
        private const val TABLE_PRENDAS = "prendas"
        private const val COL_PRENDA_ID = "id"
        private const val COL_PRENDA_NOMBRE = "nombre"
        private const val COL_PRENDA_COLOR = "color"
        private const val COL_PRENDA_IMAGEN_PATH = "imagen_path"
        private const val COL_PRENDA_CATEGORIA = "categoria"
        private const val COL_PRENDA_USUARIO_ID = "usuario_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Habilitar claves foráneas
        db.execSQL("PRAGMA foreign_keys=ON")
        
        val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS(
                $COL_USUARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USUARIO_NOMBRE TEXT,
                $COL_USUARIO_CORREO TEXT UNIQUE,
                $COL_USUARIO_PASSWORD TEXT
            )""".trimIndent()
        db.execSQL(crearTablaUsuarios)

        val crearTablaPrendas = """
            CREATE TABLE $TABLE_PRENDAS(
                $COL_PRENDA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PRENDA_NOMBRE TEXT,
                $COL_PRENDA_COLOR TEXT,
                $COL_PRENDA_IMAGEN_PATH TEXT,
                $COL_PRENDA_CATEGORIA TEXT,
                $COL_PRENDA_USUARIO_ID INTEGER,
                FOREIGN KEY($COL_PRENDA_USUARIO_ID) REFERENCES $TABLE_USUARIOS($COL_USUARIO_ID)
            )""".trimIndent()
        db.execSQL(crearTablaPrendas)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRENDAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    fun registrarUsuario(nombre: String, correo: String, password: String): Boolean {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put(COL_USUARIO_NOMBRE, nombre)
            put(COL_USUARIO_CORREO, correo)
            put(COL_USUARIO_PASSWORD, password)
        }
        val resultado = db.insert(TABLE_USUARIOS, null, valores)
        db.close()
        return resultado != -1L
    }

    fun validarLogin(correo: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USUARIOS WHERE $COL_USUARIO_CORREO = ? AND $COL_USUARIO_PASSWORD = ?",
            arrayOf(correo, password)
        )
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    fun obtenerIdUsuario(correo: String): Int {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT $COL_USUARIO_ID FROM $TABLE_USUARIOS WHERE $COL_USUARIO_CORREO = ?",
            arrayOf(correo)
        )
        var id = -1
        if (cursor.moveToFirst()) {
            // Usamos getColumnIndexOrThrow para evitar errores si la columna no existe
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USUARIO_ID))
        }
        cursor.close()
        db.close()
        return id
    }

    fun agregarPrenda(nombre: String, categoria: String, color: String, rutaImagen: String, idUsuario: Int): Boolean {
        return try {
            // Verificar que el usuario existe
            if (!verificarUsuarioExiste(idUsuario)) {
                println("DBHelper - Error: Usuario con ID $idUsuario no existe")
                return false
            }
            
            val db = this.writableDatabase
            // Habilitar claves foráneas para esta conexión
            db.execSQL("PRAGMA foreign_keys=ON")
            val values = ContentValues().apply {
                put(COL_PRENDA_NOMBRE, nombre)
                put(COL_PRENDA_CATEGORIA, categoria)
                put(COL_PRENDA_COLOR, color)
                put(COL_PRENDA_IMAGEN_PATH, rutaImagen)
                put(COL_PRENDA_USUARIO_ID, idUsuario)
            }
            
            println("DBHelper - Insertando prenda con valores:")
            println("- Nombre: '$nombre'")
            println("- Categoría: '$categoria'")
            println("- Color: '$color'")
            println("- Imagen: '$rutaImagen'")
            println("- Usuario ID: $idUsuario")
            
            val resultado = db.insert(TABLE_PRENDAS, null, values)
            
            val exito = resultado != -1L
            println("DBHelper - Resultado insert: $resultado, Éxito: $exito")
            
            if (!exito) {
                println("DBHelper - Insert falló, posibles causas:")
                println("  - Restricción de clave foránea")
                println("  - Datos inválidos")
                println("  - Error de base de datos")
                
                // Intentar insert sin restricciones para diagnosticar
                try {
                    db.execSQL("PRAGMA foreign_keys=OFF")
                    val resultadoSinFK = db.insert(TABLE_PRENDAS, null, values)
                    println("DBHelper - Insert sin FK: $resultadoSinFK")
                    if (resultadoSinFK != -1L) {
                        println("DBHelper - El problema ES la clave foránea")
                    } else {
                        println("DBHelper - El problema NO es la clave foránea")
                    }
                    db.execSQL("PRAGMA foreign_keys=ON")
                } catch (e2: Exception) {
                    println("DBHelper - Error en diagnóstico: ${e2.message}")
                }
            }
            
            db.close()
            exito
        } catch (e: Exception) {
            println("DBHelper - Error al insertar prenda: ${e.message}")
            println("DBHelper - Tipo de error: ${e::class.java.simpleName}")
            e.printStackTrace()
            false
        }
    }

    fun eliminarPrenda(idPrenda: Int): Boolean {
        val db = this.writableDatabase
        val resultado = db.delete(TABLE_PRENDAS, "$COL_PRENDA_ID = ?", arrayOf(idPrenda.toString()))
        db.close()
        return resultado > 0
    }

    private fun verificarUsuarioExiste(idUsuario: Int): Boolean {
        return try {
            val db = this.readableDatabase
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM $TABLE_USUARIOS WHERE $COL_USUARIO_ID = ?",
                arrayOf(idUsuario.toString())
            )
            cursor.moveToFirst()
            val count = cursor.getInt(0)
            cursor.close()
            db.close()
            
            val existe = count > 0
            println("DBHelper - Usuario ID $idUsuario existe: $existe")
            existe
        } catch (e: Exception) {
            println("DBHelper - Error verificando usuario: ${e.message}")
            false
        }
    }

    fun mostrarUsuariosExistentes() {
        try {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT $COL_USUARIO_ID, $COL_USUARIO_CORREO FROM $TABLE_USUARIOS", null)
            
            println("DBHelper - Usuarios existentes en la base de datos:")
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USUARIO_ID))
                    val correo = cursor.getString(cursor.getColumnIndexOrThrow(COL_USUARIO_CORREO))
                    println("  - ID: $id, Correo: $correo")
                } while (cursor.moveToNext())
            } else {
                println("  - No hay usuarios en la base de datos")
            }
            
            cursor.close()
            db.close()
        } catch (e: Exception) {
            println("DBHelper - Error mostrando usuarios: ${e.message}")
        }
    }

    fun mostrarEstructuraTabla() {
        try {
            val db = this.readableDatabase
            val cursor = db.rawQuery("PRAGMA table_info($TABLE_PRENDAS)", null)
            
            println("DBHelper - Estructura de la tabla $TABLE_PRENDAS:")
            if (cursor.moveToFirst()) {
                do {
                    val columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val columnType = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                    println("  - Columna: $columnName (Tipo: $columnType)")
                } while (cursor.moveToNext())
            }
            
            cursor.close()
            db.close()
        } catch (e: Exception) {
            println("DBHelper - Error mostrando estructura: ${e.message}")
        }
    }

    fun obtenerPrendasUsuario(idUsuario: Int): List<Prenda> {
        val db = this.readableDatabase
        val prendas = mutableListOf<Prenda>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_PRENDAS WHERE $COL_PRENDA_USUARIO_ID = ?",
            arrayOf(idUsuario.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                // Asumimos que la clase Prenda solo necesita id, rutaImagen, tipo y idUsuario.
                // Si la clase Prenda también tiene 'nombre' y 'color', debes añadirlos aquí.
                val prenda = Prenda(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRENDA_ID)),
                    rutaImagen = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRENDA_IMAGEN_PATH)),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRENDA_CATEGORIA)),
                    idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRENDA_USUARIO_ID))
                )
                prendas.add(prenda)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return prendas
    }
}


