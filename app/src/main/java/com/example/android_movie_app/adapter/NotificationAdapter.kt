package com.example.android_movie_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_movie_app.Notifications
import com.example.android_movie_app.R
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private val list: List<Notifications>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgNotification: ImageView = itemView.findViewById(R.id.imgNotification)
        val tvTitle: TextView = itemView.findViewById(R.id.tvNotificationTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvNotificationContent)
        val tvTime: TextView = itemView.findViewById(R.id.tvNotificationTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = list[position]

        // icon mặc định (nếu bạn muốn có thể lưu loại notification trong DB để thay đổi icon)
        holder.imgNotification.setImageResource(R.drawable.ic_infor)

        holder.tvTitle.text = item.title
        holder.tvContent.text = item.content

        // format thời gian nếu có createdAt
        item.createdAt?.let {
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            holder.tvTime.text = formatter.format(it)
        } ?: run {
            holder.tvTime.text = ""
        }
    }

    override fun getItemCount(): Int = list.size
}
