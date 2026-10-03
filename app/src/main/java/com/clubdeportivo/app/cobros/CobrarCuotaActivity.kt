package com.clubdeportivo.app.cobros

import android.content.Intent
import android.graphics.Color
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
import com.clubdeportivo.app.data.Socio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.res.ColorStateList
import android.widget.AdapterView

class CobrarCuotaActivity : AppCompatActivity() {

    private var socioActual: Socio? = null   // socio encontrado en la búsqueda
    private var ultimoPago: Pago? = null     // último pago registrado en esta pantalla

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobrar_cuota)

        val etDni = findViewById<EditText>(R.id.etDni)
        val etImporte = findViewById<EditText>(R.id.etImporte)
        val cardSocio = findViewById<View>(R.id.cardSocio)
        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvEstado = findViewById<TextView>(R.id.tvEstado)
        val tvNroSocio = findViewById<TextView>(R.id.tvNroSocio)
        val tvDniSocio = findViewById<TextView>(R.id.tvDniSocio)
        val tvVence = findViewById<TextView>(R.id.tvVence)
        val spMedioPago = findViewById<Spinner>(R.id.spMedioPago)
        val spCuotas = findViewById<Spinner>(R.id.spCuotas)

        // Opciones de los spinners (la posición 0 es el "placeholder")
        val medios = listOf(
            "Seleccionar...",
            "Efectivo",
            "Tarjeta de crédito",
            "Tarjeta de débito",
            "Transferencia"
        )
        val cuotas = listOf("Seleccionar...", "1", "3", "6")
        spMedioPago.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, medios)
        spCuotas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cuotas)

        // Las cuotas solo aplican a tarjeta de crédito
        fun actualizarCuotas() {
            val esCredito = spMedioPago.selectedItem.toString() == "Tarjeta de crédito"
            spCuotas.isEnabled = esCredito
            spCuotas.alpha = if (esCredito) 1f else 0.5f
            if (!esCredito) spCuotas.setSelection(1)   // posición 1 = "1" cuota
        }
        spMedioPago.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                actualizarCuotas()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        findViewById<TextView>(R.id.btnVolver).setOnClickListener { finish() }

        // BUSCAR socio por DNI
        findViewById<Button>(R.id.btnBuscar).setOnClickListener {
            val dni = etDni.text.toString().trim()
            if (dni.length < 7) {
                etDni.error = "Ingresá un DNI de 7 u 8 dígitos"
                return@setOnClickListener
            }
            val socio = Datos.buscarPorDni(dni)
            if (socio == null) {
                socioActual = null
                cardSocio.visibility = View.GONE
                toast("No se encontró un socio con ese DNI")
                return@setOnClickListener
            }
            socioActual = socio
            tvNombre.text = "${socio.nombre} ${socio.apellido}"
            tvEstado.text = socio.estado
            val colorChip =
                if (socio.estado == "Activo") R.color.estado_activo else R.color.estado_vencido
            tvEstado.backgroundTintList = ColorStateList.valueOf(getColor(colorChip))
            tvNroSocio.text = "N° Socio: ${socio.id}"
            tvDniSocio.text = "DNI: ${socio.dni}"
            tvVence.text = "Vence: ${socio.fechaVencimiento}"
            etImporte.setText("15000")
            cardSocio.visibility = View.VISIBLE
        }

        // REGISTRAR pago (Create)
        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            val socio = socioActual
            if (socio == null) {
                toast("Primero buscá un socio por DNI")
                return@setOnClickListener
            }
            val importe = etImporte.text.toString().toDoubleOrNull()
            if (importe == null || importe <= 0) {
                etImporte.error = "Ingresá un importe válido (ej: 15000)"
                return@setOnClickListener
            }
            if (spMedioPago.selectedItemPosition == 0) {
                toast("Seleccioná un medio de pago")
                return@setOnClickListener
            }
            if (spCuotas.isEnabled && spCuotas.selectedItemPosition == 0) {
                toast("Seleccioná la cantidad de cuotas")
                return@setOnClickListener
            }

            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "AR")).format(Date())
            val pago = Pago(
                id = Datos.pagos.size + 1,
                dni = socio.dni,
                concepto = "Cuota mensual",
                importe = importe,
                medioPago = spMedioPago.selectedItem.toString(),
                fecha = fecha
            )
            Datos.pagos.add(pago)
            ultimoPago = pago
            toast("Pago registrado")
            abrirComprobante(pago, socio)
        }

        // DESCARGAR comprobante (solo si ya se registró un pago)
        findViewById<Button>(R.id.btnDescargar).setOnClickListener {
            val pago = ultimoPago
            val socio = socioActual
            if (pago == null || socio == null) {
                toast("Primero registrá un pago")
            } else {
                abrirComprobante(pago, socio)
            }
        }
    }

    private fun abrirComprobante(pago: Pago, socio: Socio) {
        val intent = Intent(this, ComprobanteActivity::class.java)
        intent.putExtra("nombre", "${socio.nombre} ${socio.apellido}")
        intent.putExtra("nroSocio", socio.id.toString())
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