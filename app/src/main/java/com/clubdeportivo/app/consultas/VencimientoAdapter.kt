package com.clubdeportivo.app.consultas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.Socio

class VencimientoAdapter(private var lista: List<Socio>) :
    RecyclerView.Adapter<VencimientoAdapter.VencimientoViewHolder>() {

    class VencimientoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.txtNombre)
        val txtNumero: TextView = view.findViewById(R.id.txtNumero)
        val txtEstado: TextView = view.findViewById(R.id.txtEstado)
        val txtDni: TextView = view.findViewById(R.id.txtDni)
        val txtNombreSolo: TextView = view.findViewById(R.id.txtNombreSolo)
        val txtApellidoSolo: TextView = view.findViewById(R.id.txtApellidoSolo)
        val txtVencimiento: TextView = view.findViewById(R.id.txtVencimiento)
        val txtApto: TextView = view.findViewById(R.id.txtApto)
        val txtEstadoValor: TextView = view.findViewById(R.id.txtEstadoValor)
        val txtTelefono: TextView = view.findViewById(R.id.txtTelefono)
        val txtDireccion: TextView = view.findViewById(R.id.txtDireccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VencimientoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vencimiento, parent, false)
        return VencimientoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VencimientoViewHolder, position: Int) {
        val socio = lista[position]
        holder.txtNombre.text = "${socio.nombre} ${socio.apellido}"
        holder.txtNumero.text = "N° Socio: ${socio.id.toString().padStart(4, '0')}"
        holder.txtEstado.text = socio.estado
        holder.txtDni.text = socio.dni
        holder.txtNombreSolo.text = socio.nombre
        holder.txtApellidoSolo.text = socio.apellido
        holder.txtVencimiento.text = socio.fechaVencimiento
        holder.txtApto.text = if (socio.aptoFisico) "Sí" else "No"
        holder.txtEstadoValor.text = socio.estado
        holder.txtTelefono.text = socio.telefono
        holder.txtDireccion.text = socio.direccion

        val color = when {
            socio.estado.equals("Vencido", ignoreCase = true) -> "#E53935"
            socio.estado.equals("Alerta", ignoreCase = true) -> "#F57C00"
            else -> "#2E7D32"
        }
        holder.txtEstado.setBackgroundColor(Color.parseColor(color))
        holder.txtEstadoValor.setTextColor(Color.parseColor(color))
    }

    override fun getItemCount(): Int = lista.size

    fun actualizar(nuevaLista: List<Socio>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
