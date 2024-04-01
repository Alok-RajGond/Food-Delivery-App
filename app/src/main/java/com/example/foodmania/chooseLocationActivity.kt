package com.example.foodmania

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.foodmania.databinding.ActivityChooseLocationBinding

class chooseLocationActivity : AppCompatActivity() {
    private val binding: ActivityChooseLocationBinding by lazy {
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val locationList = arrayOf("Patna", "Gaya", "Motihari", "Siwan", "MujaffarPur", "Chhapra", "Sitamadhi")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        val autoCompleteTextView = binding.listOfLoc
        autoCompleteTextView.setAdapter(adapter)


    }
}