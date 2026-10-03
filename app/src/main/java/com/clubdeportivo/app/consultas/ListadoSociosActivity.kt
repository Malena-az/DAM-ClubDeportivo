package com.clubdeportivo.app.consultas

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.View
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
    private lateinit var txtSinResultados: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_socios)

        val rvSocios = findViewById<RecyclerView>(R.id.rvSocios)
        val edtBuscar = findViewById<EditText>(R.id.edtBuscar)
        txtMostrando = findViewById(R.id.txtMostrando)
        txtSinResultados = findViewById(R.id.txtSinResultados)

        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        adapter = SocioAdapter(Datos.socios.toList()) { socio ->
            val intent = Intent(this, DetalleSocioActivity::class.java)
            intent.putExtra("nombre", socio.nombre)
            intent.putExtra("apellido", socio.apellido)
            intent.putExtra("dni", socio.dni)
            intent.putExtra("telefono", socio.telefono)
            intent.putExtra("direccion", socio.direccion)
            intent.putExtra("email", socio.email)
            intent.putExtra("aptoFisico", socio.aptoFisico)
            intent.putExtra("estado", socio.estado)
            intent.putExtra("fechaVencimiento", socio.fechaVencimiento)
            startActivity(intent)
        }
        rvSocios.layoutManager = LinearLayoutManager(this)
        rvSocios.adapter = adapter
        actualizarMostrando(adapter.itemCount)

        edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString().trim().lowercase()
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
                txtSinResultados.visibility = if (filtrados.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_socios
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, MenuActivity::class.java))
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

    private fun actualizarMostrando(cantidad: Int) {
        txtMostrando.text = "Mostrando $cantidad socios"
    }
}
