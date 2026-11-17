package com.example.registroclientes

import java.util.Locale

// 2.0 -> "2", 2.5 -> "2.5"
fun Double.formatoCantidad(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()

// 15000.0 -> "15,000" (o "15.000", según el idioma del dispositivo)
fun Double.formatoDinero(): String =
    if (this % 1.0 == 0.0) String.format(Locale.getDefault(), "%,.0f", this)
    else String.format(Locale.getDefault(), "%,.2f", this)
