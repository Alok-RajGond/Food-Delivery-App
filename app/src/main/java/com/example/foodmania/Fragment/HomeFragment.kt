package com.example.foodmania.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodmania.R
import com.example.foodmania.adapter.MenuAdapter
import com.example.foodmania.databinding.FragmentHomeBinding
import com.example.foodmania.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Set click listener for "View All Menu" button
        binding.viewAllMenu.setOnClickListener{
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

        // Retrieve and display popular menu items
        retriveAndDisplayPopularMenuItem()

        return binding.root // Return the root view of the fragment
    }

    // Retrieve and display popular menu items from Firebase database
    private fun retriveAndDisplayPopularMenuItem() {
        // Get reference of the Firebase database
        database = FirebaseDatabase.getInstance()
        val foodRef = database.reference.child("menu")
        menuItems = mutableListOf()

        // Retrieve menu items from the database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                // Display random popular items
                randomPopularItems()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }

    // Display a random subset of popular menu items
    private fun randomPopularItems() {
        val index = menuItems.indices.toList().shuffled() // Create a shuffled list of indices
        val numItemToShow = 4 // Number of popular items to display
        val subsetMenuItems = index.take(numItemToShow).map { menuItems[it] } // Take a subset of menuItems
        setPopularItemsAdapter(subsetMenuItems) // Set up RecyclerView adapter
    }

    // Set up RecyclerView adapter for popular menu items
    private fun setPopularItemsAdapter(subsetMenuItem: List<MenuItem>) {
        val adapter = MenuAdapter(subsetMenuItem, requireContext()) // Create adapter instance
        binding.popularRecyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager
        binding.popularRecyclerView.adapter = adapter // Set adapter to RecyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up image slider with a list of images
        val imageList = ArrayList<SlideModel>().apply {
            add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
            add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
            add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))
            add(SlideModel(R.drawable.banner4, ScaleTypes.FIT))
            add(SlideModel(R.drawable.banner5, ScaleTypes.FIT))
            add(SlideModel(R.drawable.banner6, ScaleTypes.FIT))
            add(SlideModel(R.drawable.banner7, ScaleTypes.FIT))
        }

        // Initialize and configure the image slider
        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList) // Set the list of images
        imageSlider.setImageList(imageList, ScaleTypes.FIT) // Set scale type for images

        // Set item click listener for the image slider
        imageSlider.setItemClickListener(object: ItemClickListener {
            override fun doubleClick(position: Int) {
                // Handle double click if needed
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
