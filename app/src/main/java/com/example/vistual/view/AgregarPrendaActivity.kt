package com.example.vistual.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vistual.R
import com.example.vistual.models.DBHelper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AgregarPrendaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var sharedPreferences: SharedPreferences
    private var usuarioId: Int = -1

    private lateinit var ivPreviewPrenda: ImageView
    private lateinit var etNombrePrenda: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var etColorPrenda: EditText
    private lateinit var btnGuardarPrenda: Button
    private lateinit var btnCancelar: Button

    private var imagenUri: Uri? = null
    private var rutaImagenGuardada: String? = null

    private val PERMISSION_REQUEST_CODE = 1001

    private val seleccionarImagen = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imagenUri = it
            ivPreviewPrenda.setImageURI(it)
            guardarImagenEnAlmacenamientoInterno(it)
        }
    }

    private val solicitarPermisos = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val todosPermitidos = permissions.entries.all { it.value }
        if (todosPermitidos) {
            abrirGaleria()
        } else {
            Toast.makeText(this, "Se necesitan permisos para acceder a las imágenes", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_prenda)

        inicializarVistas()
        configurarSpinner()
        configurarEventos()
        obtenerDatosUsuario()
        dbHelper = DBHelper(this)
    }

    private fun inicializarVistas() {
        ivPreviewPrenda = findViewById(R.id.iv_preview_prenda)
        etNombrePrenda = findViewById(R.id.et_nombre_prenda)
        spinnerCategoria = findViewById(R.id.spinner_categoria)
        etColorPrenda = findViewById(R.id.et_color_prenda)
        btnGuardarPrenda = findViewById(R.id.btn_guardar_prenda)
        btnCancelar = findViewById(R.id.btn_cancelar)
    }

    private fun configurarSpinner() {
        val categorias = arrayOf("Seleccionar categoría", "Top", "Bottom", "Zapatos", "Accesorios")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapter
    }

    private fun configurarEventos() {
        ivPreviewPrenda.setOnClickListener {
            verificarYSolicitarPermisos()
        }

        btnGuardarPrenda.setOnClickListener {
            guardarPrenda()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun verificarYSolicitarPermisos() {
        val permisosNecesarios = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val permisosDenegados = permisosNecesarios.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permisosDenegados.isNotEmpty()) {
            solicitarPermisos.launch(permisosDenegados.toTypedArray())
        } else {
            abrirGaleria()
        }
    }

    private fun abrirGaleria() {
        seleccionarImagen.launch("image/*")
    }

    private fun obtenerDatosUsuario() {
        sharedPreferences = getSharedPreferences("ClosetVirtual", MODE_PRIVATE)
        usuarioId = sharedPreferences.getInt("id_usuario", -1)
        
        if (usuarioId == -1) {
            Toast.makeText(this, "Error: Usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun guardarImagenEnAlmacenamientoInterno(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Toast.makeText(this, "No se pudo leer la imagen seleccionada", Toast.LENGTH_SHORT).show()
                return
            }
            
            val nombreArchivo = "prenda_${System.currentTimeMillis()}.jpg"
            val archivo = File(filesDir, nombreArchivo)
            val outputStream = FileOutputStream(archivo)

            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            rutaImagenGuardada = archivo.absolutePath
            Toast.makeText(this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar la imagen: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun guardarPrenda() {
        val nombre = etNombrePrenda.text.toString().trim()
        val categoria = spinnerCategoria.selectedItem.toString()
        val color = etColorPrenda.text.toString().trim()
        
        // Debug: Verificar la categoría seleccionada
        println("Categoría seleccionada: '$categoria'")
        println("Posición del spinner: ${spinnerCategoria.selectedItemPosition}")

        when {
            nombre.isEmpty() -> {
                Toast.makeText(this, "Ingrese el nombre de la prenda", Toast.LENGTH_SHORT).show()
                return
            }
            categoria == "Seleccionar categoría" || spinnerCategoria.selectedItemPosition == 0 -> {
                Toast.makeText(this, "Seleccione una categoría válida", Toast.LENGTH_SHORT).show()
                return
            }
            color.isEmpty() -> {
                Toast.makeText(this, "Ingrese el color de la prenda", Toast.LENGTH_SHORT).show()
                return
            }
            rutaImagenGuardada.isNullOrEmpty() -> {
                Toast.makeText(this, "Seleccione una imagen", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Debug: mostrar valores antes de guardar
        println("Debug - Guardando prenda:")
        println("- Nombre: '$nombre'")
        println("- Categoría: '$categoria'")
        println("- Color: '$color'")
        println("- Ruta imagen: '$rutaImagenGuardada'")
        println("- Usuario ID: $usuarioId")
        
        // Debug: mostrar usuarios existentes y estructura de tabla
        dbHelper.mostrarUsuariosExistentes()
        dbHelper.mostrarEstructuraTabla()

        try {
            val exito = dbHelper.agregarPrenda(nombre, categoria, color, rutaImagenGuardada!!, usuarioId)
            
            if (exito) {
                Toast.makeText(this, "Prenda agregada exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al agregar la prenda en la base de datos", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}
