package com.example.ep4finalrestaurante

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class PlatoAdapter(
    private var items: List<Plato>,
    private val onClick: (Plato) -> Unit
) : RecyclerView.Adapter<PlatoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plato, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plato = items[position]
        holder.tvNombre.text = plato.nombre
        holder.tvPrecio.text = "Precio: $" + formatearPrecio(plato.precio)
        holder.itemView.setOnClickListener { onClick(plato) }
    }

    override fun getItemCount() = items.size

    fun actualizar(nuevos: List<Plato>) {
        items = nuevos
        notifyDataSetChanged()
    }

    private fun formatearPrecio(valor: Double): String =
        if (valor % 1.0 == 0.0) String.format(Locale.getDefault(), "%,.0f", valor)
        else String.format(Locale.getDefault(), "%,.2f", valor)
}
