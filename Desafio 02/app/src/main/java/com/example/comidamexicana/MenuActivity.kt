package com.example.comidamexicana

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.widget.Toast
import androidx.core.app.NotificationCompat


class MenuActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var llContainer: LinearLayout
    private lateinit var llDescription: LinearLayout
    private lateinit var llSelectedFoods: LinearLayout
    private lateinit var tvDescription: TextView
    private lateinit var selectedFoods: MutableList<Food>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        llContainer = findViewById(R.id.llContainer)
        llDescription = findViewById(R.id.llDescription)
        llSelectedFoods = findViewById(R.id.llSelectedFoods)
        tvDescription = findViewById(R.id.tvDescription)

        database = FirebaseDatabase.getInstance().getReference("comidas")

        selectedFoods = mutableListOf()

        fetchData()

        // Configura el botón de añadir al carrito
        val btnAddToCart = findViewById<Button>(R.id.btnAddToCart)
        btnAddToCart.setOnClickListener {
            addToCart()
        }
    }

    private fun fetchData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                llContainer.removeAllViews()  // Limpiar vistas existentes
                for (dataSnapshot in snapshot.children) {
                    val map = dataSnapshot.value as? Map<String, Any>
                    val nombre = map?.get("nombre") as? String ?: ""

                    // Asegurarnos de que el precio sea Double
                    val precio = (map?.get("precio") as? Number)?.toDouble() ?: 0.0

                    // Creamos el objeto Food sin imagen
                    val food = Food(nombre, precio)
                    addFoodItem(food)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MenuActivity", "DatabaseError: ${error.message}")
            }
        })
    }

    private fun addFoodItem(food: Food) {
        Log.d("MenuActivity", "Adding food item: ${food.nombre}")

        val inflater = LayoutInflater.from(this)
        val itemView = inflater.inflate(R.layout.item_food, llContainer, false)

        val tvNombre = itemView.findViewById<TextView>(R.id.tvNombre)
        val tvPrecio = itemView.findViewById<TextView>(R.id.tvPrecio)

        tvNombre.text = food.nombre
        tvPrecio.text = "${food.precio} MXN"

        itemView.setOnClickListener {
            val isSelected = selectedFoods.contains(food)
            if (isSelected) {
                selectedFoods.remove(food)
                removeSelectedFoodView(food)
            } else {
                selectedFoods.add(food)
                addSelectedFoodView(food)
            }
        }

        llContainer.addView(itemView)
    }

    private fun addSelectedFoodView(food: Food) {
        val inflater = LayoutInflater.from(this)
        val selectedFoodView = inflater.inflate(R.layout.item_food_selected, llSelectedFoods, false)

        val tvSelectedName = selectedFoodView.findViewById<TextView>(R.id.tvSelectedName)
        val tvSelectedPrice = selectedFoodView.findViewById<TextView>(R.id.tvSelectedPrice)
        val btnAddToCart = selectedFoodView.findViewById<Button>(R.id.btnAddToCart)
        val btnViewCart = selectedFoodView.findViewById<Button>(R.id.btnViewCart)

        tvSelectedName.text = food.nombre
        tvSelectedPrice.text = "${food.precio} MXN"

        btnAddToCart.setOnClickListener {
            Toast.makeText(this, "${food.nombre} ha sido añadido al carrito", Toast.LENGTH_SHORT).show()
            addToCart()  // Opcional: si necesitas realizar otras acciones
        }

        btnViewCart.setOnClickListener {
            viewCart()
        }

        llSelectedFoods.addView(selectedFoodView)
    }

    private fun removeSelectedFoodView(food: Food) {
        for (i in 0 until llSelectedFoods.childCount) {
            val view = llSelectedFoods.getChildAt(i)
            val tvSelectedName = view.findViewById<TextView>(R.id.tvSelectedName)
            val tvSelectedPrice = view.findViewById<TextView>(R.id.tvSelectedPrice)

            if (tvSelectedName.text == food.nombre && tvSelectedPrice.text == "${food.precio} MXN") {
                llSelectedFoods.removeViewAt(i)
                break
            }
        }
    }

    private fun displayFoodDetails(food: Food) {
        llDescription.visibility = View.VISIBLE
        tvDescription.text = "${food.nombre}\n${food.precio} MXN"
    }

    private fun addToCart() {
        // Crea un Intent para abrir la CarritoActivity
        val intent = Intent(this, CarritoActivity::class.java)
        intent.putParcelableArrayListExtra("selectedFoods", ArrayList(selectedFoods))

        // Muestra una notificación
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "default_channel")
            .setContentTitle("Carrito")
            .setContentText("Ítems añadidos al carrito")
            .setSmallIcon(R.drawable.ic_cart)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify(1, notification)

        // Inicia la CarritoActivity
        startActivity(intent)
    }


    private fun viewCart() {
        val intent = Intent(this, CarritoActivity::class.java)
        startActivity(intent)
    }

    // Método para obtener los ítems seleccionados
    private fun getSelectedFoods(): List<Food> {
        return selectedFoods
    }


}
