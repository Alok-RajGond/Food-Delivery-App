package com.example.foodmania.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodmania.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>, // List of food item names in the cart
    private val cartItemPrice: MutableList<String>, // List of prices for each food item in the cart
    private var cartImage: MutableList<String>, // List of image URIs for each food item in the cart
    private var cartDescription: MutableList<String>,
    private val cartQuantity: MutableList<Int>,
    private var cartIngredient: MutableList<String>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    //initializing firebase authentication to fetch particular user's profile
    private val auth = FirebaseAuth.getInstance()

    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber = cartItems.size

        itemQuantities = IntArray(cartItems.size) { 1 }
        cartItemsReference = database.reference.child("user").child(userId).child("cartItem")

    }

    companion object {
        private var itemQuantities: IntArray = intArrayOf()

        private lateinit var cartItemsReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    //get updated quanitity to cart fragment (to food quantity variable)
    fun getUpdatedItemsQuantities(): MutableList<Int> {

        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }


    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {

                val quantity = itemQuantities[position]
                //id in (fragment_cart.xml).text = localVariable[index]
                cartFoodName.text = cartItems[position]
                cartFoodPrice.text = cartItemPrice[position]

                //load images using glide
                val uriString = cartImage[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(cartFoodImage)

                cartItemQuantity.text = quantity.toString()

                minusItemBtn.setOnClickListener {
                    decreaseQuantity(position)
                }

                plusItemBtn.setOnClickListener {
                    increaseQuantity(position)
                }

                delItemBtn.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }
                }
            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                cartQuantity[position] = itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                cartQuantity[position] = itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            val positionRetrive: Int = position
            getUniqueKeyAtPosition(positionRetrive) { uniqueKey ->
                if (uniqueKey != null) {
                    removeItem(position, uniqueKey)
                }
            }
        }


        private fun removeItem(position: Int, uniqueKey: String) {
            if (uniqueKey != null) {
                cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
                    // Remove item from lists
                    cartItems.removeAt(position)
                    cartImage.removeAt(position)
                    cartItemPrice.removeAt(position)
                    cartDescription.removeAt(position)
                    cartQuantity.removeAt(position)
                    cartIngredient.removeAt(position)
                    Toast.makeText(context, "Item removed from the cart", Toast.LENGTH_SHORT).show()

                    // Update itemQuantities array
                    itemQuantities =
                        itemQuantities.filterIndexed { index, i -> index != position }.toIntArray()

                    // Notify adapter of the removed item
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to remove from the cart", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Log.e("CartAdapter", "Invalid position: $position")
            }
        }


        private fun getUniqueKeyAtPosition(positionRetrive: Int, onComplete: (String?) -> Unit) {
            cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String? = null
                    //loop for snapshot children
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if (index == positionRetrive) {
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }
}

/*
class CartAdapter(
    private val context: Context, // Context of the activity or fragment
    private val cartItems: MutableList<String>, // List of food item names in the cart
    private val cartItemPrice: MutableList<String>, // List of prices for each food item in the cart
    private val cartImage: MutableList<String>, // List of image URIs for each food item in the cart
    private val cartQuantity: MutableList<Int>, // List of quantities for each food item in the cart
    private val cartDescription: MutableList<String>, // List of descriptions for each food item in the cart
    private val cartIngredient: MutableList<String>

) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // Firebase authentication instance
    private val auth = FirebaseAuth.getInstance()

    // Arrays to hold the quantities of each item in the cart and Firebase database reference for cart items
    private lateinit var itemQuantities: IntArray
    private lateinit var cartItemReference: DatabaseReference

    init {
        // Initialize the arrays and database reference when the adapter is created
        val database = FirebaseDatabase.getInstance()
        val userId =
            auth.currentUser?.uid ?: "" // Get current user ID or empty string if not authenticated
        val cartItemNumber = cartItems.size // Get the number of items in the cart

        // Initialize the itemQuantities array with default quantity 1 for each item
        itemQuantities = IntArray(cartItemNumber) { 1 }

        // Get the reference to the "cartItems" node in the Firebase database
        cartItemReference = database.reference.child("user").child(userId).child("cartItems")
    }

    companion object {


        // Static properties for itemQuantities and cartItemReference
        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemReference: DatabaseReference


        fun getUpdatedItemsQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
            itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }
    }

    // Create view holder instances for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        // Inflate the layout for each cart item
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    // Bind data to the view holder
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    // Return the total number of items in the cart
    override fun getItemCount(): Int = cartItems.size



    // View holder class for each cart item
    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind data to the views
        fun bind(position: Int) {
            binding.apply {
                // Get the quantity of the item at the current position
                val quantity = itemQuantities[position]

                // Set the food item name, price, and quantity to their respective views
                cartFoodName.text = cartItems[position]
                cartFoodPrice.text = cartItemPrice[position]
                cartItemQuantity.text = quantity.toString()

                // Load the food item image using Glide library
                val uriString = cartImage[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(CartFoodImage)

                // Set click listeners for buttons
                minusItemBtn.setOnClickListener { decreaseQuantity(position) }
                plusItemBtn.setOnClickListener { increaseQuantity(position) }
                delItemBtn.setOnClickListener { deleteItem(position) }
            }
        }

        // Decrease the quantity of the item at the specified position
        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] >= 1) {
                itemQuantities[position]--
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        // Increase the quantity of the item at the specified position
        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        // Delete the item at the specified position
        private fun deleteItem(position: Int) {
            val positionRetrive = position
            // Get the unique key of the item at the specified position from Firebase
            getUniqueKeyAtPosition(positionRetrive) { uniqueKey ->
                if (uniqueKey != null) {
                    // Remove the item from Firebase database and local lists
                    removeItem(position, uniqueKey)
                }
            }
        }
    }

    // Remove item from Firebase database and local lists
    private fun removeItem(position: Int, uniqueKey: String) {
        if (uniqueKey != null) {
            cartItemReference.child(uniqueKey).removeValue().addOnSuccessListener {
                // Remove item from local lists
                cartItems.removeAt(position)
                cartImage.removeAt(position)
                cartDescription.removeAt(position)
                cartQuantity.removeAt(position)
                cartItemPrice.removeAt(position)
                cartIngredient.removeAt(position)

                // Remove the quantity from the itemQuantities array
                itemQuantities =
                    itemQuantities.filterIndexed { index, i -> index != position }.toIntArray()

                // Notify adapter about the item removal
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, cartItems.size)

                // Show success message
                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                // Show failure message if deletion fails
                Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Get the unique key of the item at the specified position in Firebase database
    private fun getUniqueKeyAtPosition(positionRetrive: Int, onComplete: (String?) -> Unit) {
        cartItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniqueKey: String? = null
                // Loop through the children of the snapshot to find the item at the specified position
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if (index == positionRetrive) {
                        // Get the unique key of the item
                        uniqueKey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                // Call onComplete callback with the unique key
                onComplete(uniqueKey)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }
}
*/