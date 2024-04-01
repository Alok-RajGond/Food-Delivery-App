package com.example.foodmania

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodmania.adapter.NotificationAdapter
import com.example.foodmania.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class Notification_Bottom_Fragment : BottomSheetDialogFragment(){

    private lateinit var binding: FragmentNotificationBottomBinding
    // TODO: Rename and change types of parameters
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentNotificationBottomBinding.inflate(layoutInflater, container, false)

        val notifications = listOf("Order has been canceled, Successfully", "Order is ready to Deliver", "Congratulations, Your order is places")
        val notificationImages = listOf(R.drawable.icecream, R.drawable.icecream, R.drawable.icecream)

        val adapter = NotificationAdapter(ArrayList(notifications), ArrayList(notificationImages))
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter

        return binding.root
    }

    companion object {

    }
}