package com.example.android_movie_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_movie_app.Movie
import com.example.android_movie_app.R

class GenreRecyclerAdapter(
    private val movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<GenreRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bannerImage: ImageView = itemView.findViewById(R.id.bannerImage)
        val txtMovieName: TextView = itemView.findViewById(R.id.txtMovieName)

        fun bind(movie: Movie) {
            txtMovieName.text = movie.name

            val fullPosterUrl = if (movie.posterUrl?.startsWith("http") == true) {
                movie.posterUrl
            } else {
                "https://img.ophim.live/uploads/movies/${movie.posterUrl}"
            }

            Glide.with(itemView.context)
                .load(fullPosterUrl)
                .placeholder(R.drawable.gradient_thumb)
                .error(R.drawable.gradient_thumb)
                .into(bannerImage)

            itemView.setOnClickListener { onItemClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_genre_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size
}