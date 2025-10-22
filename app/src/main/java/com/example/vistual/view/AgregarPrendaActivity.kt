package com.example.vistual.models

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, "closet_virtual.db", null, 1) {

    companion object {
        // Tabla Usuarios (sin cambios)
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_USUARIO_ID = "id"
        private const val COL_USUARIO_NOMBRE_COMPLETO = "nombre"
        private const val COL_USUARIO_CORREO = "correo"
        private const val COL_USUARIO_PASSWORD = "password"

        // --- INICIO: CAMBIOS EN LA TABLA PRENDAS ---
        private const val TABLE_PRENDAS = "prendas"
        private const val COL_PRENDA_ID = "id"
        // Nuevas columnas
        private const val COL_PRENDA_NOMBRE = "nombre_prenda"
        private const val COL_PRENDA_COLOR = "color_prenda"
        // Columnas existentes
        private const val COL_PRENDA_IMAGEN_PATH = "imagen_path"
        private const val COL_PRENDA_CATEGORIA = "categoria"
        private const val COL_PRENDA_USUARIO_ID = "usuario_id"
        // --- FIN: CAMBIOS EN LA TABLA PRENDAS ---
    }

    override fun onCreate(db: SQLiteDatabase) {
        val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS(
                $COL_USUARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $COL_USUARIO_NOMBRE_COMPLETO TEXT, 
                $COL_USUARIO_CORREO TEXT UNIQUE, 
                $COL_USUARIO_PASSWORD TEXT
            )""".trimIndent()
        db.execSQL(crearTablaUsuarios)

        // --- Sentencia CREATE TABLE actualizada ---
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
            put(COL_USUARIO_NOMBRE_COMPLETO, nombre)
            put(COL_USUARIO_CORREO, correo)
            put(COL_USUARIO_PASSWORD, password)
        }
        val resultado = db.insert(TABLE_USUARIOS, null, valores)
        db.close()
        return resultado != -1L
    }

    fun validarLogin(correo: String, password: String): Boolean {
        // ... (sin cambios)
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
        // ... (sin cambios)
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT $COL_USUARIO_ID FROM $TABLE_USUARIOS WHERE $COL_USUARIO_CORREO = ?",
            arrayOf(correo)
        )
        var id = -1
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USUARIO_ID))
        }
        cursor.close()
        db.close()
        return id
    }

    // --- INICIO: FUNCIÓN agregarPrenda CORREGIDA Y ACTUALIZADA ---
    // Ahora recibe todos los parámetros y devuelve un Boolean para saber si tuvo éxito.
    fun agregarPrenda(nombre: String, categoria: String, color: String, rutaImagen: String, idUsuario: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_PRENDA_NOMBRE, nombre)
            put(COL_PRENDA_CATEGORIA, categoria)
            put(COL_PRENDA_COLOR, color)
            put(COL_PRENDA_IMAGEN_PATH, rutaImagen)
            put(COL_PRENDA_USUARIO_ID, idUsuario)
        }
        val resultado = db.insert(TABLE_PRENDAS, null, values)
        db.close()
        // db.insert() devuelve -1 si hay un error, o el ID de la fila si tiene éxito.
        return resultado != -1L
    }
    // --- FIN: FUNCIÓN agregarPrenda ---

    fun obtenerPrendasUsuario(idUsuario: Int): List<Prenda> {
        // ... (sin cambios en la lógica, pero necesita actualizarse si Prenda cambia)
        val db = this.readableDatabase
        val prendas = mutableListOf<Prenda>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_PRENDAS WHERE $COL_PRENDA_USUARIO_ID = ?",
            arrayOf(idUsuario.toString())
        )

        if (cursor.moveToFirst()) {
            do {
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
