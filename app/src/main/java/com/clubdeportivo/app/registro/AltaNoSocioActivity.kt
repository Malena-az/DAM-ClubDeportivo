package com.clubdeportivo.app.registro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.clubdeportivo.app.BaseActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.ClubDbHelper
import com.clubdeportivo.app.data.NoSocio
import com.clubdeportivo.app.util.dniRequerido
import com.clubdeportivo.app.util.requerido

class AltaNoSocioActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alta_no_socio)
        configurarPantalla("Alta de no socio")
        val db = ClubDbHelper.get(this)

        val etNombre = findViewById<EditText>(R.id.etNombreNoSocio)
        val etApellido = findViewById<EditText>(R.id.etApellidoNoSocio)
        val etDni = findViewById<EditText>(R.id.etDniNoSocio)
        val etTelefono = findViewById<EditText>(R.id.etTelefonoNoSocio)
        val etObservaciones = findViewById<EditText>(R.id.etObservaciones)

        // Devuelve true si el DNI está libre; si no, marca el error en el campo
        fun dniDisponible(dni: String): Boolean {
            if (db.existeDniSocio(dni)) {
                etDni.error = "Este DNI pertenece a un socio"
                etDni.requestFocus()
                return false
            }
            if (db.buscarNoSocioPorDni(dni) != null) {
                etDni.error = "Este DNI ya está registrado como no socio"
                etDni.requestFocus()
                return false
            }
            return true
        }

        findViewById<Button>(R.id.btnVerificarDniNoSocio).setOnClickListener {
            val dni = etDni.dniRequerido() ?: return@setOnClickListener
            if (dniDisponible(dni)) Toast.makeText(this, "DNI disponible", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnGuardarNoSocio).setOnClickListener {
            val nombre = etNombre.requerido("Ingresá el nombre") ?: return@setOnClickListener
            val apellido = etApellido.requerido("Ingresá el apellido") ?: return@setOnClickListener
            val dni = etDni.dniRequerido() ?: return@setOnClickListener
            val telefono = etTelefono.requerido("Ingresá el teléfono") ?: return@setOnClickListener
            if (!telefono.matches(Regex("^[0-9]+$"))) {
                etTelefono.error = "El teléfono solo debe contener números"
                etTelefono.requestFocus()
                return@setOnClickListener
            }
            if (!dniDisponible(dni)) return@setOnClickListener

            val observaciones = etObservaciones.text.toString().trim()
            val id = db.insertarNoSocio(NoSocio(0, nombre, apellido, dni, telefono, observaciones))
            if (id == -1L) {
                Toast.makeText(this, "No se pudo guardar el registro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resumen = """
                Nombre: $nombre
                Apellido: $apellido
                DNI: $dni
                Teléfono: $telefono
                Observaciones: ${if (observaciones.isEmpty()) "-" else observaciones}
            """.trimIndent()

            AlertDialog.Builder(this)
                .setTitle("✅ No socio registrado con éxito")
                .setMessage(resumen)
                .setPositiveButton("Aceptar") { _, _ -> finish() }
                .setCancelable(false)
                .show()
        }

        findViewById<Button>(R.id.btnLimpiarNoSocio).setOnClickListener {
            etNombre.text.clear(); etApellido.text.clear(); etDni.text.clear()
            etTelefono.text.clear(); etObservaciones.text.clear()
            etNombre.requestFocus()
        }
    }
}
