package com.example.ep4finalrestaurante

data class Plato(
    val id: Int = 0,
    val nombre: String,
    val precio: Double
)

// Una línea de la receta: cuánto de un ingrediente usa UN plato
data class RecetaItem(
    val ingredienteId: Int,
    val ingredienteNombre: String,
    val unidad: String,
    val cantidad: Double
)
