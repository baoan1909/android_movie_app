package com.example.android_movie_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_movie_app.CommentWithUser
import com.example.android_movie_app.R
import com.example.android_movie_app.data.CommentDAO
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CommentAdapter(
    private val context: Context,
    private var comments: List<CommentWithUser>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val commentDAO = CommentDAO(context)

    private var onReplyClickListener: ((CommentWithUser) -> Unit)? = null
    fun setOnReplyClickListener(listener: (CommentWithUser) -> Unit) {
        onReplyClickListener = listener
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewAvatar: ImageView = itemView.findViewById(R.id.imageViewAvatar)
        val textViewUsername: TextView = itemView.findViewById(R.id.textViewUsername)
        val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)
        val textViewCommentBody: TextView = itemView.findViewById(R.id.textViewCommentBody)
        val textViewToggleMore: TextView = itemView.findViewById(R.id.textViewToggleMore)
        val buttonReply: TextView = itemView.findViewById(R.id.buttonReply)
        val textViewViewReplies: TextView = itemView.findViewById(R.id.textViewViewReplies)
        val recyclerViewReplies: RecyclerView = itemView.findViewById(R.id.recyclerViewReplies)

        init {
            recyclerViewReplies.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        // Avatar
        Glide.with(context)
            .load(comment.avatarPath)
            .placeholder(R.drawable.ic_account_circle)
            .into(holder.imageViewAvatar)

        // Username
        holder.textViewUsername.text = comment.username ?: "User#${comment.userId}"

        // Comment content
        holder.textViewCommentBody.text = comment.content

        // Xem thêm / Thu gọn
        holder.textViewCommentBody.post {
            val lineCount = holder.textViewCommentBody.lineCount
            if (lineCount > 3) {
                holder.textViewToggleMore.visibility = View.VISIBLE
                if (comment.isExpanded) {
                    holder.textViewCommentBody.maxLines = Int.MAX_VALUE
                    holder.textViewToggleMore.text = "Thu gọn"
                } else {
                    holder.textViewCommentBody.maxLines = 3
                    holder.textViewToggleMore.text = "Xem thêm"
                }

                holder.textViewToggleMore.setOnClickListener {
                    comment.isExpanded = !comment.isExpanded
                    notifyItemChanged(position)
                }
            } else {
                holder.textViewToggleMore.visibility = View.GONE
            }
        }

        // Timestamp
        holder.textViewTimestamp.text = "• ${formatTimeAgo(comment.createdAt)}"

        // Reply click
        holder.buttonReply.setOnClickListener {
            onReplyClickListener?.invoke(comment)
        }

        // Nested replies
        val replies = comment.replies ?: emptyList()
        if (replies.isNotEmpty()) {
            // Luôn hiển thị nút "Xem/Ẩn phản hồi" nếu có replies
            holder.textViewViewReplies.visibility = View.VISIBLE

            // Cập nhật text và visibility của RecyclerView con dựa trên trạng thái
            if (comment.isRepliesVisible) {
                holder.textViewViewReplies.text = "Ẩn phản hồi"
                holder.recyclerViewReplies.visibility = View.VISIBLE

                // Cập nhật hoặc tạo mới adapter
                // Cách này hiệu quả hơn là luôn tạo mới
                val replyAdapter = holder.recyclerViewReplies.adapter as? ReplyAdapter
                if (replyAdapter == null) {
                    holder.recyclerViewReplies.adapter = ReplyAdapter(context, replies)
                } else {
                    replyAdapter.updateData(replies) // Giả sử bạn có hàm updateData trong ReplyAdapter
                }

            } else {
                holder.textViewViewReplies.text = "Xem thêm ${replies.size} phản hồi"
                holder.recyclerViewReplies.visibility = View.GONE
            }

            // Listener để bật/tắt
            holder.textViewViewReplies.setOnClickListener {
                comment.isRepliesVisible = !comment.isRepliesVisible
                notifyItemChanged(position)
            }
        } else {
            // Nếu không có replies, ẩn tất cả
            holder.textViewViewReplies.visibility = View.GONE
            holder.recyclerViewReplies.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = comments.size

    fun updateData(newComments: List<CommentWithUser>) {
        comments = newComments
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
            else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        }
    }
}
