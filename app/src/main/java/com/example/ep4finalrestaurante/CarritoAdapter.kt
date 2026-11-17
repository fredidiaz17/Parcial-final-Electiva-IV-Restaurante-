package com.example.registroclientes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Recibe la lista mutable de la Activity: cuando la Activity la modifica, llama a refrescar()
class CarritoAdapter(
    private val items: List<ItemVenta>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDetalle: TextView = view.findViewById(R.id.tvDetalle)
        val tvSubtotal: TextView = view.findViewById(R.id.tvSubtotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvNombre.text = item.nombre
        holder.tvDetalle.text = "${item.cantidad} × $${item.precio.formatoDinero()}"
        holder.tvSubtotal.text = "$${item.subtotal.formatoDinero()}"
        holder.itemView.setOnClickListener { onClick(holder.adapterPosition) }
    }

    override fun getItemCount() = items.size

    fun refrescar() = notifyDataSetChanged()
}
