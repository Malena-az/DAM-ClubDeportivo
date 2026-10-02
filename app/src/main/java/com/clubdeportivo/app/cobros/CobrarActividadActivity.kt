package com.clubdeportivo.app.cobros

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.Datos
import com.clubdeportivo.app.data.Pago
import java.util.Calendar

class CobrarActividadActivity : AppCompatActivity() {

    private var dniValidado: String? = null   // DNI buscado que NO es de un socio
    private var fechaElegida: String? = null  // fecha elegida en el calendario
    private var ultimoPago: Pago? = null      // último pago registrado en esta pantalla

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobrar_actividad)

        val etDni = findViewById<EditText>(R.id.etDni)
        val etImporte = findViewById<EditText>(R.id.etImporte)
        val tvAviso = findViewById<TextView>(R.id.tvAviso)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val spConcepto = findViewById<Spinner>(R.id.spConcepto)
        val spMedioPago = findViewById<Spinner>(R.id.spMedioPago)
        val spCuotas = findViewById<Spinner>(R.id.spCuotas)

        // Opciones de los spinners (la posición 0 es el "placeholder")
        val conceptos = listOf("Seleccionar...", "Natación", "Fútbol", "Tenis", "Pilates")
        val medios = listOf("Seleccionar...", "Efectivo", "Tarjeta de crédito", "Tarjeta de débito", "Transferencia")
        val cuotas = listOf("Seleccionar...", "1", "3", "6")
        spConcepto.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, conceptos)
        spMedioPago.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, medios)
        spCuotas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cuotas)

        findViewById<TextView>(R.id.btnVolver).setOnClickListener { finish() }

        // FECHA: abre un calendario
        tvFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, anio, mes, dia ->
                fechaElegida = String.format("%02d/%02d/%d", dia, mes + 1, anio)
                tvFecha.text = fechaElegida
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // BUSCAR: si el DNI es de un socio, se avisa y no se puede cobrar
        findViewById<Button>(R.id.btnBuscar).setOnClickListener {
            val dni = etDni.text.toString().trim()
            if (dni.length < 7) {
                etDni.error = "Ingresá un DNI de 7 u 8 dígitos"
                return@setOnClickListener
            }
            if (Datos.buscarPorDni(dni) != null) {
                dniValidado = null
                tvAviso.visibility = View.VISIBLE
            } else {
                dniValidado = dni
                tvAviso.visibility = View.GONE
                toast("DNI válido para cobrar actividad")
            }
        }

        // REGISTRAR pago (Create)
        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            val dni = dniValidado
            if (dni == null) {
                toast("Buscá un DNI que no pertenezca a un socio")
                return@setOnClickListener
            }
            if (spConcepto.selectedItemPosition == 0) {
                toast("Seleccioná un concepto")
                return@setOnClickListener
            }
            val importe = etImporte.text.toString().toDoubleOrNull()
            if (importe == null || importe <= 0) {
                etImporte.error = "Ingresá un importe válido"
                return@setOnClickListener
            }
            if (spMedioPago.selectedItemPosition == 0) {
                toast("Seleccioná un medio de pago")
                return@setOnClickListener
            }
            if (spCuotas.selectedItemPosition == 0) {
                toast("Seleccioná la cantidad de cuotas")
                return@setOnClickListener
            }
            val fecha = fechaElegida
            if (fecha == null) {
                toast("Seleccioná la fecha de pago")
                return@setOnClickListener
            }

            val pago = Pago(
                id = Datos.pagos.size + 1,
                dni = dni,
                concepto = spConcepto.selectedItem.toString(),
                importe = importe,
                medioPago = spMedioPago.selectedItem.toString(),
                fecha = fecha
            )
            Datos.pagos.add(pago)
            ultimoPago = pago
            toast("Pago registrado")
            abrirComprobante(pago)
        }

        // DESCARGAR comprobante (solo si ya se registró un pago)
        findViewById<Button>(R.id.btnDescargar).setOnClickListener {
            val pago = ultimoPago
            if (pago == null) toast("Primero registrá un pago") else abrirComprobante(pago)
        }
    }

    private fun abrirComprobante(pago: Pago) {
        val intent = Intent(this, ComprobanteActivity::class.java)
        intent.putExtra("nombre", "No socio")
        intent.putExtra("nroSocio", "-")
        intent.putExtra("dni", pago.dni)
        intent.putExtra("concepto", pago.concepto)
        intent.putExtra("importe", pago.importe)
        intent.putExtra("medioPago", pago.medioPago)
        intent.putExtra("fecha", pago.fecha)
        startActivity(intent)
    }

    private fun toast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}