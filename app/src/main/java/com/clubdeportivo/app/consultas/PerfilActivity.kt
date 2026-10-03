package com.clubdeportivo.app.consultas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.registro.MenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.TextView
import android.widget.Toast
import com.clubdeportivo.app.registro.LoginActivity
import android.view.View

class PerfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_perfil
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> startActivity(Intent(this, MenuActivity::class.java))
                R.id.nav_socios -> startActivity(Intent(this, ListadoSociosActivity::class.java))
                R.id.nav_vencimiento -> startActivity(
                    Intent(
                        this,
                        VencimientosActivity::class.java
                    )
                )


            }
            true
        }
        // Datos fijos del administrador (sin base de datos en esta etapa)
        findViewById<TextView>(R.id.tvPerfilNombre).text = "María López"
        findViewById<TextView>(R.id.tvPerfilRol).text = "Socio Activo"
        findViewById<TextView>(R.id.tvPerfilNro).text = "N° Socio: 1111"
        findViewById<TextView>(R.id.tvPerfilDni).text = "95345276"
        findViewById<TextView>(R.id.tvPerfilNom).text = "María"
        findViewById<TextView>(R.id.tvPerfilApellido).text = "López"
        findViewById<TextView>(R.id.tvPerfilAlta).text = "02/02/2026"
        findViewById<TextView>(R.id.tvPerfilTel).text = "1167864568"
        findViewById<TextView>(R.id.tvPerfilDir).text = "Av. Rivadavia"
        findViewById<TextView>(R.id.tvPerfilCorreo).text = "maria@gmail.com"

        findViewById<TextView>(R.id.btnVolver).setOnClickListener { finish() }

        findViewById<View>(R.id.accionPassword).setOnClickListener {
            Toast.makeText(this, "Función en desarrollo", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.accionConfig).setOnClickListener {
            Toast.makeText(this, "Función en desarrollo", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.accionCerrar).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}