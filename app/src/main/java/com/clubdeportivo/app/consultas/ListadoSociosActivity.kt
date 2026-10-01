package com.clubdeportivo.app.consultas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.Datos
import com.clubdeportivo.app.data.Socio
import com.clubdeportivo.app.registro.MenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListadoSociosActivity : AppCompatActivity() {

    private lateinit var adapter: SocioAdapter
    private lateinit var txtMostrando: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_socios)

        val rvSocios = findViewById<RecyclerView>(R.id.rvSocios)
        val edtBuscar = findViewById<EditText>(R.id.edtBuscar)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        txtMostrando = findViewById(R.id.txtMostrando)

        adapter = SocioAdapter(Datos.socios.toList())
        rvSocios.layoutManager = LinearLayoutManager(this)
        rvSocios.adapter = adapter
        actualizarMostrando(adapter.itemCount)

        btnBuscar.setOnClickListener {
            val texto = edtBuscar.text.toString().trim().lowercase()
            val filtrados: List<Socio> = if (texto.isEmpty()) {
                Datos.socios.toList()
            } else {
                Datos.socios.filter {
                    it.dni.contains(texto) ||
                            it.nombre.lowercase().contains(texto) ||
                            it.apellido.lowercase().contains(texto)
                }
            }
            adapter.actualizar(filtrados)
            actualizarMostrando(filtrados.size)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_socios
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> startActivity(Intent(this, MenuActivity::class.java))
                R.id.nav_vencimiento -> startActivity(Intent(this, VencimientosActivity::class.java))
                R.id.nav_perfil -> startActivity(Intent(this, PerfilActivity::class.java))
            }
            true
        }
    }

    private fun actualizarMostrando(cantidad: Int) {
        txtMostrando.text = "Mostrando $cantidad socios"
    }
}