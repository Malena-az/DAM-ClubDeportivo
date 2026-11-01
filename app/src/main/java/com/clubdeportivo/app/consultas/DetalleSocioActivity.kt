package com.clubdeportivo.app.consultas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.clubdeportivo.app.BaseActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.ClubDbHelper
import com.clubdeportivo.app.data.Estados
import com.clubdeportivo.app.data.Fechas
import com.clubdeportivo.app.data.Socio
import com.clubdeportivo.app.registro.AltaSocioActivity
import com.clubdeportivo.app.util.FormUtil

class DetalleSocioActivity : BaseActivity() {

    private lateinit var db: ClubDbHelper
    private var socioId = -1
    private var socio: Socio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_socio)
        configurarPantalla("Detalle de socio", R.id.nav_socios)
        db = ClubDbHelper.get(this)
        socioId = intent.getIntExtra("socioId", -1)

        findViewById<Button>(R.id.btnEditar).setOnClickListener {
            startActivity(Intent(this, AltaSocioActivity::class.java).putExtra("socioId", socioId))
        }
        findViewById<Button>(R.id.btnDarDeBaja).setOnClickListener { confirmarBaja() }
        findViewById<Button>(R.id.btnHistorial).setOnClickListener { mostrarHistorial() }
        // Abre el carnet del socio ya buscado (le pasa el DNI por Intent)
        findViewById<Button>(R.id.btnVerCarnet).setOnClickListener {
            val s = socio ?: return@setOnClickListener
            startActivity(Intent(this, CarnetActivity::class.java).putExtra("dni", s.dni))
        }
    }

    // Se recarga al volver de "Editar socio" para mostrar los datos nuevos
    override fun onResume() {
        super.onResume()
        val s = db.obtenerSocio(socioId)
        if (s == null || !s.activo) {
            finish()
            return
        }
        socio = s
        val estado = s.estado
        findViewById<TextView>(R.id.txtNombreCompleto).text = s.nombreCompleto
        findViewById<TextView>(R.id.valDni).text = s.dni
        findViewById<TextView>(R.id.valNombre).text = s.nombre
        findViewById<TextView>(R.id.valApellido).text = s.apellido
        findViewById<TextView>(R.id.valVencimiento).text = Fechas.aVisible(s.fechaVencimiento)
        findViewById<TextView>(R.id.valApto).text = if (s.aptoFisico) "Sí" else "No"
        findViewById<TextView>(R.id.valTelefono).text = s.telefono
        findViewById<TextView>(R.id.valDireccion).text = s.direccion
        findViewById<TextView>(R.id.valCorreo).text = s.email

        val valEstado = findViewById<TextView>(R.id.valEstado)
        valEstado.text = estado
        valEstado.setTextColor(Estados.color(estado))

        val badge = findViewById<TextView>(R.id.txtBadge)
        badge.text = estado
        badge.setBackgroundColor(Estados.color(estado))
    }

    private fun confirmarBaja() {
        val s = socio ?: return
        AlertDialog.Builder(this)
            .setTitle("Dar de baja")
            .setMessage(
                "¿Dar de baja a ${s.nombreCompleto}?\n\n" +
                        "Dejará de aparecer en listados, vencimientos, carnet y cobros. " +
                        "Su historial de pagos se conserva."
            )
            .setPositiveButton("Dar de baja") { _, _ ->
                if (db.darDeBaja(s.id)) {
                    Toast.makeText(this, "Socio dado de baja", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "No se pudo dar de baja", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarHistorial() {
        val s = socio ?: return
        val pagos = db.listarPagosSocio(s.id)
        val mensaje = if (pagos.isEmpty()) {
            "Este socio todavía no registra pagos."
        } else {
            pagos.joinToString("\n\n") {
                "${Fechas.aVisible(it.fechaPago)} · ${it.concepto}\n" +
                        "${FormUtil.moneda(it.importe)} · ${it.medioPago}" +
                        (if (it.cuotas > 1) " (${it.cuotas} cuotas)" else "")
            }
        }
        AlertDialog.Builder(this)
            .setTitle("Historial de ${s.nombreCompleto}")
            .setMessage(mensaje)
            .setPositiveButton("Cerrar", null)
            .show()
    }
}
