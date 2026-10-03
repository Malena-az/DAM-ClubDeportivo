package com.clubdeportivo.app.consultas

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.Datos
import com.clubdeportivo.app.data.Socio
import com.clubdeportivo.app.registro.MenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CarnetActivity : AppCompatActivity() {

    private lateinit var txtSinResultados: TextView
    private lateinit var layoutCarnet: View
    private lateinit var btnDescargar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        txtSinResultados = findViewById(R.id.txtSinResultados)
        layoutCarnet = findViewById(R.id.layoutCarnet)
        btnDescargar = findViewById(R.id.btnDescargar)

        val edtBuscar = findViewById<EditText>(R.id.edtBuscarDni)
        findViewById<Button>(R.id.btnBuscarCarnet).setOnClickListener {
            val dni = edtBuscar.text.toString().trim()
            if (dni.isEmpty()) {
                Toast.makeText(this, "Ingresá un DNI", Toast.LENGTH_SHORT).show()
            } else {
                val socio = Datos.socios.find { it.dni == dni }
                mostrarCarnet(socio)
            }
        }

        val socioInicial = Datos.socios.firstOrNull { it.estado.equals("Activo", ignoreCase = true) }
        mostrarCarnet(socioInicial)

        findViewById<TextView>(R.id.btnVolver).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        btnDescargar.setOnClickListener {
            Toast.makeText(this, "Carnet descargado", Toast.LENGTH_SHORT).show()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, MenuActivity::class.java))
                    true
                }
                R.id.nav_socios -> {
                    startActivity(Intent(this, ListadoSociosActivity::class.java))
                    true
                }
                R.id.nav_vencimiento -> {
                    startActivity(Intent(this, VencimientosActivity::class.java))
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun mostrarCarnet(socio: Socio?) {
        if (socio == null) {
            layoutCarnet.visibility = View.GONE
            btnDescargar.visibility = View.GONE
            txtSinResultados.visibility = View.VISIBLE
            return
        }

        layoutCarnet.visibility = View.VISIBLE
        btnDescargar.visibility = View.VISIBLE
        txtSinResultados.visibility = View.GONE

        findViewById<TextView>(R.id.txtNombre).text = "${socio.nombre} ${socio.apellido}"
        findViewById<TextView>(R.id.txtNumero).text = "N° Socio: ${socio.id.toString().padStart(4, '0')}"
        findViewById<TextView>(R.id.txtDni).text = "DNI: ${socio.dni}"
        findViewById<TextView>(R.id.valAlta).text = "01/01/2026"
        findViewById<TextView>(R.id.valVencimiento).text = socio.fechaVencimiento
        findViewById<TextView>(R.id.valApto).text = if (socio.aptoFisico) "Sí" else "No"
        findViewById<TextView>(R.id.valEstado).text = socio.estado

        val esActivo = socio.estado.equals("Activo", ignoreCase = true)
        val barra = findViewById<TextView>(R.id.txtBarraEstado)
        val colorEstado = if (esActivo) "#2E7D32" else "#E53935"

        barra.text = if (esActivo) "SOCIO ACTIVO" else "SOCIO VENCIDO"
        barra.setBackgroundColor(Color.parseColor(colorEstado))
        findViewById<TextView>(R.id.valEstado).setTextColor(Color.parseColor(colorEstado))
    }
}
