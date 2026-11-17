package com.example.ep4finalrestaurante

data class Compra(
    val id: Int,
    val ingredienteNombre: String,
    val unidad: String,
    val cantidad: Double,
    val costoTotal: Double,
    val fecha: String
)
