package com.clubdeportivo.app.registro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.ClubDbHelper
import com.clubdeportivo.app.data.Sesion

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = ClubDbHelper.get(this)
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val txtError = findViewById<TextView>(R.id.txtError)

        findViewById<Button>(R.id.btnIngresar).setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            txtError.visibility = android.view.View.GONE

            when {
                usuario.isEmpty() || contrasena.isEmpty() ->
                    Toast.makeText(this, "Completá usuario y contraseña", Toast.LENGTH_SHORT).show()
                // La combinación usuario + contraseña se valida contra la tabla usuarios
                !db.validarLogin(usuario, contrasena) -> {
                    txtError.visibility = android.view.View.VISIBLE
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Sesion.usuario = usuario
                    startActivity(Intent(this, MenuActivity::class.java))
                    finish()
                }
            }
        }
    }
}
