package com.example.dam2cgrupo4

import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val registrarSocio = findViewById<LinearLayout>(R.id.btnRegistrarSocio)
        val registrarNoSocio = findViewById<LinearLayout>(R.id.btnRegistrarNoSocio)
        val navInicio = findViewById<LinearLayout>(R.id.navInicio)

        // Resaltar ícono de Inicio como pantalla activa
        val iconoInicio = navInicio.getChildAt(0) as ImageView
        iconoInicio.setColorFilter(resources.getColor(R.color.verde, theme))

        registrarSocio.setOnClickListener {
            val intent = Intent(this, AltaSocioActivity::class.java)
            startActivity(intent)
        }

        registrarNoSocio.setOnClickListener {
            val intent = Intent(this, AltaNoSocioActivity::class.java)
            startActivity(intent)
        }
    }
}