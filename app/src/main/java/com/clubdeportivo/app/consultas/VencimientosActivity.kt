package com.clubdeportivo.app.consultas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.Datos
import com.clubdeportivo.app.data.Socio
import com.clubdeportivo.app.registro.MenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VencimientosActivity : AppCompatActivity() {

    private var expandirLista = false
    private lateinit var layoutLista: LinearLayout
    private lateinit var btnVerMas: Button
    private lateinit var todosVencidos: List<Socio>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimientos)

        val txtSinVencimientos = findViewById<TextView>(R.id.txtSinVencimientos)
        val layoutVacio = findViewById<LinearLayout>(R.id.layoutVacio)
        val txtTituloLista = findViewById<TextView>(R.id.txtTituloLista)
        val txtCantidad = findViewById<TextView>(R.id.txtCantidad)
        val txtFecha = findViewById<TextView>(R.id.txtFecha)
        layoutLista = findViewById(R.id.layoutListaVencimientos)
        btnVerMas = findViewById(R.id.btnVerMas)

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoy = formato.format(Date())
        txtFecha.text = hoy

        val vencenHoy = Datos.socios.filter { it.fechaVencimiento == hoy }
        txtCantidad.text = vencenHoy.size.toString()

        todosVencidos = Datos.socios
            .filter { it.estado.equals("Vencido", ignoreCase = true) }
            .sortedByDescending { parseFecha(it.fechaVencimiento) }

        if (todosVencidos.isEmpty()) {
            layoutVacio.visibility = View.VISIBLE
            txtTituloLista.visibility = View.GONE
            layoutLista.visibility = View.GONE
            btnVerMas.visibility = View.GONE
        } else {
            layoutVacio.visibility = View.GONE
            txtTituloLista.visibility = View.VISIBLE
            txtTituloLista.text = "Todos los vencimientos (${todosVencidos.size})"
            layoutLista.visibility = View.VISIBLE
            mostrarItems()

            btnVerMas.setOnClickListener {
                expandirLista = !expandirLista
                mostrarItems()
            }
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

    private fun mostrarItems() {
        layoutLista.removeAllViews()
        val visibles = if (expandirLista) todosVencidos else todosVencidos.take(2)
        val inflater = LayoutInflater.from(this)

        for (socio in visibles) {
            val item = inflater.inflate(R.layout.item_vencimiento, layoutLista, false)
            item.findViewById<TextView>(R.id.txtNombre).text = "${socio.nombre} ${socio.apellido}"
            item.findViewById<TextView>(R.id.txtNumero).text = "N° Socio: ${socio.id.toString().padStart(4, '0')}"
            item.findViewById<TextView>(R.id.txtEstado).text = socio.estado
            item.findViewById<TextView>(R.id.txtDni).text = socio.dni
            item.findViewById<TextView>(R.id.txtNombreSolo).text = socio.nombre
            item.findViewById<TextView>(R.id.txtApellidoSolo).text = socio.apellido
            item.findViewById<TextView>(R.id.txtVencimiento).text = socio.fechaVencimiento
            item.findViewById<TextView>(R.id.txtApto).text = if (socio.aptoFisico) "Sí" else "No"
            item.findViewById<TextView>(R.id.txtEstadoValor).text = socio.estado
            item.findViewById<TextView>(R.id.txtTelefono).text = socio.telefono
            item.findViewById<TextView>(R.id.txtDireccion).text = socio.direccion
            layoutLista.addView(item)
        }

        if (todosVencidos.size > 2) {
            btnVerMas.visibility = View.VISIBLE
            btnVerMas.text = if (expandirLista) "Ver menos" else "Ver más (${todosVencidos.size - 2} más)"
        } else {
            btnVerMas.visibility = View.GONE
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
