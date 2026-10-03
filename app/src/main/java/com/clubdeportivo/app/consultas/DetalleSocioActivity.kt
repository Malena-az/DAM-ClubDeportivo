package com.clubdeportivo.app.consultas

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.registro.MenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetalleSocioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_socio)

        val nombre = intent.getStringExtra("nombre") ?: ""
        val apellido = intent.getStringExtra("apellido") ?: ""
        val dni = intent.getStringExtra("dni") ?: ""
        val telefono = intent.getStringExtra("telefono") ?: ""
        val direccion = intent.getStringExtra("direccion") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val aptoFisico = intent.getBooleanExtra("aptoFisico", false)
        val estado = intent.getStringExtra("estado") ?: "Activo"
        val vencimiento = intent.getStringExtra("fechaVencimiento") ?: ""

        findViewById<TextView>(R.id.txtNombreCompleto).text = "$nombre $apellido"
        findViewById<TextView>(R.id.valDni).text = dni
        findViewById<TextView>(R.id.valNombre).text = nombre
        findViewById<TextView>(R.id.valApellido).text = apellido
        findViewById<TextView>(R.id.valVencimiento).text = vencimiento
        findViewById<TextView>(R.id.valApto).text = if (aptoFisico) "Sí" else "No"
        findViewById<TextView>(R.id.valEstado).text = estado
        findViewById<TextView>(R.id.valTelefono).text = telefono
        findViewById<TextView>(R.id.valDireccion).text = direccion
        findViewById<TextView>(R.id.valCorreo).text = email

        val badge = findViewById<TextView>(R.id.txtBadge)
        badge.text = estado
        val color = when {
            estado.equals("Vencido", ignoreCase = true) -> "#E53935"
            estado.equals("Alerta", ignoreCase = true) -> "#F57C00"
            else -> "#2E7D32"
        }
        badge.setBackgroundColor(Color.parseColor(color))

        val colorTexto = when {
            estado.equals("Vencido", ignoreCase = true) -> Color.parseColor("#E53935")
            estado.equals("Alerta", ignoreCase = true) -> Color.parseColor("#F57C00")
            else -> Color.parseColor("#2E7D32")
        }
        findViewById<TextView>(R.id.valEstado).setTextColor(colorTexto)

        findViewById<TextView>(R.id.btnVolver).setOnClickListener {
            startActivity(Intent(this, ListadoSociosActivity::class.java))
        }

        findViewById<Button>(R.id.btnEditar).setOnClickListener {
            Toast.makeText(this, "Editar socio", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnDarDeBaja).setOnClickListener {
            Toast.makeText(this, "Dar de baja", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnHistorial).setOnClickListener {
            Toast.makeText(this, "Ver historial", Toast.LENGTH_SHORT).show()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_socios
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, MenuActivity::class.java))
                    true
                }
                R.id.nav_socios -> {
                    finish()
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
}