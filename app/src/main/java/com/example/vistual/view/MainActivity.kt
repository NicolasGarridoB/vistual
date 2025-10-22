package com.example.vistual.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vistual.R
import com.example.vistual.models.DBHelper
import com.example.vistual.models.Prenda
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var sharedPreferences: SharedPreferences
    private var usuarioId: Int = -1


    private lateinit var topsAdapter: PrendaAdapter
    private lateinit var bottomsAdapter: PrendaAdapter


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {

                abrirSelectorDeImagen()
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
            }
        }


    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {

                mostrarDialogoTipoPrenda(it)
            }
        }

    // --- FIN: Nuevo código ---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)
        sharedPreferences = getSharedPreferences("ClosetVirtual", MODE_PRIVATE)

        // Verificar si el usuario está logueado
        if (!sharedPreferences.getBoolean("logged_in", false)) {
            irALogin()
            return
        }

        usuarioId = sharedPreferences.getInt("id_usuario", -1)

        inicializarVistas()
        cargarPrendas()
    }

    private fun inicializarVistas() {

        val recyclerViewTops = findViewById<RecyclerView>(R.id.recycler_view_tops)
        val recyclerViewBottoms = findViewById<RecyclerView>(R.id.recycler_view_bottoms)
        val fabAgregarPrenda = findViewById<FloatingActionButton>(R.id.fab_agregar_prenda)


        topsAdapter = PrendaAdapter(mutableListOf())
        bottomsAdapter = PrendaAdapter(mutableListOf())

        recyclerViewTops.adapter = topsAdapter
        recyclerViewBottoms.adapter = bottomsAdapter


        fabAgregarPrenda.setOnClickListener {
            verificarPermisoAlmacenamiento()
        }
    }



    private fun verificarPermisoAlmacenamiento() {

        val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permissionToRequest) == PackageManager.PERMISSION_GRANTED -> {

                abrirSelectorDeImagen()
            }
            shouldShowRequestPermissionRationale(permissionToRequest) -> {

                Toast.makeText(this, "Se necesita acceso a la galería para agregar prendas.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(permissionToRequest)
            }
            else -> {

                requestPermissionLauncher.launch(permissionToRequest)
            }
        }
    }

    private fun abrirSelectorDeImagen() {

        seleccionarImagenLauncher.launch("image/png")
    }

    private fun mostrarDialogoTipoPrenda(imagenUri: Uri) {
        val tipos = arrayOf("Parte Superior", "Parte Inferior")
        AlertDialog.Builder(this)
            .setTitle("¿Qué tipo de prenda es?")
            .setItems(tipos) { dialog, which ->
                val tipoSeleccionado = if (which == 0) "TOP" else "BOTTOM"
                // Persistimos el permiso para poder acceder a la URI más tarde
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(imagenUri, flag)

                guardarPrendaEnDB(imagenUri, tipoSeleccionado)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarPrendaEnDB(imagenUri: Uri, tipo: String) {
        // Valores predeterminados para los nuevos campos.
        val nombrePredeterminado = "Prenda" // Puedes dejarlo vacío: ""
        val colorPredeterminado = "Sin color"  // Puedes dejarlo vacío: ""

        // Llamada corregida con los parámetros en el orden correcto
        dbHelper.agregarPrenda(
            nombre = nombrePredeterminado,
            categoria = tipo,
            color = colorPredeterminado,
            rutaImagen = imagenUri.toString(),
            idUsuario = usuarioId
        )

        // El resto de la función sigue igual
        cargarPrendas()
        Toast.makeText(this, "Prenda guardada con éxito", Toast.LENGTH_SHORT).show()
    }




    private fun cargarPrendas() {
        if (usuarioId == -1) return

        val todasLasPrendas = dbHelper.obtenerPrendasUsuario(usuarioId)


        val prendasSuperiores = todasLasPrendas.filter { it.tipo == "TOP" }
        val prendasInferiores = todasLasPrendas.filter { it.tipo == "BOTTOM" }

        topsAdapter.actualizarPrendas(prendasSuperiores)
        bottomsAdapter.actualizarPrendas(prendasInferiores)
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
