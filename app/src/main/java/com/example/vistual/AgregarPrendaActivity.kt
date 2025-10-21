package com.example.vistual

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.vistual.models.DBHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AgregarPrendaActivity : AppCompatActivity() {
    
    private lateinit var dbHelper: DBHelper
    private lateinit var sharedPreferences: SharedPreferences
    private var imagenPath: String? = null
    private var usuarioId: Int = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_prenda)
        
        dbHelper = DBHelper(this)
        sharedPreferences = getSharedPreferences("ClosetVirtual", MODE_PRIVATE)
        usuarioId = sharedPreferences.getInt("id_usuario", -1)
        imagenPath = intent.getStringExtra("imagen_path")
        
        inicializarVistas()
    }
    
    private fun inicializarVistas() {
        val ivPreview = findViewById<ImageView>(R.id.iv_preview_prenda)
        val etNombre = findViewById<EditText>(R.id.et_nombre_prenda)
        val spinnerCategoria = findViewById<Spinner>(R.id.spinner_categoria)
        val etColor = findViewById<EditText>(R.id.et_color_prenda)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar_prenda)
        val btnCancelar = findViewById<Button>(R.id.btn_cancelar)
        
        // Mostrar imagen capturada
        if (!imagenPath.isNullOrEmpty()) {
            val bitmap = BitmapFactory.decodeFile(imagenPath)
            ivPreview.setImageBitmap(bitmap)
        }
        
        // Configurar spinner de categorías
        val categorias = arrayOf("Camisas", "Pantalones", "Zapatos", "Accesorios", "Chaquetas", "Vestidos", "Faldas", "Otros")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        spinnerCategoria.adapter = adapter
        
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val categoria = spinnerCategoria.selectedItem.toString()
            val color = etColor.text.toString().trim()
            
            if (nombre.isEmpty() || color.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Guardar imagen permanentemente
            val imagenPermanente = guardarImagenPermanente()
            
            if (dbHelper.agregarPrenda(nombre, categoria, color, imagenPermanente, usuarioId)) {
                Toast.makeText(this, "Prenda agregada exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al agregar la prenda", Toast.LENGTH_SHORT).show()
            }
        }
        
        btnCancelar.setOnClickListener {
            // Eliminar imagen temporal
            if (!imagenPath.isNullOrEmpty()) {
                File(imagenPath!!).delete()
            }
            finish()
        }
    }
    
    private fun guardarImagenPermanente(): String {
        if (imagenPath.isNullOrEmpty()) return ""
        
        val originalFile = File(imagenPath!!)
        val permanentFile = File(filesDir, "prenda_${System.currentTimeMillis()}.jpg")
        
        try {
            val inputStream = FileInputStream(originalFile)
            val outputStream = FileOutputStream(permanentFile)
            
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            
            inputStream.close()
            outputStream.close()
            
            // Eliminar archivo temporal
            originalFile.delete()
            
            return permanentFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}