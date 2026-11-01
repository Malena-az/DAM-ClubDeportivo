package com.clubdeportivo.app.registro

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.clubdeportivo.app.BaseActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.cobros.CobrarCuotaActivity
import com.clubdeportivo.app.data.ClubDbHelper
import com.clubdeportivo.app.data.Fechas
import com.clubdeportivo.app.data.Socio
import com.clubdeportivo.app.util.dniRequerido
import com.clubdeportivo.app.util.requerido
import java.util.Calendar

/**
 * Alta de socio. Si recibe el extra "socioId" funciona en modo edición
 * (misma pantalla, datos precargados, el DNI no se puede cambiar).
 */
class AltaSocioActivity : BaseActivity() {

    private lateinit var db: ClubDbHelper
    private var socioEdicion: Socio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alta_socio)
        db = ClubDbHelper.get(this)

        val socioId = intent.getIntExtra("socioId", -1)
        socioEdicion = if (socioId != -1) db.obtenerSocio(socioId) else null
        val editando = socioEdicion != null

        configurarPantalla(if (editando) "Editar socio" else "Alta socio", if (editando) R.id.nav_socios else R.id.nav_inicio)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etDni = findViewById<EditText>(R.id.etDni)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etDireccion = findViewById<EditText>(R.id.etDireccion)
        val etFechaNac = findViewById<EditText>(R.id.etFechaNacimiento)
        val cbApto = findViewById<CheckBox>(R.id.cbAptoMedico)
        val btnVerificar = findViewById<Button>(R.id.btnVerificarDni)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarSocio)

        socioEdicion?.let {
            etNombre.setText(it.nombre); etApellido.setText(it.apellido); etDni.setText(it.dni)
            etTelefono.setText(it.telefono); etEmail.setText(it.email); etDireccion.setText(it.direccion)
            etFechaNac.setText(if (it.fechaNacimiento.isEmpty()) "" else Fechas.aVisible(it.fechaNacimiento))
            cbApto.isChecked = it.aptoFisico
            etDni.isEnabled = false                      // el DNI identifica al socio
            btnVerificar.isEnabled = false
            btnGuardar.text = "Guardar cambios"
        }

        etFechaNac.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, anio, mes, dia ->
                etFechaNac.setText(String.format("%02d/%02d/%04d", dia, mes + 1, anio))
                etFechaNac.error = null
            }, cal.get(Calendar.YEAR) - 25, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }

        btnVerificar.setOnClickListener {
            val dni = etDni.dniRequerido() ?: return@setOnClickListener
            if (db.existeDniSocio(dni)) {
                etDni.error = "Ya existe un socio con ese DNI"
                etDni.requestFocus()
            } else {
                Toast.makeText(this, "DNI disponible", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnCancelarSocio).setOnClickListener { finish() }

        btnGuardar.setOnClickListener {
            // ---- validaciones: no deja guardar un formulario vacío o incompleto
            val nombre = etNombre.requerido("Ingresá el nombre") ?: return@setOnClickListener
            val apellido = etApellido.requerido("Ingresá el apellido") ?: return@setOnClickListener
            val dni = etDni.dniRequerido() ?: return@setOnClickListener

            val telefono = etTelefono.requerido("Ingresá el teléfono") ?: return@setOnClickListener
            if (!telefono.matches(Regex("^[0-9]+$"))) {
                etTelefono.error = "El teléfono solo debe contener números"
                etTelefono.requestFocus()
                return@setOnClickListener
            }

            val email = etEmail.requerido("Ingresá el email") ?: return@setOnClickListener
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Ingresá un email válido"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            val direccion = etDireccion.requerido("Ingresá la dirección") ?: return@setOnClickListener
            val fechaNac = etFechaNac.requerido("Seleccioná la fecha de nacimiento") ?: return@setOnClickListener
            if (!cbApto.isChecked) {
                Toast.makeText(this, "Debe presentar el apto médico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val original = socioEdicion
            if (original != null) {
                val actualizado = original.copy(
                    nombre = nombre, apellido = apellido, telefono = telefono, email = email,
                    direccion = direccion, fechaNacimiento = Fechas.aIso(fechaNac), aptoFisico = true
                )
                if (db.actualizarSocio(actualizado)) {
                    Toast.makeText(this, "Socio actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "No se pudo actualizar el socio", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            val nuevo = Socio(
                id = 0, nombre = nombre, apellido = apellido, dni = dni, telefono = telefono,
                direccion = direccion, email = email, fechaNacimiento = Fechas.aIso(fechaNac), aptoFisico = true
            )
            val idNuevo = db.insertarSocio(nuevo)
            if (idNuevo == -1L) {
                etDni.error = "Ya existe un socio con ese DNI"
                etDni.requestFocus()
                return@setOnClickListener
            }

            // Socio ya guardado: se muestra el resumen y al aceptar se pasa al cobro de la primera cuota
            val resumen = """
                Nombre: $nombre
                Apellido: $apellido
                DNI: $dni
                Teléfono: $telefono
                Email: $email
                Dirección: $direccion
                Fecha de nacimiento: $fechaNac
                Apto médico: Sí
            """.trimIndent()

            AlertDialog.Builder(this)
                .setTitle("✅ Socio registrado con éxito")
                .setMessage(resumen)
                .setPositiveButton("Cobrar primera cuota") { _, _ ->
                    startActivity(Intent(this, CobrarCuotaActivity::class.java).apply {
                        putExtra("socioId", idNuevo.toInt())
                    })
                    finish()
                }
                .setCancelable(false)
                .show()
        }
    }
}
