package com.example.android_movie_app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_movie_app.ContinueWatchingItem
import com.example.android_movie_app.MovieDetailActivity
import com.example.android_movie_app.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class WatchingProgressAdapter(
    private val context: Context,
    private var watchingList: MutableList<ContinueWatchingItem>,
    private val onRemoveClick: (ContinueWatchingItem) -> Unit
) : RecyclerView.Adapter<WatchingProgressAdapter.WatchingViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    class WatchingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardViewWatching: CardView = itemView.findViewById(R.id.cardViewWatching)
        val imgMoviePoster: ImageView = itemView.findViewById(R.id.imgMoviePoster)
        val tvMovieName: TextView = itemView.findViewById(R.id.tvMovieName)
        val tvYear: TextView = itemView.findViewById(R.id.tvYear)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvEpisodeInfo: TextView = itemView.findViewById(R.id.tvEpisodeInfo)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val tvCurrentTime: TextView = itemView.findViewById(R.id.tvCurrentTime)
        val tvTotalTime: TextView = itemView.findViewById(R.id.tvTotalTime)
        val tvLastWatched: TextView = itemView.findViewById(R.id.tvLastWatched)
        val btnContinueWatching: CardView = itemView.findViewById(R.id.btnContinueWatching)
        val btnRemove: CardView = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_watching_movie, parent, false)
        return WatchingViewHolder(view)
    }

    override fun onBindViewHolder(holder: WatchingViewHolder, position: Int) {
        val item = watchingList[position]
        val movie = item.movie
        val progress = item.progress

        // Movie basic info
        holder.tvMovieName.text = movie.name
        holder.tvYear.text = movie.year?.toString() ?: "N/A"
        holder.tvType.text = if (movie.type == "series") "Phim bộ" else "Phim lẻ"

        // Load movie poster
        val fullPosterUrl = if (movie.thumbUrl?.startsWith("http") == true) {
            movie.thumbUrl
        } else {
            "https://img.ophim.live/uploads/movies/${movie.thumbUrl}"
        }

        Glide.with(context)
            .load(fullPosterUrl)
            .placeholder(R.drawable.gradient_thumb)
            .error(R.drawable.gradient_thumb)
            .into(holder.imgMoviePoster)

        // Episode info (for series)
        if (movie.type == "series" && item.episodeNumber != null) {
            holder.tvEpisodeInfo.visibility = View.VISIBLE
            holder.tvEpisodeInfo.text = "Tập ${item.episodeNumber}"
        } else {
            holder.tvEpisodeInfo.visibility = View.GONE
        }

        // Progress calculation
        val progressPercentage = if (progress.totalTime > 0) {
            (progress.currentTime * 100 / progress.totalTime).coerceAtMost(100)
        } else {
            0
        }
        holder.progressBar.progress = progressPercentage

        // Time display
        holder.tvCurrentTime.text = formatTime(progress.currentTime)
        holder.tvTotalTime.text = formatTime(progress.totalTime)

        // Last watched time
        progress.lastWatchedAt?.let { lastWatched ->
            holder.tvLastWatched.text = "Xem gần đây: ${getTimeAgo(lastWatched)}"
        } ?: run {
            holder.tvLastWatched.text = "Chưa xác định"
        }

        // Continue watching button
        holder.btnContinueWatching.setOnClickListener {
            val intent = Intent(context, MovieDetailActivity::class.java).apply {
                putExtra("movie_id", movie.id)
                putExtra("movie_name", movie.name)
                putExtra("movie_poster", movie.posterUrl)
                putExtra("movie_thumb", movie.thumbUrl)
                putExtra("movie_rating", movie.rating)
                putExtra("movie_year", movie.year)
                putExtra("movie_content", movie.content)
                // Add watching progress info
                putExtra("continue_watching", true)
                putExtra("current_time", progress.currentTime)
                putExtra("episode_id", progress.episodeId)
            }
            context.startActivity(intent)
        }

        // Card click also continues watching
        holder.cardViewWatching.setOnClickListener {
            holder.btnContinueWatching.performClick()
        }

        // Remove button
        holder.btnRemove.setOnClickListener {
            onRemoveClick(item)
        }
    }

    override fun getItemCount(): Int = watchingList.size

    fun updateList(newList: List<ContinueWatchingItem>) {
        watchingList.clear()
        watchingList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(item: ContinueWatchingItem) {
        val position = watchingList.indexOf(item)
        if (position != -1) {
            watchingList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, remainingSeconds)
        } else {
            String.format("%d:%02d", minutes, remainingSeconds)
        }
    }

    private fun getTimeAgo(date: Date): String {
        val now = Date()
        val diffInMillis = now.time - date.time
        
        return when {
            diffInMillis < TimeUnit.MINUTES.toMillis(1) -> "Vừa xong"
            diffInMillis < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
                "$minutes phút trước"
            }
            diffInMillis < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
                "$hours giờ trước"
            }
            diffInMillis < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                "$days ngày trước"
            }
            else -> {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
            }
        }
    }
}
