package com.example.vistual

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vistual.models.DBHelper
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    
    private lateinit var dbHelper: DBHelper
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gridViewPrendas: GridView
    private lateinit var adapter: PrendasAdapter
    private var usuarioId: Int = -1
    private val CAMERA_REQUEST_CODE = 100
    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        dbHelper = DBHelper(this)
        sharedPreferences = getSharedPreferences("ClosetVirtual", MODE_PRIVATE)
        
        // Verificar si el usuario está logueado
        if (!sharedPreferences.getBoolean("logged_in", false)) {
            irALogin()
            return
        }
        
        usuarioId = sharedPreferences.getInt("id_usuario", -1)
        val correoUsuario = sharedPreferences.getString("correo_usuario", "Usuario")
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        inicializarVistas(correoUsuario ?: "Usuario")
        cargarPrendas()
    }
    
    private fun inicializarVistas(correoUsuario: String) {
        val tvBienvenida = findViewById<TextView>(R.id.tv_bienvenida)
        val btnAgregarPrenda = findViewById<Button>(R.id.btn_agregar_prenda)
        val btnCerrarSesion = findViewById<Button>(R.id.btn_cerrar_sesion)
        gridViewPrendas = findViewById(R.id.grid_prendas)
        
        tvBienvenida.text = "Bienvenido, $correoUsuario"
        
        btnAgregarPrenda.setOnClickListener {
            verificarPermisosCamara()
        }
        
        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
        
        adapter = PrendasAdapter(this, mutableListOf())
        gridViewPrendas.adapter = adapter
    }
    
    private fun verificarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            abrirCamara()
        }
    }
    
    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirCamara()
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            mostrarDialogoAgregarPrenda(imageBitmap)
        }
    }
    
    private fun mostrarDialogoAgregarPrenda(bitmap: Bitmap) {
        val intent = Intent(this, AgregarPrendaActivity::class.java)
        // Guardar imagen temporalmente y pasar la ruta
        val tempImagePath = guardarImagenTemporal(bitmap)
        intent.putExtra("imagen_path", tempImagePath)
        startActivity(intent)
    }
    
    private fun guardarImagenTemporal(bitmap: Bitmap): String {
        val file = File(filesDir, "temp_${System.currentTimeMillis()}.jpg")
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }
    
    private fun cargarPrendas() {
        val prendas = dbHelper.obtenerPrendasUsuario(usuarioId)
        adapter.actualizarPrendas(prendas)
    }
    
    private fun cerrarSesion() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        irALogin()
    }
    
    private fun irALogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    override fun onResume() {
        super.onResume()
        if (usuarioId != -1) {
            cargarPrendas()
        }
    }
}