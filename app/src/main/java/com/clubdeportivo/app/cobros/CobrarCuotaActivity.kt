package com.clubdeportivo.app.cobros

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.clubdeportivo.app.BaseActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.ClubDbHelper
import com.clubdeportivo.app.data.Estados
import com.clubdeportivo.app.data.Fechas
import com.clubdeportivo.app.data.Pago
import com.clubdeportivo.app.data.Precio
import com.clubdeportivo.app.data.Socio
import com.clubdeportivo.app.util.FormUtil
import com.clubdeportivo.app.util.dniRequerido

/**
 * Cobro de la cuota. Se llega desde dos lugares:
 *  1) El botón "Cobrar cuota" del menú (se busca al socio por DNI).
 *  2) Automáticamente después de dar de alta un socio: llega el extra "socioId"
 *     y la pantalla ya muestra al socio, sin pasar por el botón.
 * El importe sale de la tabla precios, no se escribe a mano.
 */
class CobrarCuotaActivity : BaseActivity() {

    private lateinit var db: ClubDbHelper
    private lateinit var precioMensual: Precio
    private lateinit var precioQuincenal: Precio
    private var socioActual: Socio? = null

    private lateinit var etDni: EditText
    private lateinit var cardSocio: View
    private lateinit var tvImporte: TextView
    private lateinit var rbMensual: RadioButton
    private lateinit var rbQuincenal: RadioButton
    private lateinit var spMedioPago: Spinner
    private lateinit var spCuotas: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobrar_cuota)
        configurarPantalla("Cobrar cuota")
        db = ClubDbHelper.get(this)

        val mensual = db.obtenerPrecio(ClubDbHelper.CUOTA_MENSUAL)
        val quincenal = db.obtenerPrecio(ClubDbHelper.CUOTA_QUINCENAL)
        if (mensual == null || quincenal == null) {
            Toast.makeText(this, "Falta cargar la lista de precios", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        precioMensual = mensual
        precioQuincenal = quincenal

        etDni = findViewById(R.id.etDni)
        cardSocio = findViewById(R.id.cardSocio)
        tvImporte = findViewById(R.id.tvImporte)
        rbMensual = findViewById(R.id.rbMensual)
        rbQuincenal = findViewById(R.id.rbQuincenal)
        spMedioPago = findViewById(R.id.spMedioPago)
        spCuotas = findViewById(R.id.spCuotas)

        FormUtil.configurarMedioYCuotas(this, spMedioPago, spCuotas)

        rbMensual.text = "Mensual (${precioMensual.dias} días) · ${FormUtil.moneda(precioMensual.importe)}"
        rbQuincenal.text = "Quincenal (${precioQuincenal.dias} días) · ${FormUtil.moneda(precioQuincenal.importe)}"
        findViewById<RadioGroup>(R.id.rgPeriodo).setOnCheckedChangeListener { _, _ -> mostrarImporte() }
        mostrarImporte()

        findViewById<Button>(R.id.btnBuscar).setOnClickListener {
            val dni = etDni.dniRequerido() ?: return@setOnClickListener
            val socio = db.buscarSocioPorDni(dni)
            if (socio == null) {
                socioActual = null
                cardSocio.visibility = View.GONE
                Toast.makeText(this, "No se encontró un socio con ese DNI", Toast.LENGTH_SHORT).show()
            } else {
                mostrarSocio(socio)
            }
        }

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener { registrarPago() }


        // Llegada automática desde el alta de socio
        val socioId = intent.getIntExtra("socioId", -1)
        if (socioId != -1) db.obtenerSocio(socioId)?.let { mostrarSocio(it) }
    }

    private fun precioElegido(): Precio = if (rbQuincenal.isChecked) precioQuincenal else precioMensual

    private fun mostrarImporte() {
        tvImporte.text = FormUtil.moneda(precioElegido().importe)
    }

    private fun mostrarSocio(socio: Socio) {
        socioActual = socio
        etDni.setText(socio.dni)
        findViewById<TextView>(R.id.tvNombre).text = socio.nombreCompleto
        findViewById<TextView>(R.id.tvNroSocio).text = "N° Socio: ${socio.nroSocio}"
        findViewById<TextView>(R.id.tvDniSocio).text = "DNI: ${socio.dni}"
        findViewById<TextView>(R.id.tvVence).text =
            if (socio.fechaVencimiento.isEmpty()) "Primera cuota pendiente"
            else "Vence: ${Fechas.aVisible(socio.fechaVencimiento)}"
        val tvEstado = findViewById<TextView>(R.id.tvEstado)
        tvEstado.text = socio.estado
        tvEstado.backgroundTintList = ColorStateList.valueOf(Estados.color(socio.estado))
        cardSocio.visibility = View.VISIBLE
    }

    private fun registrarPago() {
        val socio = socioActual
        if (socio == null) {
            Toast.makeText(this, "Primero buscá un socio por DNI", Toast.LENGTH_SHORT).show()
            return
        }
        if (spMedioPago.selectedItemPosition == 0) {
            Toast.makeText(this, "Seleccioná un medio de pago", Toast.LENGTH_SHORT).show()
            return
        }
        if (spCuotas.isEnabled && spCuotas.selectedItemPosition == 0) {
            Toast.makeText(this, "Seleccioná la cantidad de cuotas", Toast.LENGTH_SHORT).show()
            return
        }

        val pago = db.registrarPagoCuota(
            socio, precioElegido(), spMedioPago.selectedItem.toString(), FormUtil.cuotasElegidas(spCuotas)
        )
        if (pago == null) {
            Toast.makeText(this, "No se pudo registrar el pago", Toast.LENGTH_SHORT).show()
            return
        }
        // Se vuelve a leer el socio: su vencimiento ya se actualizó en la base
        val actualizado = db.obtenerSocio(socio.id) ?: socio
        mostrarSocio(actualizado)
        Toast.makeText(this, "Pago registrado", Toast.LENGTH_SHORT).show()
        abrirComprobante(pago, actualizado)
    }

    private fun abrirComprobante(pago: Pago, socio: Socio) {
        val intent = Intent(this, ComprobanteActivity::class.java)
        intent.putExtra("nroComprobante", pago.id)
        intent.putExtra("nombre", socio.nombreCompleto)
        intent.putExtra("nroSocio", socio.nroSocio)
        intent.putExtra("dni", pago.dni)
        intent.putExtra("concepto", pago.concepto)
        intent.putExtra("importe", pago.importe)
        intent.putExtra("medioPago", pago.medioPago)
        intent.putExtra("cuotas", pago.cuotas)
        intent.putExtra("fecha", Fechas.aVisible(pago.fechaPago))
        intent.putExtra("vencimiento", Fechas.aVisible(pago.nuevoVencimiento))
        startActivity(intent)
    }
}
