package com.clubdeportivo.app.consultas

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.BaseActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.ClubDbHelper

class ListadoSociosActivity : BaseActivity() {

    private lateinit var db: ClubDbHelper
    private lateinit var adapter: SocioAdapter
    private lateinit var edtBuscar: EditText
    private lateinit var txtMostrando: TextView
    private lateinit var txtSinResultados: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_socios)
        configurarPantalla("Listado de Socios", R.id.nav_socios)
        db = ClubDbHelper.get(this)

        edtBuscar = findViewById(R.id.edtBuscar)
        txtMostrando = findViewById(R.id.txtMostrando)
        txtSinResultados = findViewById(R.id.txtSinResultados)

        adapter = SocioAdapter(emptyList()) { socio ->
            startActivity(Intent(this, DetalleSocioActivity::class.java).apply {
                putExtra("socioId", socio.id)
            })
        }
        findViewById<RecyclerView>(R.id.rvSocios).apply {
            layoutManager = LinearLayoutManager(this@ListadoSociosActivity)
            adapter = this@ListadoSociosActivity.adapter
        }

        // Filtra mientras se escribe (DNI, nombre o apellido) y también con el botón Buscar
        edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = filtrar()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        findViewById<Button>(R.id.btnBuscar).setOnClickListener { filtrar() }
    }

    // Se recarga al volver (por si se editó o se dio de baja un socio)
    override fun onResume() {
        super.onResume()
        filtrar()
    }

    private fun filtrar() {
        val socios = db.listarSocios(edtBuscar.text.toString())
        adapter.actualizar(socios)
        txtMostrando.text = "Mostrando ${socios.size} socios"
        txtSinResultados.visibility = if (socios.isEmpty()) View.VISIBLE else View.GONE
    }
}
