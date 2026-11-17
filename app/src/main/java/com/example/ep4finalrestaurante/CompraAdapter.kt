package com.example.registroclientes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CompraAdapter(
    private var items: List<Compra>
) : RecyclerView.Adapter<CompraAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIngrediente: TextView = view.findViewById(R.id.tvIngrediente)
        val tvDetalle: TextView = view.findViewById(R.id.tvDetalle)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compra, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val c = items[position]
        holder.tvIngrediente.text = c.ingredienteNombre
        holder.tvDetalle.text =
            "${c.cantidad.formatoCantidad()} ${c.unidad}  ·  $${c.costoTotal.formatoDinero()}"
        holder.tvFecha.text = c.fecha
    }

    override fun getItemCount() = items.size

    fun actualizar(nuevos: List<Compra>) {
        items = nuevos
        notifyDataSetChanged()
    }
}
