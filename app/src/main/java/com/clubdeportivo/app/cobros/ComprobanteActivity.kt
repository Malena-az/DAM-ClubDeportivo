package com.clubdeportivo.app.cobros

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.clubdeportivo.app.BaseActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.registro.MenuActivity
import com.clubdeportivo.app.util.FormUtil
import com.clubdeportivo.app.util.PdfUtil

/**
 * Documento de pago. Todos los datos llegan desde la pantalla anterior con putExtra()
 * y se leen acá con getStringExtra() / getDoubleExtra() / getIntExtra().
 */
class ComprobanteActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprobante)
        configurarPantalla("Comprobante de pago")

        val nroComprobante = intent.getIntExtra("nroComprobante", 0)
        val nombre = intent.getStringExtra("nombre") ?: ""
        val nroSocio = intent.getStringExtra("nroSocio") ?: "-"
        val dni = intent.getStringExtra("dni") ?: ""
        val concepto = intent.getStringExtra("concepto") ?: ""
        val importe = intent.getDoubleExtra("importe", 0.0)
        val medioPago = intent.getStringExtra("medioPago") ?: ""
        val cuotas = intent.getIntExtra("cuotas", 1)
        val fecha = intent.getStringExtra("fecha") ?: ""
        val vencimiento = intent.getStringExtra("vencimiento") ?: ""

        val numero = nroComprobante.toString().padStart(6, '0')

        findViewById<TextView>(R.id.tvNroComprobante).text = "Comprobante N° $numero"
        findViewById<TextView>(R.id.tvFecha).text = "Fecha: $fecha"
        findViewById<TextView>(R.id.tvNombre).text = nombre
        findViewById<TextView>(R.id.tvNroSocio).text = "N° de socio: $nroSocio"
        findViewById<TextView>(R.id.tvDni).text = "DNI: $dni"
        findViewById<TextView>(R.id.tvConcepto).text = "• Concepto: $concepto"
        findViewById<TextView>(R.id.tvMedioPago).text = "• Medio de pago: $medioPago"
        findViewById<TextView>(R.id.tvCuotas).text = "• Cuotas: $cuotas"
        val tvVenc = findViewById<TextView>(R.id.tvVencimiento)
        if (vencimiento.isEmpty()) tvVenc.visibility = View.GONE
        else tvVenc.text = "• Nuevo vencimiento: $vencimiento"
        findViewById<TextView>(R.id.tvImporte).text = "Total: ${FormUtil.moneda(importe)}"

        val lineas = mutableListOf(
            "Comprobante N° $numero",
            "Fecha: $fecha",
            "",
            "Recibimos de: $nombre",
            "N° de socio: $nroSocio      DNI: $dni",
            "",
            "Concepto: $concepto",
            "Medio de pago: $medioPago",
            "Cuotas: $cuotas"
        )
        if (vencimiento.isNotEmpty()) lineas.add("Nuevo vencimiento: $vencimiento")
        lineas.add("")
        lineas.add("TOTAL: ${FormUtil.moneda(importe)}")

        findViewById<Button>(R.id.btnGuardarPdf).setOnClickListener {
            val archivo = PdfUtil.guardar(this, "comprobante_$numero.pdf", "Club Deportivo - Comprobante", lineas)
            if (archivo != null) {
                Toast.makeText(this, "PDF generado", Toast.LENGTH_SHORT).show()
                PdfUtil.abrir(this, archivo)
            } else {
                Toast.makeText(this, "No se pudo generar el PDF", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnCompartir).setOnClickListener {
            val envio = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Comprobante de pago N° $numero")
                putExtra(Intent.EXTRA_TEXT, "Club Deportivo\n" + lineas.joinToString("\n"))
            }
            startActivity(Intent.createChooser(envio, "Compartir comprobante"))
        }

        // Cerrar: vuelve al menú principal limpiando las pantallas intermedias
        findViewById<Button>(R.id.btnCerrar).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
        }
    }
}
