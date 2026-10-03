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
        txtFecha.text = formato.format(Date())

        val vencidos = Datos.socios.filter { it.estado.equals("Vencido", ignoreCase = true) }
        txtCantidad.text = vencidos.size.toString()

        if (vencidos.isEmpty()) {
            layoutVacio.visibility = View.VISIBLE
            txtTituloLista.visibility = View.GONE
            rvVencimientos.visibility = View.GONE
        } else {
            layoutVacio.visibility = View.GONE
            txtTituloLista.visibility = View.VISIBLE
            txtTituloLista.text = "Socios con vencimiento hoy (${vencidos.size})"
            rvVencimientos.visibility = View.VISIBLE
            rvVencimientos.layoutManager = LinearLayoutManager(this)
            rvVencimientos.adapter = VencimientoAdapter(vencidos)
        }

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
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
}
