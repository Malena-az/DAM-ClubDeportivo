package com.clubdeportivo.app.data

import android.graphics.Color

object Estados {
    const val ACTIVO = "Activo"
    const val ALERTA = "Alerta"
    const val VENCIDO = "Vencido"
    const val PENDIENTE = "Pendiente"

    /** Un único lugar define el color de cada estado para todas las pantallas. */
    fun color(estado: String): Int = when (estado) {
        VENCIDO -> Color.parseColor("#E53935")
        ALERTA, PENDIENTE -> Color.parseColor("#F57C00")
        else -> Color.parseColor("#2E7D32")
    }
}

data class Socio(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val telefono: String = "",
    val direccion: String = "",
    val email: String = "",
    val fechaNacimiento: String = "",   // ISO
    val aptoFisico: Boolean = true,
    val fechaAlta: String = "",         // ISO
    val fechaVencimiento: String = "",  // ISO ("" si todavía no pagó la primera cuota)
    val activo: Boolean = true
) {
    val estado: String get() = Fechas.estado(fechaVencimiento)
    /**
     * La cuota está al día cuando todavía falta más de 3 días para el vencimiento.
     * Desde 3 días antes (estado Alerta), el día del vencimiento y después, se puede cobrar la próxima.
     */
    val cuotaAlDia: Boolean get() = estado == Estados.ACTIVO
    val nombreCompleto: String get() = "$nombre $apellido"
    val nroSocio: String get() = id.toString().padStart(4, '0')
}

data class NoSocio(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val telefono: String,
    val observaciones: String
)

data class Precio(
    val id: Int,
    val concepto: String,
    val tipo: String,       // "CUOTA" o "ACTIVIDAD"
    val importe: Double,
    val dias: Int           // días que suma al vencimiento (solo cuotas)
)

data class Pago(
    val id: Int,
    val socioId: Int?,
    val dni: String,
    val concepto: String,
    val importe: Double,
    val medioPago: String,
    val cuotas: Int,
    val fechaPago: String,          // ISO
    val nuevoVencimiento: String    // ISO ("" si no es una cuota)
)

data class Usuario(
    val id: Int,
    val usuario: String,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val telefono: String,
    val direccion: String,
    val email: String,
    val fechaAlta: String
)

/** Usuario que inició sesión (se usa en Menú y Perfil). */
object Sesion {
    var usuario: String = ""
}
