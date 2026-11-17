package com.example.ep4finalrestaurante

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class IngredientesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: IngredienteAdapter
    private lateinit var tvVacio: TextView

    private val unidades = listOf("und", "g", "kg", "ml", "l")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredientes)
        title = "Ingredientes"

        dbHelper = DatabaseHelper(this)
        tvVacio = findViewById(R.id.tvVacio)

        adapter = IngredienteAdapter(emptyList()) { ingrediente ->
            mostrarDialogo(ingrediente) // tocar un ítem = editar
        }

        val rv = findViewById<RecyclerView>(R.id.rvIngredientes)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAgregar).setOnClickListener {
            mostrarDialogo(null) // botón + = nuevo
        }

        cargarLista()
    }

    private fun cargarLista() {
        val lista = dbHelper.obtenerIngredientes()
        adapter.actualizar(lista)
        tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
    }

    // Un solo diálogo para crear (ingrediente = null) y para editar
    private fun mostrarDialogo(ingrediente: Ingrediente?) {
        val vista = LayoutInflater.from(this).inflate(R.layout.dialog_ingrediente, null)
        val etNombre = vista.findViewById<EditText>(R.id.etNombre)
        val spUnidad = vista.findViewById<Spinner>(R.id.spUnidad)
        val etStock = vista.findViewById<EditText>(R.id.etStock)
        val etStockMinimo = vista.findViewById<EditText>(R.id.etStockMinimo)

        spUnidad.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, unidades
        )

        // Si se está editando, precargar los datos
        if (ingrediente != null) {
            etNombre.setText(ingrediente.nombre)
            spUnidad.setSelection(unidades.indexOf(ingrediente.unidad).coerceAtLeast(0))
            etStock.setText(ingrediente.stockActual.toString())
            etStockMinimo.setText(ingrediente.stockMinimo.toString())
        }

        val builder = AlertDialog.Builder(this)
            .setTitle(if (ingrediente == null) "Nuevo ingrediente" else "Editar ingrediente")
            .setView(vista)
            .setPositiveButton("Guardar", null) // se define abajo para controlar el cierre
            .setNegativeButton("Cancelar", null)

        if (ingrediente != null) {
            builder.setNeutralButton("Eliminar") { _, _ -> confirmarEliminar(ingrediente) }
        }

        val dialogo = builder.create()
        dialogo.show()

        // Así el diálogo no se cierra si la validación falla
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val unidad = spUnidad.selectedItem.toString()
            val stock = etStock.text.toString().toDoubleOrNull()
            val stockMinimo = etStockMinimo.text.toString().toDoubleOrNull() ?: 0.0

            if (nombre.isEmpty()) {
                etNombre.error = "Ingrese el nombre"
                return@setOnClickListener
            }
            if (stock == null || stock < 0) {
                etStock.error = "Ingrese un stock válido"
                return@setOnClickListener
            }
            if (stockMinimo < 0) {
                etStockMinimo.error = "No puede ser negativo"
                return@setOnClickListener
            }

            val ok = if (ingrediente == null) {
                dbHelper.insertarIngrediente(nombre, unidad, stock, stockMinimo)
            } else {
                dbHelper.actualizarIngrediente(
                    ingrediente.copy(
                        nombre = nombre,
                        unidad = unidad,
                        stockActual = stock,
                        stockMinimo = stockMinimo
                    )
                )
            }

            if (ok) {
                Toast.makeText(this, "Ingrediente guardado", Toast.LENGTH_SHORT).show()
                dialogo.dismiss()
                cargarLista()
            } else {
                etNombre.error = "Ya existe un ingrediente con ese nombre"
            }
        }
    }

    private fun confirmarEliminar(ingrediente: Ingrediente) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar ingrediente")
            .setMessage("¿Eliminar \"${ingrediente.nombre}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                if (dbHelper.eliminarIngrediente(ingrediente.id)) {
                    Toast.makeText(this, "Ingrediente eliminado", Toast.LENGTH_SHORT).show()
                    cargarLista()
                } else {
                    Toast.makeText(
                        this,
                        "No se puede eliminar: el ingrediente se usa en recetas o compras",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
