package com.clubdeportivo.app.consultas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.clubdeportivo.app.BaseActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.ClubDbHelper
import com.clubdeportivo.app.data.Estados
import com.clubdeportivo.app.data.Fechas
import com.clubdeportivo.app.data.Socio

/**
 * Listado diario de socios a vencer: los que vencen hoy y los ya vencidos,
 * ordenados por fecha de vencimiento (ORDER BY en la consulta SQL).
 */
class VencimientosActivity : BaseActivity() {

    private lateinit var db: ClubDbHelper
    private var expandirLista = false
    private var todosVencidos: List<Socio> = emptyList()

    private lateinit var layoutLista: LinearLayout
    private lateinit var layoutVacio: LinearLayout
    private lateinit var btnVerMas: Button
    private lateinit var txtTituloLista: TextView
    private lateinit var txtCantidad: TextView
    private lateinit var txtCantidadVencidos: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimientos)
        configurarPantalla("Control de Vencimientos", R.id.nav_vencimiento)
        db = ClubDbHelper.get(this)

        layoutLista = findViewById(R.id.layoutListaVencimientos)
        layoutVacio = findViewById(R.id.layoutVacio)
        btnVerMas = findViewById(R.id.btnVerMas)
        txtTituloLista = findViewById(R.id.txtTituloLista)
        txtCantidad = findViewById(R.id.txtCantidad)
        txtCantidadVencidos = findViewById(R.id.txtCantidadVencidos)

        findViewById<TextView>(R.id.txtFecha).text = Fechas.aVisible(Fechas.hoy())

        btnVerMas.setOnClickListener {
            expandirLista = !expandirLista
            mostrarItems()
        }
        findViewById<View>(R.id.btnRefrescar).setOnClickListener {
            it.animate().rotationBy(360f).setDuration(400).start()
            cargar()
            Toast.makeText(this, "Lista actualizada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargar()
    }

    private fun cargar() {
        findViewById<TextView>(R.id.txtFecha).text = Fechas.aVisible(Fechas.hoy())
        txtCantidad.text = db.contarVencenHoy().toString()
        txtCantidadVencidos.text = db.contarVencidos().toString()
        todosVencidos = db.listarVencimientos()

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
        }
    }

    private fun mostrarItems() {
        layoutLista.removeAllViews()
        val visibles = if (expandirLista) todosVencidos else todosVencidos.take(2)
        val inflater = LayoutInflater.from(this)

        for (socio in visibles) {
            val item = inflater.inflate(R.layout.item_vencimiento, layoutLista, false)
            val color = Estados.color(socio.estado)
            item.findViewById<TextView>(R.id.txtNombre).text = socio.nombreCompleto
            item.findViewById<TextView>(R.id.txtNumero).text = "N° Socio: ${socio.nroSocio}"
            item.findViewById<TextView>(R.id.txtEstado).apply {
                text = socio.estado
                setBackgroundColor(color)
            }
            item.findViewById<TextView>(R.id.txtDni).text = socio.dni
            item.findViewById<TextView>(R.id.txtNombreSolo).text = socio.nombre
            item.findViewById<TextView>(R.id.txtApellidoSolo).text = socio.apellido
            item.findViewById<TextView>(R.id.txtVencimiento).text = Fechas.aVisible(socio.fechaVencimiento)
            item.findViewById<TextView>(R.id.txtApto).text = if (socio.aptoFisico) "Sí" else "No"
            item.findViewById<TextView>(R.id.txtEstadoValor).apply {
                text = socio.estado
                setTextColor(color)
            }
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
}
