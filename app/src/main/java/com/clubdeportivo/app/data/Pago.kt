package com.clubdeportivo.app.data

data class Pago(
    val id: Int,
    val dni : String,
    val concepto : String,
    val importe: Double,
    val medioPago: String,
    val fecha: String
)