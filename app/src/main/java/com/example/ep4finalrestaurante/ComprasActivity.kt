package com.example.ep4finalrestaurante

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ComprasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spIngrediente: Spinner
    private lateinit var etCantidad: EditText
    private lateinit var etCosto: EditText
    private lateinit var tvStockActual: TextView
    private lateinit var tvVacio: TextView
    private lateinit var btnRegistrar: Button
    private lateinit var adapter: CompraAdapter

    private var ingredientes: List<Ingrediente> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compras)
        title = "Compras"

        dbHelper = DatabaseHelper(this)
        spIngrediente = findViewById(R.id.spIngrediente)
        etCantidad = findViewById(R.id.etCantidad)
        etCosto = findViewById(R.id.etCosto)
        tvStockActual = findViewById(R.id.tvStockActual)
        tvVacio = findViewById(R.id.tvVacio)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        adapter = CompraAdapter(emptyList())
        val rv = findViewById<RecyclerView>(R.id.rvCompras)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // Al cambiar de ingrediente se muestra su stock actual
        spIngrediente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, position: Int, id: Long) {
                mostrarStock(position)
            }

            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        btnRegistrar.setOnClickListener { registrar() }

        cargarIngredientes()
        cargarHistorial()
    }

    private fun cargarIngredientes() {
        ingredientes = dbHelper.obtenerIngredientes()

        if (ingredientes.isEmpty()) {
            btnRegistrar.isEnabled = false
            tvStockActual.text = ""
            Toast.makeText(
                this,
                "Primero registre ingredientes en el módulo Ingredientes",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val seleccion = spIngrediente.selectedItemPosition.coerceAtLeast(0)
        spIngrediente.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            ingredientes.map { "${it.nombre} (${it.unidad})" }
        )
        spIngrediente.setSelection(seleccion)
        mostrarStock(seleccion)
    }

    private fun mostrarStock(posicion: Int) {
        val ing = ingredientes.getOrNull(posicion) ?: return
        tvStockActual.text = "Stock actual: ${ing.stockActual.formatoCantidad()} ${ing.unidad}"
    }

    private fun cargarHistorial() {
        val lista = dbHelper.obtenerCompras()
        adapter.actualizar(lista)
        tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun registrar() {
        val ing = ingredientes.getOrNull(spIngrediente.selectedItemPosition) ?: return
        val cantidad = etCantidad.text.toString().toDoubleOrNull()
        val costo = etCosto.text.toString().toDoubleOrNull()

        if (cantidad == null || cantidad <= 0) {
            etCantidad.error = "Ingrese una cantidad mayor que 0"
            return
        }
        if (costo == null || costo < 0) {
            etCosto.error = "Ingrese un costo válido"
            return
        }

        if (dbHelper.registrarCompra(ing.id, cantidad, costo)) {
            Toast.makeText(this, "Compra registrada", Toast.LENGTH_SHORT).show()
            etCantidad.text.clear()
            etCosto.text.clear()
            cargarIngredientes() // refresca el stock mostrado
            cargarHistorial()
        } else {
            Toast.makeText(this, "No se pudo registrar la compra", Toast.LENGTH_LONG).show()
        }
    }
}
