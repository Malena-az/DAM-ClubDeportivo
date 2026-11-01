package com.clubdeportivo.app.util

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.clubdeportivo.app.R
import java.util.Locale

/** Cosas de formularios que se repiten en varias pantallas. */
object FormUtil {
    const val CREDITO = "Tarjeta de crédito"

    fun moneda(valor: Double): String =
        "$ " + String.format(Locale("es", "AR"), "%,.2f", valor)

    /**
     * Carga los spinners de medio de pago y cuotas (desde arrays.xml) y deja las cuotas
     * habilitadas solo cuando el medio es tarjeta de crédito.
     */
    fun configurarMedioYCuotas(context: Context, spMedio: Spinner, spCuotas: Spinner) {
        spMedio.adapter = ArrayAdapter.createFromResource(
            context, R.array.medios_pago, android.R.layout.simple_spinner_dropdown_item
        )
        spCuotas.adapter = ArrayAdapter.createFromResource(
            context, R.array.cuotas, android.R.layout.simple_spinner_dropdown_item
        )
        actualizarCuotas(spMedio, spCuotas)

        spMedio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                actualizarCuotas(spMedio, spCuotas)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun actualizarCuotas(spMedio: Spinner, spCuotas: Spinner) {
        val medio = spMedio.selectedItem?.toString() ?: ""
        val esCredito = medio == CREDITO
        spCuotas.isEnabled = esCredito
        spCuotas.alpha = if (esCredito) 1f else 0.5f
        when {
            esCredito -> spCuotas.setSelection(0)                       // obliga a elegir
            spMedio.selectedItemPosition == 0 -> spCuotas.setSelection(0)
            else -> spCuotas.setSelection(1)                            // 1 cuota
        }
    }

    /** Cantidad de cuotas elegida (1 si el medio no es tarjeta de crédito). */
    fun cuotasElegidas(spCuotas: Spinner): Int =
        if (spCuotas.isEnabled) spCuotas.selectedItem.toString().toIntOrNull() ?: 1 else 1
}
