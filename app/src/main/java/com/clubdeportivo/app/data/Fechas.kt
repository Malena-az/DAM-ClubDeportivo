package com.clubdeportivo.app.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * En la base las fechas se guardan como texto ISO (yyyy-MM-dd) para que
 * ORDER BY y las comparaciones (<, <=) funcionen bien en SQL.
 * En pantalla se muestran como dd/MM/yyyy.
 */
object Fechas {
    private const val ISO = "yyyy-MM-dd"
    private const val VISIBLE = "dd/MM/yyyy"

    fun hoy(): String = SimpleDateFormat(ISO, Locale.US).format(Date())

    fun sumarDias(fechaIso: String, dias: Int): String {
        val cal = Calendar.getInstance()
        val fecha = SimpleDateFormat(ISO, Locale.US).parse(fechaIso)
        if (fecha != null) cal.time = fecha
        cal.add(Calendar.DAY_OF_MONTH, dias)
        return SimpleDateFormat(ISO, Locale.US).format(cal.time)
    }

    fun aVisible(fechaIso: String): String {
        if (fechaIso.isEmpty()) return "-"
        return try {
            SimpleDateFormat(VISIBLE, Locale.US).format(SimpleDateFormat(ISO, Locale.US).parse(fechaIso)!!)
        } catch (e: Exception) {
            fechaIso
        }
    }

    fun aIso(fechaVisible: String): String {
        return try {
            SimpleDateFormat(ISO, Locale.US).format(SimpleDateFormat(VISIBLE, Locale.US).parse(fechaVisible)!!)
        } catch (e: Exception) {
            ""
        }
    }

    /** Estado de la cuota según la fecha de vencimiento (ISO). */
    fun estado(vencimientoIso: String): String {
        if (vencimientoIso.isEmpty()) return Estados.PENDIENTE
        val hoy = hoy()
        return when {
            vencimientoIso < hoy -> Estados.VENCIDO
            vencimientoIso <= sumarDias(hoy, 3) -> Estados.ALERTA   // vence hoy o en los próximos 3 días
            else -> Estados.ACTIVO
        }
    }
}
