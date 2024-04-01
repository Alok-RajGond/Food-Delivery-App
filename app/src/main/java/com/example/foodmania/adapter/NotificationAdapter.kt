package com.example.foodmania.adapter

 import android.view.LayoutInflater
 import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodmania.databinding.NotificaitonItemBinding

class NotificationAdapter (private var notifiation: ArrayList<String>, private var notificationImage: ArrayList<Int>): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = NotificaitonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }



    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = notifiation.size

    inner class NotificationViewHolder(private val binding: NotificaitonItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                notificationTextView.text = notifiation[position]
                notificaitonImageView.setImageResource(notificationImage[position])
            }
        }

    }
}