package com.example.android_movie_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_movie_app.CommentWithUser
import com.example.android_movie_app.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ReplyAdapter(
    private val context: Context,
    private var replies: List<CommentWithUser>
) : RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {

    inner class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewAvatar: ImageView = itemView.findViewById(R.id.imageViewAvatar)
        val textViewUsername: TextView = itemView.findViewById(R.id.textViewUsername)
        val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)
        val textViewCommentBody: TextView = itemView.findViewById(R.id.textViewCommentBody)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_reply, parent, false)
        return ReplyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val reply = replies[position]

        // Avatar (Glide)
        Glide.with(context)
            .load(reply.avatarUrl) // link avatar lấy từ DB hoặc API
            .placeholder(R.drawable.ic_account_circle) // khi đang load
            .error(R.drawable.ic_account_circle)       // khi lỗi
            .into(holder.imageViewAvatar)

        // Username
        holder.textViewUsername.text = reply.username ?: "User#${reply.userId}"

        // Nội dung
        holder.textViewCommentBody.text = reply.content

        // Thời gian
        holder.textViewTimestamp.text = "• ${formatTimeAgo(reply.createdAt)}"
    }

    override fun getItemCount(): Int = replies.size

    fun updateData(newReplies: List<CommentWithUser>) {
        replies = newReplies
        notifyDataSetChanged()
    }

    private fun formatTimeAgo(date: Date?): String {
        if (date == null) return ""
        val now = Date()
        val diff = now.time - date.time

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            minutes < 1 -> "Vừa xong"
            minutes < 60 -> "$minutes phút trước"
            hours < 24 -> "$hours giờ trước"
            days == 1L -> "Hôm qua"
            days < 7 -> "$days ngày trước"
            else -> {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.format(date)
            }
        }
    }
}
