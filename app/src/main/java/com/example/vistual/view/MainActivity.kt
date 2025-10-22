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

    // RecyclerViews as class properties
    private lateinit var recyclerViewTops: RecyclerView
    private lateinit var recyclerViewBottoms: RecyclerView
    private lateinit var recyclerViewZapatos: RecyclerView
    private lateinit var recyclerViewAccesorios: RecyclerView

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)
        sharedPreferences = getSharedPreferences("ClosetVirtual", MODE_PRIVATE)

        if (!sharedPreferences.getBoolean("logged_in", false)) {
            irALogin()
            return
        }

        usuarioId = sharedPreferences.getInt("id_usuario", -1)
        if (usuarioId == -1) {
            migrarDatosAntiguos()
            usuarioId = sharedPreferences.getInt("id_usuario", -1)
        }

        configurarToolbar()
        inicializarVistas()
        cargarPrendas()
    }

    private fun migrarDatosAntiguos() {
        val oldPrefs1 = getSharedPreferences("usuario_data", MODE_PRIVATE)
        val oldUserId1 = oldPrefs1.getInt("usuario_id", -1)
        if (oldUserId1 != -1) {
            val editor = sharedPreferences.edit()
            editor.putInt("id_usuario", oldUserId1)
            editor.putBoolean("logged_in", true)
            editor.apply()
            oldPrefs1.edit().clear().apply()
            Toast.makeText(this, "Datos de usuario migrados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarToolbar() {
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun inicializarVistas() {
        recyclerViewTops = findViewById(R.id.recycler_view_tops)
        recyclerViewBottoms = findViewById(R.id.recycler_view_bottoms)
        recyclerViewZapatos = findViewById(R.id.recycler_view_zapatos)
        recyclerViewAccesorios = findViewById(R.id.recycler_view_accesorios)
        val fabAgregarPrenda = findViewById<FloatingActionButton>(R.id.fab_agregar_prenda)

        // Use the correct layout for each adapter
        topsAdapter = PrendaAdapter(this, mutableListOf(), dbHelper, R.layout.item_prenda_carrusel)
        bottomsAdapter = PrendaAdapter(this, mutableListOf(), dbHelper, R.layout.item_prenda_inferior_carrusel)
        zapatosAdapter = PrendaAdapter(this, mutableListOf(), dbHelper, R.layout.item_prenda_carrusel)
        accesoriosAdapter = PrendaAdapter(this, mutableListOf(), dbHelper, R.layout.item_prenda_carrusel)

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
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(imagenUri, flag)
                guardarPrendaEnDB(imagenUri, tipoSeleccionado)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarPrendaEnDB(imagenUri: Uri, tipo: String) {
        val nombrePredeterminado = "Prenda"
        val colorPredeterminado = "Sin color"
        dbHelper.agregarPrenda(
            nombre = nombrePredeterminado,
            categoria = tipo,
            color = colorPredeterminado,
            rutaImagen = imagenUri.toString(),
            idUsuario = usuarioId
        )
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

        // Scroll to a middle position for infinite effect
        if (prendasSuperiores.isNotEmpty()) {
            val startPosition = Integer.MAX_VALUE / 2
            recyclerViewTops.scrollToPosition(startPosition - (startPosition % prendasSuperiores.size))
        }
        if (prendasInferiores.isNotEmpty()) {
            val startPosition = Integer.MAX_VALUE / 2
            recyclerViewBottoms.scrollToPosition(startPosition - (startPosition % prendasInferiores.size))
        }
        if (zapatos.isNotEmpty()) {
            val startPosition = Integer.MAX_VALUE / 2
            recyclerViewZapatos.scrollToPosition(startPosition - (startPosition % zapatos.size))
        }
        if (accesorios.isNotEmpty()) {
            val startPosition = Integer.MAX_VALUE / 2
            recyclerViewAccesorios.scrollToPosition(startPosition - (startPosition % accesorios.size))
        }
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
