package com.example.android_movie_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.Movie
import com.example.android_movie_app.R
import com.example.android_movie_app.dao.UserDAO
import com.example.android_movie_app.dao.UserSessionDAO

class FavoriteAdapter(
    private val context: Context,
    private var favoriteMovies: List<Movie>,
    private val onItemClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(R.id.iv_movie_poster)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_movie_title)
        val tvInfo: TextView = itemView.findViewById(R.id.tv_movie_info)
        val ivFavorite: ImageView = itemView.findViewById(R.id.iv_favorite_icon)

        fun bind(movie: Movie) {
            // Load poster (xử lý link rỗng hoặc thiếu http)
            val posterUrl = if (movie.thumbUrl?.startsWith("http") == true) {
                movie.thumbUrl
            } else {
                "https://img.ophim.live/uploads/movies/${movie.thumbUrl}"
            }

            Glide.with(context)
                .load(posterUrl)
                .placeholder(R.drawable.gradient_thumb)
                .error(R.drawable.gradient_thumb)
                .into(ivPoster)

            // Tên phim
            tvTitle.text = movie.name

            // Thông tin phụ: năm + type
            val typeLabel = if (movie.type == "series") "Series" else "Single"
            tvInfo.text = "${movie.year ?: "N/A"} • $typeLabel"

            // Icon favorite (đã yêu thích thì hiện đỏ)
            ivFavorite.setImageResource(R.drawable.ic_favorite)

            // Bắt sự kiện click item
            itemView.setOnClickListener { onItemClick(movie) }

            // Bắt sự kiện bỏ yêu thích
            ivFavorite.setOnClickListener { onFavoriteClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_favorite_movie, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteMovies[position])
    }

    override fun getItemCount(): Int = favoriteMovies.size

    fun updateData(newList: List<Movie>) {
        favoriteMovies = newList
        notifyDataSetChanged()
    }
}