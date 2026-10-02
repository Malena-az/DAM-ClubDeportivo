package com.clubdeportivo.app.cobros

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R

class ComprobanteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprobante)

        // Leer los datos que mandó la pantalla anterior con putExtra
        val nombre = intent.getStringExtra("nombre") ?: ""
        val nroSocio = intent.getStringExtra("nroSocio") ?: ""
        val dni = intent.getStringExtra("dni") ?: ""
        val concepto = intent.getStringExtra("concepto") ?: ""
        val importe = intent.getDoubleExtra("importe", 0.0)
        val medioPago = intent.getStringExtra("medioPago") ?: ""
        val fecha = intent.getStringExtra("fecha") ?: ""

        // Mostrar los datos en pantalla
        findViewById<TextView>(R.id.tvNombre).text = nombre
        findViewById<TextView>(R.id.tvNroSocio).text = "N° de Socio: $nroSocio"
        findViewById<TextView>(R.id.tvDni).text = "DNI: $dni"
        findViewById<TextView>(R.id.tvConcepto).text = "• Concepto: $concepto"
        findViewById<TextView>(R.id.tvImporte).text = "• Importe: $" + String.format("%,.2f", importe)
        findViewById<TextView>(R.id.tvMedioPago).text = "• Medio de pago: $medioPago"
        findViewById<TextView>(R.id.tvFecha).text = "• Fecha: $fecha"

        // Volver atrás
        findViewById<TextView>(R.id.btnVolver).setOnClickListener { finish() }

        // Guardar PDF y Compartir: simulados por ahora (no hay backend en esta etapa)
        findViewById<Button>(R.id.btnGuardarPdf).setOnClickListener {
            Toast.makeText(this, "Comprobante guardado como PDF", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnCompartir).setOnClickListener {
            Toast.makeText(this, "Compartiendo comprobante...", Toast.LENGTH_SHORT).show()
        }

        // Cerrar: vuelve al menú principal, limpiando las pantallas intermedias
        findViewById<Button>(R.id.btnCerrar).setOnClickListener {
            val intentMenu = Intent(this, com.clubdeportivo.app.registro.MenuActivity::class.java)
            intentMenu.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intentMenu)
            finish()
        }
    }
}