package com.clubdeportivo.app.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Datos {
    val socios = mutableListOf(
        Socio(1, "Maria", "Acuña", "12345678", "1145678901", "Belgrano 123", "maria@gmail.com", true , "Activo", "15/10/2026"),
        Socio(2, "Nahuel", "Lopez", "23456789", "1156789012", "Mitre 321", "nahuel@gmail.com", true, "Vencido", "02/09/2026"),
        Socio(3, "Ana", "Gomez","34567890", "1189012345", "San Martín 654","ana@gmail.com", true, "Activo","05/12/2026"),
        Socio(4,"Mauricio","Martinez","27987654", "1156789012","Santa Fe 1456", "mauricio@gmail.com",true,"Vencido","10/09/2026" ),
        Socio(5, "Sofia", "Fernandez", "38765432", "1178901234", "Cordoba 789", "sofia@gmail.com", true, "Alerta", "20/10/2026"),
        Socio(6, "Carlos", "Perez", "20123456", "1167890123", "Lavalle 789", "carlos@gmail.com", true, "Vencido", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
    )
    val pagos = mutableListOf<Pago>()

    fun buscarPorDni(dni: String): Socio? = socios.find { it.dni == dni }
}