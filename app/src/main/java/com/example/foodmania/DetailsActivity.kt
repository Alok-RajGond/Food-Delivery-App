package com.example.foodmania

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.foodmania.databinding.ActivityDetailsBinding
import com.example.foodmania.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    // Variables to hold information about the food item
    private var foodName: String? = null
    private var foodPrice: String? = null
    private var foodDescription: String? = null
    private var foodImage: String? = null
    private var foodIngredient: String? = null
    private lateinit var auth: FirebaseAuth

    // This method is called when the activity is starting
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using View Binding
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // Retrieve data passed from the previous activity via Intent
        foodName = intent.getStringExtra("MenuItemName")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodIngredient = intent.getStringExtra("MenuItemIngredient")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodImage = intent.getStringExtra("MenuItemImage")

        // Set the retrieved data to corresponding views using View Binding
        with(binding) {
            detailFoodName.text = foodName
            detailDescription.text = foodDescription
            detailIngredient.text = foodIngredient

            // Load food image using Glide library from the URL provided
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(detailFoodImage)
        }

        // Set a click listener for the back button to finish the activity and return to the previous one
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.detailAddToCartBtn.setOnClickListener {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid ?: ""

        //create a cartItem object
        val cartItem = CartItems(
            foodName.toString(),
            foodPrice.toString(),
            foodImage.toString(),
            foodDescription.toString(),
            1
        )

        //save data to cart item to firebase
        database.child("user").child(userId).child("cartItem").push().setValue(cartItem)
            .addOnCompleteListener { task ->

                Toast.makeText(this, "Item is successfully added to cart", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener { task ->
                Toast.makeText(this, "Item is not added to cart", Toast.LENGTH_SHORT).show()
            }
    }
}
