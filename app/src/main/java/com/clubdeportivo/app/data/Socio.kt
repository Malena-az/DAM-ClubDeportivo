package com.clubdeportivo.app.data

import android.R

data class Socio(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val telefono: String ="",
    val direccion: String ="",
    val email: String ="",
    val aptoFisico: Boolean = true,
    val estado : String = "Activo",
    val fechaVencimiento: String=""
)