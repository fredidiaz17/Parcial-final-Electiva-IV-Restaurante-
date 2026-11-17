package com.example.registroclientes

// Una línea del carrito de ventas
data class ItemVenta(
    val platoId: Int,
    val nombre: String,
    val precio: Double,
    val cantidad: Int
) {
    val subtotal: Double get() = precio * cantidad
}
