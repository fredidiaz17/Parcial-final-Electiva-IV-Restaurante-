package com.example.ep4finalrestaurante

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlatosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: PlatoAdapter
    private lateinit var tvVacio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platos)
        title = "Platos"

        dbHelper = DatabaseHelper(this)
        tvVacio = findViewById(R.id.tvVacio)

        adapter = PlatoAdapter(emptyList()) { plato ->
            val intent = Intent(this, PlatoFormActivity::class.java).apply {
                putExtra(PlatoFormActivity.EXTRA_ID, plato.id)
                putExtra(PlatoFormActivity.EXTRA_NOMBRE, plato.nombre)
                putExtra(PlatoFormActivity.EXTRA_PRECIO, plato.precio)
            }
            startActivity(intent)
        }

        val rv = findViewById<RecyclerView>(R.id.rvPlatos)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAgregar).setOnClickListener {
            startActivity(Intent(this, PlatoFormActivity::class.java))
        }
    }

    // Al volver del formulario, la lista se recarga con los cambios
    override fun onResume() {
        super.onResume()
        val lista = dbHelper.obtenerPlatos()
        adapter.actualizar(lista)
        tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
    }
}
