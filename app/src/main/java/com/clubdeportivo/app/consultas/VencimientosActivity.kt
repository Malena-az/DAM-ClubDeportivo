package com.clubdeportivo.app.consultas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.Datos
import com.clubdeportivo.app.data.Socio
import com.clubdeportivo.app.registro.MenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VencimientosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimientos)

        val rvVencimientos = findViewById<RecyclerView>(R.id.rvVencimientos)
        val txtSinVencimientos = findViewById<TextView>(R.id.txtSinVencimientos)
        val layoutVacio = findViewById<LinearLayout>(R.id.layoutVacio)
        val txtTituloLista = findViewById<TextView>(R.id.txtTituloLista)
        val txtCantidad = findViewById<TextView>(R.id.txtCantidad)
        val txtFecha = findViewById<TextView>(R.id.txtFecha)

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoy = formato.format(Date())
        txtFecha.text = hoy

        // Socios que vencen HOY
        val vencenHoy = Datos.socios.filter { it.fechaVencimiento == hoy }
        txtCantidad.text = vencenHoy.size.toString()

        // Todos los vencidos, ordenados por fecha más reciente primero
        val todosVencidos = Datos.socios
            .filter { it.estado.equals("Vencido", ignoreCase = true) }
            .sortedByDescending { parseFecha(it.fechaVencimiento) }

        if (todosVencidos.isEmpty()) {
            layoutVacio.visibility = View.VISIBLE
            txtTituloLista.visibility = View.GONE
            rvVencimientos.visibility = View.GONE
        } else {
            layoutVacio.visibility = View.GONE
            txtTituloLista.visibility = View.VISIBLE
            txtTituloLista.text = "Todos los vencimientos (${todosVencidos.size})"
            rvVencimientos.visibility = View.VISIBLE
            rvVencimientos.layoutManager = LinearLayoutManager(this)
            rvVencimientos.adapter = VencimientoAdapter(todosVencidos)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_vencimiento
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
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun parseFecha(fecha: String): Date {
        return try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fecha) ?: Date(0)
        } catch (e: Exception) {
            Date(0)
        }
    }
}
