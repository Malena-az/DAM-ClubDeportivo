package com.clubdeportivo.app.consultas

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.clubdeportivo.app.BaseActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.ClubDbHelper
import com.clubdeportivo.app.data.Estados
import com.clubdeportivo.app.data.Fechas
import com.clubdeportivo.app.data.Socio
import com.clubdeportivo.app.util.PdfUtil
import com.clubdeportivo.app.util.dniRequerido

/** El empleado busca al socio por DNI y ve (o descarga) su carnet. Los datos salen de la base. */
class CarnetActivity : BaseActivity() {

    private lateinit var txtSinResultados: TextView
    private lateinit var layoutCarnet: View
    private lateinit var btnDescargar: Button
    private var socioMostrado: Socio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)
        configurarPantalla("Carnet de socio")
        val db = ClubDbHelper.get(this)

        txtSinResultados = findViewById(R.id.txtSinResultados)
        layoutCarnet = findViewById(R.id.layoutCarnet)
        btnDescargar = findViewById(R.id.btnDescargar)
        mostrarCarnet(null, mostrarMensaje = false)

        val edtBuscar = findViewById<EditText>(R.id.edtBuscarDni)
        findViewById<Button>(R.id.btnBuscarCarnet).setOnClickListener {
            val dni = edtBuscar.dniRequerido() ?: return@setOnClickListener
            mostrarCarnet(db.buscarSocioPorDni(dni), mostrarMensaje = true)
        }

        // Llegada desde el detalle de un socio: ya viene el DNI
        intent.getStringExtra("dni")?.let { dniRecibido ->
            edtBuscar.setText(dniRecibido)
            mostrarCarnet(db.buscarSocioPorDni(dniRecibido), mostrarMensaje = true)
        }

        btnDescargar.setOnClickListener {
            val s = socioMostrado ?: return@setOnClickListener
            val lineas = listOf(
                "Socio: ${s.nombreCompleto}",
                "N° de socio: ${s.nroSocio}",
                "DNI: ${s.dni}",
                "Fecha de alta: ${Fechas.aVisible(s.fechaAlta)}",
                "Vencimiento: ${Fechas.aVisible(s.fechaVencimiento)}",
                "Apto físico: ${if (s.aptoFisico) "Sí" else "No"}",
                "Estado: ${s.estado}",
                "",
                "Este carnet es personal e intransferible."
            )
            val archivo = PdfUtil.guardar(this, "carnet_${s.nroSocio}.pdf", "Club Deportivo - Carnet de socio", lineas)
            if (archivo != null) {
                Toast.makeText(this, "Carnet generado", Toast.LENGTH_SHORT).show()
                PdfUtil.abrir(this, archivo)
            } else {
                Toast.makeText(this, "No se pudo generar el carnet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarCarnet(socio: Socio?, mostrarMensaje: Boolean) {
        socioMostrado = socio
        if (socio == null) {
            layoutCarnet.visibility = View.GONE
            btnDescargar.visibility = View.GONE
            txtSinResultados.visibility = if (mostrarMensaje) View.VISIBLE else View.GONE
            return
        }

        layoutCarnet.visibility = View.VISIBLE
        btnDescargar.visibility = View.VISIBLE
        txtSinResultados.visibility = View.GONE

        findViewById<TextView>(R.id.txtNombre).text = socio.nombreCompleto
        findViewById<TextView>(R.id.txtNumero).text = "N° Socio: ${socio.nroSocio}"
        findViewById<TextView>(R.id.txtDni).text = "DNI: ${socio.dni}"
        findViewById<TextView>(R.id.valAlta).text = Fechas.aVisible(socio.fechaAlta)
        findViewById<TextView>(R.id.valVencimiento).text = Fechas.aVisible(socio.fechaVencimiento)
        findViewById<TextView>(R.id.valApto).text = if (socio.aptoFisico) "Sí" else "No"

        val estado = socio.estado
        val color = Estados.color(estado)
        val valEstado = findViewById<TextView>(R.id.valEstado)
        valEstado.text = estado
        valEstado.setTextColor(color)

        val barra = findViewById<TextView>(R.id.txtBarraEstado)
        barra.text = when (estado) {
            Estados.VENCIDO -> "SOCIO VENCIDO"
            Estados.PENDIENTE -> "CUOTA PENDIENTE"
            else -> "SOCIO ACTIVO"      // Activo o Alerta: hasta el día del vencimiento puede ingresar
        }
        barra.setBackgroundColor(color)
    }
}
