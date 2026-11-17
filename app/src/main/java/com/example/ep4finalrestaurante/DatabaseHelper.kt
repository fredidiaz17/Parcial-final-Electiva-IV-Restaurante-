package com.example.ep4finalrestaurante

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "RestauranteDB", null, 1) {

    // SQLite trae las llaves foráneas desactivadas: hay que activarlas aquí
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE ingredientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE,
                unidad TEXT NOT NULL,
                stock_actual REAL NOT NULL DEFAULT 0,
                stock_minimo REAL NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE platos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE,
                precio REAL NOT NULL
            )
            """.trimIndent()
        )

        // Tabla puente: qué ingredientes (y cuánto) usa cada plato
        db.execSQL(
            """
            CREATE TABLE receta (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                plato_id INTEGER NOT NULL,
                ingrediente_id INTEGER NOT NULL,
                cantidad REAL NOT NULL,
                UNIQUE (plato_id, ingrediente_id),
                FOREIGN KEY (plato_id) REFERENCES platos(id) ON DELETE CASCADE,
                FOREIGN KEY (ingrediente_id) REFERENCES ingredientes(id)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE compras (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ingrediente_id INTEGER NOT NULL,
                cantidad REAL NOT NULL,
                costo_total REAL NOT NULL,
                fecha TEXT NOT NULL,
                FOREIGN KEY (ingrediente_id) REFERENCES ingredientes(id)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE ventas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha TEXT NOT NULL,
                total REAL NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE detalle_venta (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                venta_id INTEGER NOT NULL,
                plato_id INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                precio_unitario REAL NOT NULL,
                FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
                FOREIGN KEY (plato_id) REFERENCES platos(id)
            )
            """.trimIndent()
        )
    }

    // Solo para desarrollo: borra todo y recrea. Se ejecuta al subir la versión (1 -> 2).
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS detalle_venta")
        db.execSQL("DROP TABLE IF EXISTS ventas")
        db.execSQL("DROP TABLE IF EXISTS compras")
        db.execSQL("DROP TABLE IF EXISTS receta")
        db.execSQL("DROP TABLE IF EXISTS platos")
        db.execSQL("DROP TABLE IF EXISTS ingredientes")
        onCreate(db)
    }

    // =====================================================
    // INGREDIENTES
    // =====================================================

    // CREATE
    fun insertarIngrediente(
        nombre: String,
        unidad: String,
        stockActual: Double,
        stockMinimo: Double
    ): Boolean {
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("unidad", unidad)
            put("stock_actual", stockActual)
            put("stock_minimo", stockMinimo)
        }
        return try {
            writableDatabase.insertOrThrow("ingredientes", null, valores) != -1L
        } catch (e: SQLiteConstraintException) {
            false // nombre repetido
        }
    }

    // READ
    fun obtenerIngredientes(): List<Ingrediente> {
        val lista = mutableListOf<Ingrediente>()
        val cursor = readableDatabase.rawQuery(
            "SELECT id, nombre, unidad, stock_actual, stock_minimo " +
                    "FROM ingredientes ORDER BY nombre",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Ingrediente(
                        id = it.getInt(0),
                        nombre = it.getString(1),
                        unidad = it.getString(2),
                        stockActual = it.getDouble(3),
                        stockMinimo = it.getDouble(4)
                    )
                )
            }
        }
        return lista
    }

    // UPDATE
    fun actualizarIngrediente(ingrediente: Ingrediente): Boolean {
        val valores = ContentValues().apply {
            put("nombre", ingrediente.nombre)
            put("unidad", ingrediente.unidad)
            put("stock_actual", ingrediente.stockActual)
            put("stock_minimo", ingrediente.stockMinimo)
        }
        return try {
            writableDatabase.update(
                "ingredientes",
                valores,
                "id = ?",
                arrayOf(ingrediente.id.toString())
            ) > 0
        } catch (e: SQLiteConstraintException) {
            false // nombre repetido
        }
    }

    // DELETE
    // Falla (devuelve false) si el ingrediente ya se usa en una receta o en una compra
    fun eliminarIngrediente(id: Int): Boolean {
        return try {
            writableDatabase.delete(
                "ingredientes",
                "id = ?",
                arrayOf(id.toString())
            ) > 0
        } catch (e: SQLiteConstraintException) {
            false
        }
    }
}
