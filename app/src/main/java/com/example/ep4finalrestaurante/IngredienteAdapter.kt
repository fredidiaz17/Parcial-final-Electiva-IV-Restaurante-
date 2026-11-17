package com.example.ep4finalrestaurante

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredienteAdapter(
    private var items: List<Ingrediente>,
    private val onClick: (Ingrediente) -> Unit
) : RecyclerView.Adapter<IngredienteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvStock: TextView = view.findViewById(R.id.tvStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingrediente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ing = items[position]
        holder.tvNombre.text = ing.nombre
        holder.tvStock.text =
            "Stock: ${formatear(ing.stockActual)} ${ing.unidad}  (mín. ${formatear(ing.stockMinimo)})"

        // En rojo cuando el stock está en el mínimo o por debajo
        val bajo = ing.stockMinimo > 0 && ing.stockActual <= ing.stockMinimo
        holder.tvStock.setTextColor(if (bajo) Color.RED else Color.GRAY)

        holder.itemView.setOnClickListener { onClick(ing) }
    }

    override fun getItemCount() = items.size

    fun actualizar(nuevos: List<Ingrediente>) {
        items = nuevos
        notifyDataSetChanged()
    }

    // 2.0 -> "2", 2.5 -> "2.5"
    private fun formatear(valor: Double): String =
        if (valor % 1.0 == 0.0) valor.toLong().toString() else valor.toString()
}
