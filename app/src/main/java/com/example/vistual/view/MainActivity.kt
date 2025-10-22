package com.example.vistual.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    private lateinit var zapatosAdapter: PrendaAdapter
    private lateinit var accesoriosAdapter: PrendaAdapter


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
        
        // Migración de datos antiguos si es necesario
        if (usuarioId == -1) {
            migrarDatosAntiguos()
            usuarioId = sharedPreferences.getInt("id_usuario", -1)
        }

        configurarToolbar()
        inicializarVistas()
        cargarPrendas()
    }

    private fun migrarDatosAntiguos() {
        // Intentar obtener datos de otras posibles ubicaciones de SharedPreferences
        val oldPrefs1 = getSharedPreferences("usuario_data", MODE_PRIVATE)
        val oldUserId1 = oldPrefs1.getInt("usuario_id", -1)
        
        if (oldUserId1 != -1) {
            // Migrar datos al nuevo formato
            val editor = sharedPreferences.edit()
            editor.putInt("id_usuario", oldUserId1)
            editor.putBoolean("logged_in", true)
            editor.apply()
            
            // Limpiar datos antiguos
            oldPrefs1.edit().clear().apply()
            
            Toast.makeText(this, "Datos de usuario migrados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarToolbar() {
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun inicializarVistas() {

        val recyclerViewTops = findViewById<RecyclerView>(R.id.recycler_view_tops)
        val recyclerViewBottoms = findViewById<RecyclerView>(R.id.recycler_view_bottoms)
        val recyclerViewZapatos = findViewById<RecyclerView>(R.id.recycler_view_zapatos)
        val recyclerViewAccesorios = findViewById<RecyclerView>(R.id.recycler_view_accesorios)
        val fabAgregarPrenda = findViewById<FloatingActionButton>(R.id.fab_agregar_prenda)

        topsAdapter = PrendaAdapter(mutableListOf())
        bottomsAdapter = PrendaAdapter(mutableListOf())
        zapatosAdapter = PrendaAdapter(mutableListOf())
        accesoriosAdapter = PrendaAdapter(mutableListOf())

        recyclerViewTops.adapter = topsAdapter
        recyclerViewBottoms.adapter = bottomsAdapter
        recyclerViewZapatos.adapter = zapatosAdapter
        recyclerViewAccesorios.adapter = accesoriosAdapter

        fabAgregarPrenda.setOnClickListener {
            val intent = Intent(this, AgregarPrendaActivity::class.java)
            startActivity(intent)
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
                val tipoSeleccionado = if (which == 0) "Top" else "Bottom"
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

        val prendasSuperiores = todasLasPrendas.filter { it.tipo == "Top" }
        val prendasInferiores = todasLasPrendas.filter { it.tipo == "Bottom" }
        val zapatos = todasLasPrendas.filter { it.tipo == "Zapatos" }
        val accesorios = todasLasPrendas.filter { it.tipo == "Accesorios" }

        topsAdapter.actualizarPrendas(prendasSuperiores)
        bottomsAdapter.actualizarPrendas(prendasInferiores)
        zapatosAdapter.actualizarPrendas(zapatos)
        accesoriosAdapter.actualizarPrendas(accesorios)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                cerrarSesion()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
