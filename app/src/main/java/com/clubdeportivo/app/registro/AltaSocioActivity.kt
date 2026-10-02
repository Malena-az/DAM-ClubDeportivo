package com.clubdeportivo.app.registro

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AltaSocioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alta_socio)

        val btnMenu = findViewById<Button>(R.id.btnMenu)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarSocio)
        val btnCancelar = findViewById<Button>(R.id.btnCancelarSocio)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etDni = findViewById<EditText>(R.id.etDni)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etDireccion = findViewById<EditText>(R.id.etDireccion)
        val etFechaNacimiento = findViewById<EditText>(R.id.etFechaNacimiento)
        val cbAptoMedico = findViewById<CheckBox>(R.id.cbAptoMedico)
        val etImporte = findViewById<EditText>(R.id.etImporte)
        val spMedioPago = findViewById<Spinner>(R.id.spMedioPago)
        val spCuotas = findViewById<Spinner>(R.id.spCuotas)

        // Cargar opciones de los Spinners
        val adapterMedioPago = ArrayAdapter.createFromResource(
            this, R.array.medios_pago, R.layout.simple_spinner_item
        )
        adapterMedioPago.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spMedioPago.adapter = adapterMedioPago

        val adapterCuotas = ArrayAdapter.createFromResource(
            this, R.array.cuotas, R.layout.simple_spinner_item
        )
        adapterCuotas.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spCuotas.adapter = adapterCuotas

        // Selector de fecha de nacimiento
        etFechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fecha = String.format(
                        "%02d/%02d/%04d",
                        diaSeleccionado, mesSeleccionado + 1, anioSeleccionado
                    )
                    etFechaNacimiento.setText(fecha)
                },
                anio, mes, dia
            )
            datePicker.show()
        }

        btnMenu.setOnClickListener {
            finish()
        }

        btnCancelar.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            if (validarFormulario(
                    etNombre, etApellido, etDni, etTelefono,
                    etEmail, etDireccion, etFechaNacimiento,
                    cbAptoMedico, etImporte, spMedioPago, spCuotas
                )
            ) {
                mostrarResumen(
                    etNombre, etApellido, etDni, etTelefono,
                    etEmail, etDireccion, etFechaNacimiento,
                    cbAptoMedico, etImporte, spMedioPago, spCuotas
                )
            }
        }
    }

    private fun mostrarResumen(
        etNombre: EditText,
        etApellido: EditText,
        etDni: EditText,
        etTelefono: EditText,
        etEmail: EditText,
        etDireccion: EditText,
        etFechaNacimiento: EditText,
        cbAptoMedico: CheckBox,
        etImporte: EditText,
        spMedioPago: Spinner,
        spCuotas: Spinner
    ) {
        val resumen = """
        Nombre: ${etNombre.text}
        Apellido: ${etApellido.text}
        DNI: ${etDni.text}
        Teléfono: ${etTelefono.text}
        Email: ${etEmail.text}
        Dirección: ${etDireccion.text}
        Fecha de nacimiento: ${etFechaNacimiento.text}
        Apto médico: ${if (cbAptoMedico.isChecked) "Sí" else "No"}
        Importe: $ ${etImporte.text}
        Medio de pago: ${spMedioPago.selectedItem}
        Cuotas: ${spCuotas.selectedItem}
    """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("✅ Socio registrado con éxito")
            .setMessage(resumen)
            .setPositiveButton("Aceptar") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun validarFormulario(
        etNombre: EditText,
        etApellido: EditText,
        etDni: EditText,
        etTelefono: EditText,
        etEmail: EditText,
        etDireccion: EditText,
        etFechaNacimiento: EditText,
        cbAptoMedico: CheckBox,
        etImporte: EditText,
        spMedioPago: Spinner,
        spCuotas: Spinner
    ): Boolean {

        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val fechaNacimiento = etFechaNacimiento.text.toString().trim()
        val importeTexto = etImporte.text.toString().trim()

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

        if (email.isEmpty()) {
            etEmail.error = "Ingresa el email"
            etEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Ingresa un email válido"
            etEmail.requestFocus()
            return false
        }

        if (direccion.isEmpty()) {
            etDireccion.error = "Ingresa la dirección"
            etDireccion.requestFocus()
            return false
        }

        if (fechaNacimiento.isEmpty()) {
            etFechaNacimiento.error = "Selecciona la fecha de nacimiento"
            etFechaNacimiento.requestFocus()
            return false
        }

        if (!cbAptoMedico.isChecked) {
            Toast.makeText(this, "Debe marcar el apto médico", Toast.LENGTH_SHORT).show()
            return false
        }

        if (importeTexto.isEmpty()) {
            etImporte.error = "Ingresa el importe"
            etImporte.requestFocus()
            return false
        }
        val importe = importeTexto.toDoubleOrNull()
        if (importe == null || importe <= 0) {
            etImporte.error = "El importe debe ser mayor a 0"
            etImporte.requestFocus()
            return false
        }

        if (spMedioPago.selectedItemPosition == 0) {
            Toast.makeText(this, "Selecciona un medio de pago", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spCuotas.selectedItemPosition == 0) {
            Toast.makeText(this, "Selecciona la cantidad de cuotas", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}