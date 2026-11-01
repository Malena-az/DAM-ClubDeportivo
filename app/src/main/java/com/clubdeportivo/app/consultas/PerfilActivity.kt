package com.clubdeportivo.app.consultas

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.clubdeportivo.app.BaseActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.ClubDbHelper
import com.clubdeportivo.app.data.Fechas
import com.clubdeportivo.app.data.Sesion
import com.clubdeportivo.app.registro.LoginActivity

/** Perfil del usuario que inició sesión (datos de la tabla usuarios). */
class PerfilActivity : BaseActivity() {

    private lateinit var db: ClubDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        configurarPantalla("Perfil", R.id.nav_perfil)
        db = ClubDbHelper.get(this)

        val u = db.obtenerUsuario(Sesion.usuario)
        if (u != null) {
            findViewById<TextView>(R.id.tvPerfilInicial).text = u.nombre.take(1).uppercase()
            findViewById<TextView>(R.id.tvPerfilNombre).text = "${u.nombre} ${u.apellido}"
            findViewById<TextView>(R.id.tvPerfilRol).text = "Administrador"
            findViewById<TextView>(R.id.tvPerfilNro).text = "Usuario: ${u.usuario}"
            findViewById<TextView>(R.id.tvPerfilDni).text = u.dni
            findViewById<TextView>(R.id.tvPerfilNom).text = u.nombre
            findViewById<TextView>(R.id.tvPerfilApellido).text = u.apellido
            findViewById<TextView>(R.id.tvPerfilAlta).text = Fechas.aVisible(u.fechaAlta)
            findViewById<TextView>(R.id.tvPerfilTel).text = u.telefono
            findViewById<TextView>(R.id.tvPerfilDir).text = u.direccion
            findViewById<TextView>(R.id.tvPerfilCorreo).text = u.email
        }

        findViewById<View>(R.id.accionPassword).setOnClickListener { mostrarCambioPassword() }
        findViewById<View>(R.id.accionCerrar).setOnClickListener {
            Sesion.usuario = ""
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    private fun campoPassword(pista: String) = EditText(this).apply {
        hint = pista
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    private fun mostrarCambioPassword() {
        val etActual = campoPassword("Contraseña actual")
        val etNueva = campoPassword("Contraseña nueva (mínimo 4 caracteres)")
        val etRepetir = campoPassword("Repetir contraseña nueva")
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(56, 24, 56, 0)
            addView(etActual); addView(etNueva); addView(etRepetir)
        }

        AlertDialog.Builder(this)
            .setTitle("Cambiar contraseña")
            .setView(contenedor)
            .setPositiveButton("Guardar") { _, _ ->
                val actual = etActual.text.toString()
                val nueva = etNueva.text.toString()
                val repetir = etRepetir.text.toString()
                val mensaje = when {
                    actual.isEmpty() || nueva.isEmpty() || repetir.isEmpty() -> "Completá los tres campos"
                    nueva.length < 4 -> "La contraseña nueva debe tener al menos 4 caracteres"
                    nueva != repetir -> "Las contraseñas nuevas no coinciden"
                    !db.cambiarPassword(Sesion.usuario, actual, nueva) -> "La contraseña actual es incorrecta"
                    else -> "Contraseña actualizada"
                }
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
