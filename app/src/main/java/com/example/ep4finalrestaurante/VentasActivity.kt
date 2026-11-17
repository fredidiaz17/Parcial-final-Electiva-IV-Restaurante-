package com.example.registroclientes

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VentasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spPlato: Spinner
    private lateinit var etCantidad: EditText
    private lateinit var tvTotal: TextView
    private lateinit var tvVacio: TextView
    private lateinit var btnAgregar: Button
    private lateinit var btnRegistrar: Button
    private lateinit var adapter: CarritoAdapter

    private var platos: List<Plato> = emptyList()

    // El carrito vive en memoria hasta que se presiona REGISTRAR VENTA
    private val carrito = mutableListOf<ItemVenta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventas)
        title = "Ventas"

        dbHelper = DatabaseHelper(this)
        spPlato = findViewById(R.id.spPlato)
        etCantidad = findViewById(R.id.etCantidad)
        tvTotal = findViewById(R.id.tvTotal)
        tvVacio = findViewById(R.id.tvVacio)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        adapter = CarritoAdapter(carrito) { posicion -> confirmarQuitar(posicion) }
        val rv = findViewById<RecyclerView>(R.id.rvCarrito)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        cargarPlatos()

        btnAgregar.setOnClickListener { agregarAlCarrito() }
        btnRegistrar.setOnClickListener { registrarVenta() }

        actualizarVista()
    }

    private fun cargarPlatos() {
        platos = dbHelper.obtenerPlatos()

        if (platos.isEmpty()) {
            btnAgregar.isEnabled = false
            btnRegistrar.isEnabled = false
            Toast.makeText(
                this,
                "Primero registre platos en el módulo Platos",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        spPlato.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            platos.map { "${it.nombre}  ($${it.precio.formatoDinero()})" }
        )
    }

    private fun agregarAlCarrito() {
        val plato = platos.getOrNull(spPlato.selectedItemPosition) ?: return
        val cantidad = etCantidad.text.toString().toIntOrNull()

        if (cantidad == null || cantidad <= 0) {
            etCantidad.error = "Ingrese una cantidad mayor que 0"
            return
        }

        // Si el plato ya está en el carrito, se suma la cantidad
        val indice = carrito.indexOfFirst { it.platoId == plato.id }
        if (indice >= 0) {
            val actual = carrito[indice]
            carrito[indice] = actual.copy(cantidad = actual.cantidad + cantidad)
        } else {
            carrito.add(ItemVenta(plato.id, plato.nombre, plato.precio, cantidad))
        }

        etCantidad.setText("1")
        actualizarVista()
    }

    private fun confirmarQuitar(posicion: Int) {
        if (posicion < 0 || posicion >= carrito.size) return
        AlertDialog.Builder(this)
            .setTitle("Quitar del carrito")
            .setMessage("¿Quitar \"${carrito[posicion].nombre}\"?")
            .setPositiveButton("Quitar") { _, _ ->
                carrito.removeAt(posicion)
                actualizarVista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun registrarVenta() {
        if (carrito.isEmpty()) {
            Toast.makeText(this, "Agregue al menos un plato al carrito", Toast.LENGTH_SHORT).show()
            return
        }

        val total = carrito.sumOf { it.subtotal }
        val error = dbHelper.registrarVenta(carrito.toList())

        if (error == null) {
            Toast.makeText(
                this,
                "Venta registrada. Total: $${total.formatoDinero()}",
                Toast.LENGTH_LONG
            ).show()
            carrito.clear()
            actualizarVista()
        } else {
            AlertDialog.Builder(this)
                .setTitle("No se pudo registrar la venta")
                .setMessage(error)
                .setPositiveButton("Entendido", null)
                .show()
        }
    }

    private fun actualizarVista() {
        adapter.refrescar()
        tvVacio.visibility = if (carrito.isEmpty()) View.VISIBLE else View.GONE
        tvTotal.text = "Total: $${carrito.sumOf { it.subtotal }.formatoDinero()}"
    }
}
