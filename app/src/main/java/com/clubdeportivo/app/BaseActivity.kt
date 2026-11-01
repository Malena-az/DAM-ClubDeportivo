package com.clubdeportivo.app

import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.consultas.ListadoSociosActivity
import com.clubdeportivo.app.consultas.PerfilActivity
import com.clubdeportivo.app.consultas.VencimientosActivity
import com.clubdeportivo.app.registro.MenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {

    /** Encabezado unificado: flecha para volver + título centrado. */
    protected fun configurarEncabezado(titulo: String) {
        findViewById<TextView>(R.id.tvTitulo).text = titulo
        findViewById<TextView>(R.id.btnVolver).setOnClickListener { finish() }
    }

    protected fun configurarBottomNav(tabActual: Int) {
        val nav = findViewById<BottomNavigationView>(R.id.bottomNav)
        nav.selectedItemId = tabActual
        nav.setOnItemSelectedListener { item ->
            if (item.itemId != tabActual) {
                val destino = when (item.itemId) {
                    R.id.nav_inicio -> MenuActivity::class.java
                    R.id.nav_socios -> ListadoSociosActivity::class.java
                    R.id.nav_vencimiento -> VencimientosActivity::class.java
                    R.id.nav_perfil -> PerfilActivity::class.java
                    else -> null
                }
                if (destino != null) {
                    startActivity(Intent(this, destino).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                }
            }
            true
        }
    }

    protected fun configurarPantalla(titulo: String, tabActual: Int = R.id.nav_inicio) {
        configurarEncabezado(titulo)
        configurarBottomNav(tabActual)
    }
}
