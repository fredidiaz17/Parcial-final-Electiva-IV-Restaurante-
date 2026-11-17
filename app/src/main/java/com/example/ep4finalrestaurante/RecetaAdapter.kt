package com.example.ep4finalrestaurante

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Recibe la lista mutable de la Activity: cuando la Activity la modifica, llama a refrescar()
class RecetaAdapter(
    private val items: List<RecetaItem>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<RecetaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIngrediente: TextView = view.findViewById(R.id.tvIngrediente)
        val tvCantidad: TextView = view.findViewById(R.id.tvCantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvIngrediente.text = item.ingredienteNombre
        holder.tvCantidad.text = "${formatear(item.cantidad)} ${item.unidad}"
        holder.itemView.setOnClickListener { onClick(holder.adapterPosition) }
    }

    override fun getItemCount() = items.size

    fun refrescar() = notifyDataSetChanged()

    private fun formatear(valor: Double): String =
        if (valor % 1.0 == 0.0) valor.toLong().toString() else valor.toString()
}
