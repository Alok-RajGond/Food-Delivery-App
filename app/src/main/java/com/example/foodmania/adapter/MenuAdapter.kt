package com.example.foodmania.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodmania.DetailsActivity
import com.example.foodmania.databinding.MenuItemBinding
import com.example.foodmania.model.MenuItem

class MenuAdapter(
    private val menuItems: List<MenuItem>, // List of menu items to be displayed
    private val requireContext: Context // Context reference required for starting new activities
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    // Create a new ViewHolder for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    // Bind data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    // Return the total number of items in the data set
    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Initialize item click listener in ViewHolder constructor
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailActivity(position)
                }
            }
        }

        // Open DetailsActivity when an item is clicked
        private fun openDetailActivity(position: Int) {
            val menuItem = menuItems[position]
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                // Pass details of the selected menu item to the DetailsActivity
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemPrice", menuItem.foodPrice)
                putExtra("MenuItemImage", menuItem.foodImage)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredient", menuItem.foodIngredient)
            }
            requireContext.startActivity(intent) // Start the DetailsActivity
        }

        // Bind data to the views in the ViewHolder
        fun bind(position: Int) {
            val menuItem = menuItems[position]
            binding.apply {
                menuFoodName.text = menuItem.foodName // Set food name
                menuPrice.text = menuItem.foodPrice // Set food price
                // Load and display food image using Glide library
                Glide.with(requireContext).load(Uri.parse(menuItem.foodImage)).into(menuImg)
            }
        }
    }
}
