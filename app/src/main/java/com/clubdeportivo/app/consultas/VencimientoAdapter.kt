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
        val txtDni: TextView = view.findViewById(R.id.txtDni)
        val txtDireccion: TextView = view.findViewById(R.id.txtDireccion)
        val txtTelefono: TextView = view.findViewById(R.id.txtTelefono)
        val txtApto: TextView = view.findViewById(R.id.txtApto)
        val txtVencimiento: TextView = view.findViewById(R.id.txtVencimiento)
        val txtEstado: TextView = view.findViewById(R.id.txtEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VencimientoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vencimiento, parent, false)
        return VencimientoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VencimientoViewHolder, position: Int) {
        val socio = lista[position]
        holder.txtNombre.text = "${socio.nombre} ${socio.apellido}"
        holder.txtNumero.text = "N° ${socio.id.toString().padStart(4, '0')}"
        holder.txtDni.text = "DNI ${socio.dni}"
        holder.txtDireccion.text = "Dirección: ${socio.direccion}"
        holder.txtTelefono.text = "Teléfono: ${socio.telefono}"
        holder.txtApto.text = if (socio.aptoFisico) "Apto físico: Sí" else "Apto físico: No"
        holder.txtVencimiento.text = "Vence: ${socio.fechaVencimiento}"
        holder.txtEstado.text = socio.estado

        if (socio.estado.equals("Vencido", ignoreCase = true)) {
            holder.txtEstado.setBackgroundColor(Color.parseColor("#E53935"))
        } else {
            holder.txtEstado.setBackgroundColor(Color.parseColor("#2E7D32"))
        }
    }

    override fun getItemCount(): Int = lista.size
}