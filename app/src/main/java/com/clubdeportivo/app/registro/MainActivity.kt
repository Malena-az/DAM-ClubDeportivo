package com.clubdeportivo.app.registro

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val usuarioCorrecto = "admin"
    private val passwordCorrecto = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)

        btnIngresar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            when {
                usuario.isEmpty() || contrasena.isEmpty() -> {
                    Toast.makeText(this, "Completa usuario y contraseña", Toast.LENGTH_SHORT).show()
                }
                usuario != usuarioCorrecto || contrasena != passwordCorrecto -> {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}