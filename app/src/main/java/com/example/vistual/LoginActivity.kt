package com.example.vistual

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vistual.models.DBHelper

class LoginActivity : AppCompatActivity() {
    
    private lateinit var dbHelper: DBHelper
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        dbHelper = DBHelper(this)
        sharedPreferences = getSharedPreferences("ClosetVirtual", MODE_PRIVATE)
        
        // Verificar si el usuario ya está logueado
        if (sharedPreferences.getBoolean("logged_in", false)) {
            irAMainActivity()
            return
        }
        
        val etCorreo = findViewById<EditText>(R.id.et_correo)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvCambiarARegistro = findViewById<TextView>(R.id.tv_cambiar_registro)
        
        btnLogin.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (dbHelper.validarLogin(correo, password)) {
                // Guardar sesión
                val editor = sharedPreferences.edit()
                editor.putBoolean("logged_in", true)
                editor.putString("correo_usuario", correo)
                editor.putInt("id_usuario", dbHelper.obtenerIdUsuario(correo))
                editor.apply()
                
                Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                irAMainActivity()
            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
        
        tvCambiarARegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun irAMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}