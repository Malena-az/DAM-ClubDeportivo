package com.clubdeportivo.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ClubDbHelper private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, "club_deportivo.db", null, 1) {

    companion object {
        const val CUOTA_MENSUAL = "Cuota mensual"
        const val CUOTA_QUINCENAL = "Cuota quincenal"

        @Volatile private var instancia: ClubDbHelper? = null
        fun get(context: Context): ClubDbHelper =
            instancia ?: synchronized(this) {
                instancia ?: ClubDbHelper(context).also { instancia = it }
            }
    }

    // ---------------------------------------------------------------- CREACIÓN
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT NOT NULL UNIQUE, password TEXT NOT NULL,
                nombre TEXT, apellido TEXT, dni TEXT, telefono TEXT,
                direccion TEXT, email TEXT, fecha_alta TEXT)"""
        )
        db.execSQL(
            """CREATE TABLE socios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL, apellido TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE, telefono TEXT, direccion TEXT, email TEXT,
                fecha_nacimiento TEXT, apto_fisico INTEGER NOT NULL DEFAULT 1,
                fecha_alta TEXT NOT NULL, fecha_vencimiento TEXT NOT NULL DEFAULT '',
                activo INTEGER NOT NULL DEFAULT 1)"""
        )
        db.execSQL(
            """CREATE TABLE no_socios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL, apellido TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE, telefono TEXT, observaciones TEXT,
                fecha_alta TEXT)"""
        )
        // Registro de precios: el importe se calcula solo, nadie lo tipea.
        db.execSQL(
            """CREATE TABLE precios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                concepto TEXT NOT NULL UNIQUE, tipo TEXT NOT NULL,
                importe REAL NOT NULL, dias INTEGER NOT NULL DEFAULT 0)"""
        )
        db.execSQL(
            """CREATE TABLE pagos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                socio_id INTEGER, dni TEXT NOT NULL, concepto TEXT NOT NULL,
                importe REAL NOT NULL, medio_pago TEXT NOT NULL,
                cuotas INTEGER NOT NULL DEFAULT 1, fecha_pago TEXT NOT NULL,
                nuevo_vencimiento TEXT NOT NULL DEFAULT '',
                FOREIGN KEY (socio_id) REFERENCES socios(id))"""
        )
        cargarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS pagos")
        db.execSQL("DROP TABLE IF EXISTS precios")
        db.execSQL("DROP TABLE IF EXISTS no_socios")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    private fun cargarDatosIniciales(db: SQLiteDatabase) {
        val hoy = Fechas.hoy()

        db.execSQL(
            "INSERT INTO usuarios (usuario, password, nombre, apellido, dni, telefono, direccion, email, fecha_alta) " +
                    "VALUES ('admin','1234','María','López','95345276','1167864568','Av. Rivadavia','maria@gmail.com',?)",
            arrayOf(Fechas.sumarDias(hoy, -250))
        )

        db.execSQL("INSERT INTO precios (concepto, tipo, importe, dias) VALUES ('$CUOTA_MENSUAL','CUOTA',25000,30)")
        db.execSQL("INSERT INTO precios (concepto, tipo, importe, dias) VALUES ('$CUOTA_QUINCENAL','CUOTA',14000,15)")
        db.execSQL("INSERT INTO precios (concepto, tipo, importe, dias) VALUES ('Natación','ACTIVIDAD',5000,0)")
        db.execSQL("INSERT INTO precios (concepto, tipo, importe, dias) VALUES ('Fútbol','ACTIVIDAD',4000,0)")
        db.execSQL("INSERT INTO precios (concepto, tipo, importe, dias) VALUES ('Tenis','ACTIVIDAD',4500,0)")
        db.execSQL("INSERT INTO precios (concepto, tipo, importe, dias) VALUES ('Pilates','ACTIVIDAD',4800,0)")

        // Socios de prueba. Los vencimientos son relativos a la fecha de instalación
        // para que siempre haya activos, en alerta, vencidos y uno que vence hoy.
        data class Seed(val n: String, val a: String, val dni: String, val tel: String,
                        val dir: String, val mail: String, val offset: Int)
        val seeds = listOf(
            Seed("Maria", "Acuña", "12345678", "1145678901", "Belgrano 123", "maria@gmail.com", 20),
            Seed("Nahuel", "Lopez", "23456789", "1156789012", "Mitre 321", "nahuel@gmail.com", -30),
            Seed("Ana", "Gomez", "34567890", "1189012345", "San Martín 654", "ana@gmail.com", 60),
            Seed("Mauricio", "Martinez", "27987654", "1156789012", "Santa Fe 1456", "mauricio@gmail.com", -25),
            Seed("Sofia", "Fernandez", "38765432", "1178901234", "Cordoba 789", "sofia@gmail.com", 2),
            Seed("Carlos", "Perez", "20123456", "1167890123", "Lavalle 789", "carlos@gmail.com", 0)
        )
        for (s in seeds) {
            val venc = Fechas.sumarDias(hoy, s.offset)
            val cv = ContentValues().apply {
                put("nombre", s.n); put("apellido", s.a); put("dni", s.dni)
                put("telefono", s.tel); put("direccion", s.dir); put("email", s.mail)
                put("fecha_nacimiento", "1990-01-01"); put("apto_fisico", 1)
                put("fecha_alta", Fechas.sumarDias(venc, -120)); put("fecha_vencimiento", venc)
                put("activo", 1)
            }
            val socioId = db.insert("socios", null, cv)
            // un pago previo para que "Ver historial" tenga contenido
            val pago = ContentValues().apply {
                put("socio_id", socioId); put("dni", s.dni); put("concepto", CUOTA_MENSUAL)
                put("importe", 25000.0); put("medio_pago", "Efectivo"); put("cuotas", 1)
                put("fecha_pago", Fechas.sumarDias(venc, -30)); put("nuevo_vencimiento", venc)
            }
            db.insert("pagos", null, pago)
        }
    }

    // ---------------------------------------------------------------- USUARIOS
    /** Mismo criterio que los tips: count(*) distinto de 0 = existe la combinación. */
    fun validarLogin(usuario: String, password: String): Boolean {
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM usuarios WHERE usuario = ? AND password = ?",
            arrayOf(usuario, password)
        ).use { c -> return c.moveToFirst() && c.getInt(0) > 0 }
    }

    fun obtenerUsuario(usuario: String): Usuario? {
        readableDatabase.rawQuery(
            "SELECT id, usuario, nombre, apellido, dni, telefono, direccion, email, fecha_alta " +
                    "FROM usuarios WHERE usuario = ?", arrayOf(usuario)
        ).use { c ->
            if (!c.moveToFirst()) return null
            return Usuario(
                c.getInt(0), c.getString(1), c.getString(2) ?: "", c.getString(3) ?: "",
                c.getString(4) ?: "", c.getString(5) ?: "", c.getString(6) ?: "",
                c.getString(7) ?: "", c.getString(8) ?: ""
            )
        }
    }

    fun cambiarPassword(usuario: String, actual: String, nueva: String): Boolean {
        if (!validarLogin(usuario, actual)) return false
        val cv = ContentValues().apply { put("password", nueva) }
        return writableDatabase.update("usuarios", cv, "usuario = ?", arrayOf(usuario)) > 0
    }

    // ------------------------------------------------------------------ SOCIOS
    private fun leerSocio(c: Cursor) = Socio(
        id = c.getInt(c.getColumnIndexOrThrow("id")),
        nombre = c.getString(c.getColumnIndexOrThrow("nombre")),
        apellido = c.getString(c.getColumnIndexOrThrow("apellido")),
        dni = c.getString(c.getColumnIndexOrThrow("dni")),
        telefono = c.getString(c.getColumnIndexOrThrow("telefono")) ?: "",
        direccion = c.getString(c.getColumnIndexOrThrow("direccion")) ?: "",
        email = c.getString(c.getColumnIndexOrThrow("email")) ?: "",
        fechaNacimiento = c.getString(c.getColumnIndexOrThrow("fecha_nacimiento")) ?: "",
        aptoFisico = c.getInt(c.getColumnIndexOrThrow("apto_fisico")) == 1,
        fechaAlta = c.getString(c.getColumnIndexOrThrow("fecha_alta")) ?: "",
        fechaVencimiento = c.getString(c.getColumnIndexOrThrow("fecha_vencimiento")) ?: "",
        activo = c.getInt(c.getColumnIndexOrThrow("activo")) == 1
    )

    /** @return id asignado al nuevo socio, o -1 si falló (ej: DNI repetido). */
    fun insertarSocio(s: Socio): Long {
        val cv = ContentValues().apply {
            put("nombre", s.nombre); put("apellido", s.apellido); put("dni", s.dni)
            put("telefono", s.telefono); put("direccion", s.direccion); put("email", s.email)
            put("fecha_nacimiento", s.fechaNacimiento)
            put("apto_fisico", if (s.aptoFisico) 1 else 0)
            put("fecha_alta", Fechas.hoy()); put("fecha_vencimiento", "")
            put("activo", 1)
        }
        return writableDatabase.insert("socios", null, cv)
    }

    fun actualizarSocio(s: Socio): Boolean {
        val cv = ContentValues().apply {
            put("nombre", s.nombre); put("apellido", s.apellido)
            put("telefono", s.telefono); put("direccion", s.direccion); put("email", s.email)
            put("fecha_nacimiento", s.fechaNacimiento)
            put("apto_fisico", if (s.aptoFisico) 1 else 0)
        }
        return writableDatabase.update("socios", cv, "id = ?", arrayOf(s.id.toString())) > 0
    }

    /** Baja lógica: el socio y su historial de pagos se conservan en la base. */
    fun darDeBaja(id: Int): Boolean {
        val cv = ContentValues().apply { put("activo", 0) }
        return writableDatabase.update("socios", cv, "id = ?", arrayOf(id.toString())) > 0
    }

    fun existeDniSocio(dni: String): Boolean {
        readableDatabase.rawQuery("SELECT COUNT(*) FROM socios WHERE dni = ?", arrayOf(dni))
            .use { c -> return c.moveToFirst() && c.getInt(0) > 0 }
    }

    fun buscarSocioPorDni(dni: String): Socio? {
        readableDatabase.rawQuery("SELECT * FROM socios WHERE dni = ? AND activo = 1", arrayOf(dni))
            .use { c -> return if (c.moveToFirst()) leerSocio(c) else null }
    }

    fun obtenerSocio(id: Int): Socio? {
        readableDatabase.rawQuery("SELECT * FROM socios WHERE id = ?", arrayOf(id.toString()))
            .use { c -> return if (c.moveToFirst()) leerSocio(c) else null }
    }

    /** Listado con filtro opcional por DNI, nombre o apellido. */
    fun listarSocios(filtro: String = ""): List<Socio> {
        val lista = mutableListOf<Socio>()
        val texto = "%${filtro.trim()}%"
        readableDatabase.rawQuery(
            "SELECT * FROM socios WHERE activo = 1 AND (dni LIKE ? OR nombre LIKE ? OR apellido LIKE ?) " +
                    "ORDER BY apellido, nombre",
            arrayOf(texto, texto, texto)
        ).use { c -> while (c.moveToNext()) lista.add(leerSocio(c)) }
        return lista
    }

    /** Socios que vencen hoy o ya vencieron, del vencimiento más reciente al más viejo. */
    fun listarVencimientos(): List<Socio> {
        val lista = mutableListOf<Socio>()
        readableDatabase.rawQuery(
            "SELECT * FROM socios WHERE activo = 1 AND fecha_vencimiento <> '' AND fecha_vencimiento <= ? " +
                    "ORDER BY fecha_vencimiento DESC, apellido, nombre",
            arrayOf(Fechas.hoy())
        ).use { c -> while (c.moveToNext()) lista.add(leerSocio(c)) }
        return lista
    }

    fun contarVencenHoy(): Int {
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM socios WHERE activo = 1 AND fecha_vencimiento = ?",
            arrayOf(Fechas.hoy())
        ).use { c -> return if (c.moveToFirst()) c.getInt(0) else 0 }
    }

    /** Socios con la cuota ya vencida (vencimiento anterior a hoy). */
    fun contarVencidos(): Int {
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM socios WHERE activo = 1 AND fecha_vencimiento <> '' AND fecha_vencimiento < ?",
            arrayOf(Fechas.hoy())
        ).use { c -> return if (c.moveToFirst()) c.getInt(0) else 0 }
    }

    // --------------------------------------------------------------- NO SOCIOS
    fun insertarNoSocio(n: NoSocio): Long {
        val cv = ContentValues().apply {
            put("nombre", n.nombre); put("apellido", n.apellido); put("dni", n.dni)
            put("telefono", n.telefono); put("observaciones", n.observaciones)
            put("fecha_alta", Fechas.hoy())
        }
        return writableDatabase.insert("no_socios", null, cv)
    }

    fun buscarNoSocioPorDni(dni: String): NoSocio? {
        readableDatabase.rawQuery(
            "SELECT id, nombre, apellido, dni, telefono, observaciones FROM no_socios WHERE dni = ?",
            arrayOf(dni)
        ).use { c ->
            if (!c.moveToFirst()) return null
            return NoSocio(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                c.getString(4) ?: "", c.getString(5) ?: "")
        }
    }

    // ----------------------------------------------------------------- PRECIOS
    fun listarPrecios(tipo: String): List<Precio> {
        val lista = mutableListOf<Precio>()
        readableDatabase.rawQuery(
            "SELECT id, concepto, tipo, importe, dias FROM precios WHERE tipo = ? ORDER BY concepto",
            arrayOf(tipo)
        ).use { c ->
            while (c.moveToNext())
                lista.add(Precio(c.getInt(0), c.getString(1), c.getString(2), c.getDouble(3), c.getInt(4)))
        }
        return lista
    }

    fun obtenerPrecio(concepto: String): Precio? {
        readableDatabase.rawQuery(
            "SELECT id, concepto, tipo, importe, dias FROM precios WHERE concepto = ?", arrayOf(concepto)
        ).use { c ->
            return if (c.moveToFirst())
                Precio(c.getInt(0), c.getString(1), c.getString(2), c.getDouble(3), c.getInt(4))
            else null
        }
    }

    // ------------------------------------------------------------------- PAGOS
    /**
     * Cobra una cuota: guarda el pago y actualiza el vencimiento del socio en una
     * sola transacción (o se hacen las dos cosas o ninguna).
     * Si la cuota todavía está vigente, el nuevo período se suma a partir del
     * vencimiento actual; si está vencida (o es la primera), a partir de hoy.
     */
    fun registrarPagoCuota(socio: Socio, precio: Precio, medioPago: String, cuotas: Int): Pago? {
        val db = writableDatabase
        val hoy = Fechas.hoy()
        val base = if (socio.fechaVencimiento.isNotEmpty() && socio.fechaVencimiento >= hoy)
            socio.fechaVencimiento else hoy
        val nuevoVenc = Fechas.sumarDias(base, precio.dias)

        db.beginTransaction()
        try {
            val cv = ContentValues().apply {
                put("socio_id", socio.id); put("dni", socio.dni); put("concepto", precio.concepto)
                put("importe", precio.importe); put("medio_pago", medioPago); put("cuotas", cuotas)
                put("fecha_pago", hoy); put("nuevo_vencimiento", nuevoVenc)
            }
            val id = db.insert("pagos", null, cv)
            if (id == -1L) return null
            val upd = ContentValues().apply { put("fecha_vencimiento", nuevoVenc) }
            db.update("socios", upd, "id = ?", arrayOf(socio.id.toString()))
            db.setTransactionSuccessful()
            return Pago(id.toInt(), socio.id, socio.dni, precio.concepto, precio.importe,
                medioPago, cuotas, hoy, nuevoVenc)
        } finally {
            db.endTransaction()
        }
    }

    fun registrarPagoActividad(dni: String, precio: Precio, medioPago: String, cuotas: Int, fechaIso: String): Pago? {
        val cv = ContentValues().apply {
            putNull("socio_id"); put("dni", dni); put("concepto", precio.concepto)
            put("importe", precio.importe); put("medio_pago", medioPago); put("cuotas", cuotas)
            put("fecha_pago", fechaIso); put("nuevo_vencimiento", "")
        }
        val id = writableDatabase.insert("pagos", null, cv)
        if (id == -1L) return null
        return Pago(id.toInt(), null, dni, precio.concepto, precio.importe, medioPago, cuotas, fechaIso, "")
    }

    fun listarPagosSocio(socioId: Int): List<Pago> {
        val lista = mutableListOf<Pago>()
        readableDatabase.rawQuery(
            "SELECT id, socio_id, dni, concepto, importe, medio_pago, cuotas, fecha_pago, nuevo_vencimiento " +
                    "FROM pagos WHERE socio_id = ? ORDER BY fecha_pago DESC, id DESC",
            arrayOf(socioId.toString())
        ).use { c ->
            while (c.moveToNext())
                lista.add(Pago(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getDouble(4),
                    c.getString(5), c.getInt(6), c.getString(7), c.getString(8) ?: ""))
        }
        return lista
    }
}
