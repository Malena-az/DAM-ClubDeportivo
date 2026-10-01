package com.clubdeportivo.app.consultas

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.Datos

class VencimientosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimientos)

        val rvVencimientos = findViewById<RecyclerView>(R.id.rvVencimientos)
        val txtSinVencimientos = findViewById<TextView>(R.id.txtSinVencimientos)

        val vencidos = Datos.socios.filter { it.estado.equals("Vencido", ignoreCase = true) }

        if (vencidos.isEmpty()) {
            txtSinVencimientos.visibility = View.VISIBLE
            rvVencimientos.visibility = View.GONE
        } else {
            txtSinVencimientos.visibility = View.GONE
            rvVencimientos.visibility = View.VISIBLE
            rvVencimientos.layoutManager = LinearLayoutManager(this)
            rvVencimientos.adapter = VencimientoAdapter(vencidos)
        }

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_vencimiento
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> startActivity(android.content.Intent(this, com.clubdeportivo.app.registro.MenuActivity::class.java))
                R.id.nav_socios -> startActivity(android.content.Intent(this, ListadoSociosActivity::class.java))
                R.id.nav_perfil -> startActivity(android.content.Intent(this, PerfilActivity::class.java))
            }
            true
        }
    }
}