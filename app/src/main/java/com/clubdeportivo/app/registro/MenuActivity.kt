package com.clubdeportivo.app.registro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.consultas.ListadoSociosActivity
import com.clubdeportivo.app.consultas.PerfilActivity
import com.clubdeportivo.app.consultas.VencimientosActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val txtFecha = findViewById<TextView>(R.id.txtFecha)
        val formato = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        txtFecha.text = formato.format(Date())

        findViewById<View>(R.id.cardRegistrarSocio).setOnClickListener {
            startActivity(Intent(this, AltaSocioActivity::class.java))
        }
        findViewById<View>(R.id.cardRegistrarNoSocio).setOnClickListener {
            startActivity(Intent(this, AltaNoSocioActivity::class.java))
        }

        val tarjetasDesarrollo = listOf(
            R.id.cardCobrarCuota,
            R.id.cardCobrarActividad,
            R.id.cardCarnet
        )
        for (id in tarjetasDesarrollo) {
            findViewById<View>(id).setOnClickListener {
                Toast.makeText(this, "Función en desarrollo", Toast.LENGTH_SHORT).show()
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_inicio
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> true
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
}