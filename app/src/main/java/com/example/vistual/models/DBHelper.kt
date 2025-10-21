package com.example.vistual.models
 
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
 
// Esta clase me permitirá crear el esquema de base de datos
class DBHelper (context: Context):
    SQLiteOpenHelper(context, "closet_virtual.db", null, 1)
    {
        // Método para crear el esquema de base de datos
        override fun onCreate(db: SQLiteDatabase)
        {
            // Tabla para usuarios
            val crearTablaUsuarios = """CREATE TABLE usuarios(
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                nombre TEXT, 
                correo TEXT UNIQUE, 
                password TEXT
            )""".trimIndent()
            db.execSQL(crearTablaUsuarios) // ejecutamos la sentencia
            
            // Tabla para prendas de ropa
            val crearTablaPrendas = """CREATE TABLE prendas(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                categoria TEXT,
                color TEXT,
                imagen_path TEXT,
                usuario_id INTEGER,
                fecha_creacion TEXT,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
            )""".trimIndent()
            db.execSQL(crearTablaPrendas)
        }
 
        // Método para manejar versiones y cambios en la base de datos
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
        {
            db.execSQL("DROP TABLE IF EXISTS usuarios")
            db.execSQL("DROP TABLE IF EXISTS prendas")
            onCreate(db)
        }
        
        // Método para registrar un nuevo usuario
        fun registrarUsuario(nombre: String, correo: String, password: String): Boolean {
            val db = this.writableDatabase
            val valores = ContentValues()
            valores.put("nombre", nombre)
            valores.put("correo", correo)
            valores.put("password", password)
            
            return try {
                val resultado = db.insert("usuarios", null, valores)
                db.close()
                resultado != -1L
            } catch (e: Exception) {
                db.close()
                false
            }
        }
        
        // Método para validar login
        fun validarLogin(correo: String, password: String): Boolean {
            val db = this.readableDatabase
            val cursor: Cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE correo = ? AND password = ?", 
                arrayOf(correo, password)
            )
            val existe = cursor.count > 0
            cursor.close()
            db.close()
            return existe
        }
        
        // Método para obtener ID del usuario
        fun obtenerIdUsuario(correo: String): Int {
            val db = this.readableDatabase
            val cursor: Cursor = db.rawQuery(
                "SELECT id FROM usuarios WHERE correo = ?", 
                arrayOf(correo)
            )
            var id = -1
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0)
            }
            cursor.close()
            db.close()
            return id
        }
        
        // Método para agregar una prenda
        fun agregarPrenda(nombre: String, categoria: String, color: String, imagenPath: String, usuarioId: Int): Boolean {
            val db = this.writableDatabase
            val valores = ContentValues()
            valores.put("nombre", nombre)
            valores.put("categoria", categoria)
            valores.put("color", color)
            valores.put("imagen_path", imagenPath)
            valores.put("usuario_id", usuarioId)
            valores.put("fecha_creacion", System.currentTimeMillis().toString())
            
            return try {
                val resultado = db.insert("prendas", null, valores)
                db.close()
                resultado != -1L
            } catch (e: Exception) {
                db.close()
                false
            }
        }
        
        // Método para obtener todas las prendas de un usuario
        fun obtenerPrendasUsuario(usuarioId: Int): List<Map<String, String>> {
            val db = this.readableDatabase
            val prendas = mutableListOf<Map<String, String>>()
            val cursor: Cursor = db.rawQuery(
                "SELECT * FROM prendas WHERE usuario_id = ? ORDER BY fecha_creacion DESC", 
                arrayOf(usuarioId.toString())
            )
            
            if (cursor.moveToFirst()) {
                do {
                    val prenda = mapOf(
                        "id" to cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        "nombre" to cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        "categoria" to cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
                        "color" to cursor.getString(cursor.getColumnIndexOrThrow("color")),
                        "imagen_path" to cursor.getString(cursor.getColumnIndexOrThrow("imagen_path"))
                    )
                    prendas.add(prenda)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return prendas
        }
    }