package com.example.ep4finalrestaurante

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnIngredientes).setOnClickListener {
            startActivity(Intent(this, IngredientesActivity::class.java))
        }

        findViewById<Button>(R.id.btnPlatos).setOnClickListener {
            startActivity(Intent(this, PlatosActivity::class.java))
        }

        findViewById<Button>(R.id.btnCompras).setOnClickListener {
            startActivity(Intent(this, ComprasActivity::class.java))
        }

        findViewById<Button>(R.id.btnVentas).setOnClickListener {
            startActivity(Intent(this, VentasActivity::class.java))
        }

        // Reportes se construye en la fase 5
        findViewById<Button>(R.id.btnReportes).setOnClickListener { proximamente() }
    }

    private fun proximamente() {
        Toast.makeText(this, "Módulo en construcción", Toast.LENGTH_SHORT).show()
    }
}
