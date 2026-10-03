package com.clubdeportivo.app.consultas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.R
import com.clubdeportivo.app.data.Socio

class SocioAdapter(
    private var lista: List<Socio>,
    private val onItemClick: (Socio) -> Unit
) : RecyclerView.Adapter<SocioAdapter.SocioViewHolder>() {

    class SocioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.txtNombre)
        val txtNumero: TextView = view.findViewById(R.id.txtNumero)
        val txtDni: TextView = view.findViewById(R.id.txtDni)
        val txtEstado: TextView = view.findViewById(R.id.txtEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_socio, parent, false)
        return SocioViewHolder(view)
    }

    override fun onBindViewHolder(holder: SocioViewHolder, position: Int) {
        val socio = lista[position]
        holder.txtNombre.text = "${socio.nombre} ${socio.apellido}"
        holder.txtNumero.text = "N° Socio: ${socio.id.toString().padStart(4, '0')}"
        holder.txtDni.text = "DNI: ${socio.dni}"
        holder.txtEstado.text = socio.estado
        val color = when {
            socio.estado.equals("Vencido", ignoreCase = true) -> "#E53935"
            socio.estado.equals("Alerta", ignoreCase = true) -> "#F57C00"
            else -> "#2E7D32"
        }
        holder.txtEstado.setBackgroundColor(Color.parseColor(color))
        holder.itemView.setOnClickListener { onItemClick(socio) }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizar(nuevaLista: List<Socio>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}