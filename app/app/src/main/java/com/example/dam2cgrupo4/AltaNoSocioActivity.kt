package com.example.dam2cgrupo4

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class AltaNoSocioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alta_no_socio)

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarNoSocio)
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiarNoSocio)

        val etNombre = findViewById<EditText>(R.id.etNombreNoSocio)
        val etApellido = findViewById<EditText>(R.id.etApellidoNoSocio)
        val etDni = findViewById<EditText>(R.id.etDniNoSocio)
        val etTelefono = findViewById<EditText>(R.id.etTelefonoNoSocio)
        val etObservaciones = findViewById<EditText>(R.id.etObservaciones)

        btnVolver.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            if (validarFormulario(etNombre, etApellido, etDni, etTelefono)) {
                mostrarResumen(etNombre, etApellido, etDni, etTelefono, etObservaciones)
            }
        }

        btnLimpiar.setOnClickListener {
            etNombre.text.clear()
            etApellido.text.clear()
            etDni.text.clear()
            etTelefono.text.clear()
            etObservaciones.text.clear()
        }
    }

    private fun validarFormulario(
        etNombre: EditText,
        etApellido: EditText,
        etDni: EditText,
        etTelefono: EditText
    ): Boolean {

        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        if (nombre.isEmpty()) {
            etNombre.error = "Ingresa el nombre"
            etNombre.requestFocus()
            return false
        }

        if (apellido.isEmpty()) {
            etApellido.error = "Ingresa el apellido"
            etApellido.requestFocus()
            return false
        }

        if (dni.isEmpty()) {
            etDni.error = "Ingresa el DNI"
            etDni.requestFocus()
            return false
        }
        if (!dni.matches(Regex("^[0-9]{7,8}$"))) {
            etDni.error = "El DNI debe tener entre 7 y 8 dígitos"
            etDni.requestFocus()
            return false
        }

        if (telefono.isEmpty()) {
            etTelefono.error = "Ingresa el teléfono"
            etTelefono.requestFocus()
            return false
        }
        if (!telefono.matches(Regex("^[0-9]+$"))) {
            etTelefono.error = "El teléfono solo debe contener números"
            etTelefono.requestFocus()
            return false
        }

        return true
    }

    private fun mostrarResumen(
        etNombre: EditText,
        etApellido: EditText,
        etDni: EditText,
        etTelefono: EditText,
        etObservaciones: EditText
    ) {
        val observaciones = etObservaciones.text.toString().trim()

        val resumen = """
        Nombre: ${etNombre.text}
        Apellido: ${etApellido.text}
        DNI: ${etDni.text}
        Teléfono: ${etTelefono.text}
        Observaciones: ${if (observaciones.isEmpty()) "-" else observaciones}
    """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("✅ No socio registrado con éxito")
            .setMessage(resumen)
            .setPositiveButton("Aceptar") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }
}