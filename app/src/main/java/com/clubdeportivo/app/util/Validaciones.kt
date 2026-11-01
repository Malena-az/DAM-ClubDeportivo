package com.clubdeportivo.app.util

import android.widget.EditText

private val REGEX_DNI = Regex("^[0-9]{7,8}$")

fun esDniValido(dni: String): Boolean = dni.matches(REGEX_DNI)

/**
 * Devuelve el texto (sin espacios) o, si está vacío, marca el error en el campo,
 * le da el foco y devuelve null.
 */
fun EditText.requerido(mensaje: String): String? {
    val texto = text.toString().trim()
    if (texto.isEmpty()) {
        error = mensaje
        requestFocus()
        return null
    }
    return texto
}

/** Igual que [requerido] pero además exige un DNI de 7 u 8 dígitos. */
fun EditText.dniRequerido(): String? {
    val dni = requerido("Ingresá el DNI") ?: return null
    if (!esDniValido(dni)) {
        error = "El DNI debe tener entre 7 y 8 dígitos"
        requestFocus()
        return null
    }
    return dni
}
