package com.example.ep4finalrestaurante

import android.os.Bundle
import android.view.LayoutInflater
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

class PlatoFormActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "plato_id"
        const val EXTRA_NOMBRE = "plato_nombre"
        const val EXTRA_PRECIO = "plato_precio"
    }

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etNombre: EditText
    private lateinit var etPrecio: EditText
    private lateinit var tvSinReceta: TextView
    private lateinit var adapter: RecetaAdapter

    // La receta vive en memoria hasta que se presiona GUARDAR
    private val receta = mutableListOf<RecetaItem>()
    private var platoId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plato_form)

        dbHelper = DatabaseHelper(this)
        etNombre = findViewById(R.id.etNombre)
        etPrecio = findViewById(R.id.etPrecio)
        tvSinReceta = findViewById(R.id.tvSinReceta)
        val btnEliminar = findViewById<Button>(R.id.btnEliminar)

        platoId = intent.getIntExtra(EXTRA_ID, 0)

        if (platoId == 0) {
            title = "Nuevo plato"
        } else {
            title = "Editar plato"
            etNombre.setText(intent.getStringExtra(EXTRA_NOMBRE))
            val precio = intent.getDoubleExtra(EXTRA_PRECIO, 0.0)
            etPrecio.setText(if (precio % 1.0 == 0.0) precio.toLong().toString() else precio.toString())
            receta.addAll(dbHelper.obtenerReceta(platoId))
            btnEliminar.visibility = View.VISIBLE
        }

        adapter = RecetaAdapter(receta) { posicion -> mostrarDialogoReceta(posicion) }
        val rv = findViewById<RecyclerView>(R.id.rvReceta)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
        actualizarVistaReceta()

        findViewById<Button>(R.id.btnAgregarIngrediente).setOnClickListener {
            mostrarDialogoReceta(null)
        }
        findViewById<Button>(R.id.btnGuardar).setOnClickListener { guardar() }
        btnEliminar.setOnClickListener { confirmarEliminar() }
    }

    private fun actualizarVistaReceta() {
        adapter.refrescar()
        tvSinReceta.visibility = if (receta.isEmpty()) View.VISIBLE else View.GONE
    }

    // posicion = null -> agregar ingrediente; posicion != null -> editar o quitar ese ingrediente
    private fun mostrarDialogoReceta(posicion: Int?) {
        val ingredientes = dbHelper.obtenerIngredientes()
        val editando = posicion != null
        val actual = posicion?.let { receta[it] }

        // Al agregar solo se ofrecen los ingredientes que aún no están en la receta
        val disponibles = if (actual != null) {
            ingredientes.filter { it.id == actual.ingredienteId }
        } else {
            ingredientes.filter { ing -> receta.none { it.ingredienteId == ing.id } }
        }

        if (disponibles.isEmpty()) {
            val mensaje = if (ingredientes.isEmpty())
                "Primero registre ingredientes en el módulo Ingredientes"
            else
                "Ya agregó todos los ingredientes disponibles"
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
            return
        }

        val vista = LayoutInflater.from(this).inflate(R.layout.dialog_receta, null)
        val spIngrediente = vista.findViewById<Spinner>(R.id.spIngrediente)
        val etCantidad = vista.findViewById<EditText>(R.id.etCantidad)

        spIngrediente.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            disponibles.map { "${it.nombre} (${it.unidad})" }
        )

        if (actual != null) {
            spIngrediente.isEnabled = false // al editar solo cambia la cantidad
            etCantidad.setText(actual.cantidad.toString())
        }

        val builder = AlertDialog.Builder(this)
            .setTitle(if (editando) "Editar ingrediente" else "Agregar ingrediente")
            .setView(vista)
            .setPositiveButton("Aceptar", null)
            .setNegativeButton("Cancelar", null)

        if (posicion != null) {
            builder.setNeutralButton("Quitar") { _, _ ->
                receta.removeAt(posicion)
                actualizarVistaReceta()
            }
        }

        val dialogo = builder.create()
        dialogo.show()

        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val cantidad = etCantidad.text.toString().toDoubleOrNull()
            if (cantidad == null || cantidad <= 0) {
                etCantidad.error = "Ingrese una cantidad mayor que 0"
                return@setOnClickListener
            }

            val ing = disponibles[spIngrediente.selectedItemPosition]
            val item = RecetaItem(ing.id, ing.nombre, ing.unidad, cantidad)

            if (posicion != null) receta[posicion] = item else receta.add(item)
            actualizarVistaReceta()
            dialogo.dismiss()
        }
    }

    private fun guardar() {
        val nombre = etNombre.text.toString().trim()
        val precio = etPrecio.text.toString().toDoubleOrNull()

        if (nombre.isEmpty()) {
            etNombre.error = "Ingrese el nombre"
            return
        }
        if (precio == null || precio <= 0) {
            etPrecio.error = "Ingrese un precio válido"
            return
        }
        if (receta.isEmpty()) {
            Toast.makeText(this, "Agregue al menos un ingrediente a la receta", Toast.LENGTH_SHORT).show()
            return
        }

        if (dbHelper.guardarPlato(platoId, nombre, precio, receta)) {
            Toast.makeText(this, "Plato guardado", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            etNombre.error = "Ya existe un plato con ese nombre"
        }
    }

    private fun confirmarEliminar() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar plato")
            .setMessage("¿Eliminar este plato y su receta?")
            .setPositiveButton("Eliminar") { _, _ ->
                if (dbHelper.eliminarPlato(platoId)) {
                    Toast.makeText(this, "Plato eliminado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "No se puede eliminar: el plato tiene ventas registradas",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
