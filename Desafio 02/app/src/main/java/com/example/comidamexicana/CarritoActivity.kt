package com.example.comidamexicana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CarritoActivity : AppCompatActivity() {

    private lateinit var llCartContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        llCartContainer = findViewById(R.id.llCartContainer)

        // Obtener los ítems del carrito desde el Intent
        val selectedFoods = intent.getParcelableArrayListExtra<Food>("selectedFoods") ?: arrayListOf()
        for (food in selectedFoods) {
            addFoodItemToCart(food)
        }
    }

    private fun addFoodItemToCart(food: Food) {
        val inflater = LayoutInflater.from(this)
        val itemView = inflater.inflate(R.layout.item_food_selected, llCartContainer, false)

        val tvSelectedName = itemView.findViewById<TextView>(R.id.tvSelectedName)
        val tvSelectedPrice = itemView.findViewById<TextView>(R.id.tvSelectedPrice)

        tvSelectedName.text = food.nombre
        tvSelectedPrice.text = "${food.precio} MXN"

        llCartContainer.addView(itemView)
    }
}

