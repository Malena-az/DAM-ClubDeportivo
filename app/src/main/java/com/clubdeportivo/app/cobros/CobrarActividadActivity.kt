package com.clubdeportivo.app.cobros

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.clubdeportivo.app.BaseActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.ClubDbHelper
import com.clubdeportivo.app.data.Fechas
import com.clubdeportivo.app.data.Pago
import com.clubdeportivo.app.data.Precio
import com.clubdeportivo.app.util.FormUtil
import com.clubdeportivo.app.util.dniRequerido
import java.util.Calendar

/** Cobro de actividades a personas que no son socias. El importe sale de la tabla precios. */
class CobrarActividadActivity : BaseActivity() {

    private lateinit var db: ClubDbHelper
    private var dniValidado: String? = null
    private var nombreNoSocio: String = "No socio"
    private var fechaIso: String = Fechas.hoy()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobrar_actividad)
        configurarPantalla("Cobrar actividad")
        db = ClubDbHelper.get(this)

        val etDni = findViewById<EditText>(R.id.etDni)
        val tvAviso = findViewById<TextView>(R.id.tvAviso)
        val tvNombre = findViewById<TextView>(R.id.tvNombreNoSocio)
        val tvImporte = findViewById<TextView>(R.id.tvImporte)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val spConcepto = findViewById<Spinner>(R.id.spConcepto)
        val spMedioPago = findViewById<Spinner>(R.id.spMedioPago)
        val spCuotas = findViewById<Spinner>(R.id.spCuotas)

        // Actividades y precios salen de la base
        val actividades: List<Precio> = db.listarPrecios("ACTIVIDAD")
        val opciones = listOf("Seleccionar...") + actividades.map { it.concepto }
        spConcepto.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
        spConcepto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tvImporte.text = if (position > 0) FormUtil.moneda(actividades[position - 1].importe)
                else FormUtil.moneda(0.0)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        FormUtil.configurarMedioYCuotas(this, spMedioPago, spCuotas)

        // Fecha de pago: arranca en hoy y se puede cambiar con el calendario
        tvFecha.text = Fechas.aVisible(fechaIso)
        tvFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, anio, mes, dia ->
                fechaIso = String.format("%04d-%02d-%02d", anio, mes + 1, dia)
                tvFecha.text = Fechas.aVisible(fechaIso)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }

        findViewById<Button>(R.id.btnBuscar).setOnClickListener {
            val dni = etDni.dniRequerido() ?: return@setOnClickListener
            tvNombre.visibility = View.GONE
            if (db.existeDniSocio(dni)) {
                // Es socio: no se cobra acá
                dniValidado = null
                tvAviso.visibility = View.VISIBLE
            } else {
                tvAviso.visibility = View.GONE
                dniValidado = dni
                val registrado = db.buscarNoSocioPorDni(dni)
                nombreNoSocio = registrado?.let { "${it.nombre} ${it.apellido}" } ?: "No socio"
                tvNombre.text = if (registrado != null) "No socio registrado: $nombreNoSocio"
                else "DNI sin registrar: se cobrará como no socio"
                tvNombre.visibility = View.VISIBLE
            }
        }

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            val dni = dniValidado
            if (dni == null) {
                Toast.makeText(this, "Buscá un DNI que no pertenezca a un socio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (spConcepto.selectedItemPosition == 0) {
                Toast.makeText(this, "Seleccioná un concepto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (spMedioPago.selectedItemPosition == 0) {
                Toast.makeText(this, "Seleccioná un medio de pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (spCuotas.isEnabled && spCuotas.selectedItemPosition == 0) {
                Toast.makeText(this, "Seleccioná la cantidad de cuotas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pago = db.registrarPagoActividad(
                dni, actividades[spConcepto.selectedItemPosition - 1],
                spMedioPago.selectedItem.toString(), FormUtil.cuotasElegidas(spCuotas), fechaIso
            )
            if (pago == null) {
                Toast.makeText(this, "No se pudo registrar el pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "Pago registrado", Toast.LENGTH_SHORT).show()
            abrirComprobante(pago)
        }

    }

    private fun abrirComprobante(pago: Pago) {
        val intent = Intent(this, ComprobanteActivity::class.java)
        intent.putExtra("nroComprobante", pago.id)
        intent.putExtra("nombre", nombreNoSocio)
        intent.putExtra("nroSocio", "-")
        intent.putExtra("dni", pago.dni)
        intent.putExtra("concepto", pago.concepto)
        intent.putExtra("importe", pago.importe)
        intent.putExtra("medioPago", pago.medioPago)
        intent.putExtra("cuotas", pago.cuotas)
        intent.putExtra("fecha", Fechas.aVisible(pago.fechaPago))
        intent.putExtra("vencimiento", "")
        startActivity(intent)
    }
}
