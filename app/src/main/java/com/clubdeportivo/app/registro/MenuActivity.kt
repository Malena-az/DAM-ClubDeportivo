package com.clubdeportivo.app.registro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.clubdeportivo.app.BaseActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.cobros.CobrarActividadActivity
import com.clubdeportivo.app.cobros.CobrarCuotaActivity
import com.clubdeportivo.app.consultas.CarnetActivity
import com.clubdeportivo.app.data.ClubDbHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MenuActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        configurarBottomNav(R.id.nav_inicio)

        val usuario = ClubDbHelper.get(this).obtenerUsuario(com.clubdeportivo.app.data.Sesion.usuario)
        findViewById<TextView>(R.id.txtSaludo).text = "Hola, ${usuario?.nombre ?: "Admin"}"

        val formato = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        findViewById<TextView>(R.id.txtFecha).text = formato.format(Date())

        irA(R.id.cardRegistrarSocio, AltaSocioActivity::class.java)
        irA(R.id.cardRegistrarNoSocio, AltaNoSocioActivity::class.java)
        irA(R.id.cardCarnet, CarnetActivity::class.java)
        irA(R.id.cardCobrarCuota, CobrarCuotaActivity::class.java)
        irA(R.id.cardCobrarActividad, CobrarActividadActivity::class.java)
    }

    private fun irA(idCard: Int, destino: Class<*>) {
        findViewById<View>(idCard).setOnClickListener { startActivity(Intent(this, destino)) }
    }
}
