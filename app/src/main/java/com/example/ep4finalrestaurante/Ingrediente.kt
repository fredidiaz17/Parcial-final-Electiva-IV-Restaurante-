package com.example.ep4finalrestaurante

data class Ingrediente(
    val id: Int = 0,
    val nombre: String,
    val unidad: String,
    val stockActual: Double,
    val stockMinimo: Double
)
