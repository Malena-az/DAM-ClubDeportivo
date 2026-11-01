package com.clubdeportivo.app.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/** Genera un PDF de una página y lo muestra: con el visor del celular o, si no hay, con una vista previa propia. */
object PdfUtil {

    /**
     * Guarda el PDF en la carpeta Documents propia de la app (no necesita permisos).
     * @return el archivo creado, o null si algo falló.
     */
    fun guardar(context: Context, nombreArchivo: String, titulo: String, lineas: List<String>): File? {
        val documento = PdfDocument()
        return try {
            val pagina = documento.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
            val canvas = pagina.canvas

            // Banda verde con el nombre del club
            val fondo = Paint().apply { color = Color.parseColor("#2E7D32") }
            canvas.drawRect(0f, 0f, 595f, 90f, fondo)
            val pincelTitulo = Paint().apply { color = Color.WHITE; textSize = 24f; isFakeBoldText = true }
            canvas.drawText(titulo, 40f, 55f, pincelTitulo)

            val pincel = Paint().apply { color = Color.BLACK; textSize = 15f }
            val negrita = Paint().apply { color = Color.BLACK; textSize = 18f; isFakeBoldText = true }
            var y = 140f
            for (linea in lineas) {
                val esTotal = linea.startsWith("TOTAL")
                canvas.drawText(linea, 40f, y, if (esTotal) negrita else pincel)
                y += 28f
            }

            val linea = Paint().apply { color = Color.GRAY; strokeWidth = 1f }
            canvas.drawLine(40f, 780f, 555f, 780f, linea)
            canvas.drawText("Club Deportivo - Tu esfuerzo, nuestra comunidad", 40f, 800f,
                Paint().apply { color = Color.GRAY; textSize = 11f })

            documento.finishPage(pagina)

            val carpeta = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            carpeta.mkdirs()
            val archivo = File(carpeta, nombreArchivo)
            FileOutputStream(archivo).use { documento.writeTo(it) }
            archivo
        } catch (e: Exception) {
            null
        } finally {
            documento.close()
        }
    }

    /** Intenta abrir el PDF con el visor instalado; si no hay ninguno, lo muestra en una vista previa. */
    fun abrir(context: Context, archivo: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            vistaPrevia(context, archivo)
        } catch (e: Exception) {
            vistaPrevia(context, archivo)
        }
    }

    /** Dibuja la primera página del PDF en un diálogo (no depende de otras apps). */
    private fun vistaPrevia(context: Context, archivo: File) {
        try {
            val pfd = ParcelFileDescriptor.open(archivo, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)
            val pagina = renderer.openPage(0)
            val bitmap = Bitmap.createBitmap(pagina.width * 2, pagina.height * 2, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.WHITE)
            pagina.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            pagina.close()
            renderer.close()
            pfd.close()

            val imagen = ImageView(context).apply {
                setImageBitmap(bitmap)
                adjustViewBounds = true
            }
            val scroll = ScrollView(context).apply { addView(imagen) }
            AlertDialog.Builder(context)
                .setTitle(archivo.name)
                .setView(scroll)
                .setPositiveButton("Cerrar", null)
                .show()
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo mostrar el PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
