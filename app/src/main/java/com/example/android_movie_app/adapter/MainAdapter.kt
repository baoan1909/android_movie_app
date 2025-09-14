package com.example.android_movie_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.android_movie_app.Movie
import com.example.android_movie_app.R


class MainAdapter(private val context: Context) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    /** Inflate Genre item và add vào genreContainer */
    fun setGenres(container: LinearLayout, genres: List<String>) {
        container.removeAllViews()
        for (genre in genres) {
            val view = inflater.inflate(R.layout.item_genre_tag, container, false)
            val textView = view.findViewById<TextView>(R.id.textViewGenre)
            textView.text = genre
            container.addView(view)
        }
    }

    /** Inflate Top item (Top 10 Today) */
    fun setTopMovies(container: LinearLayout, movies: List<Movie>) {
        container.removeAllViews()
        for ((index, movie) in movies.withIndex()) {
            val view = inflater.inflate(R.layout.item_movie_card_top_10, container, false)

            val numberView = view.findViewById<TextView>(R.id.numberView)
            val cardView = view.findViewById<CardView>(R.id.cardView)
            val imageView = view.findViewById<ImageView>(R.id.imageView)

            // Số thứ tự
            numberView.text = (index + 1).toString()

            // Load poster
            Glide.with(context)
                .load(movie.posterUrl)
                .placeholder(R.drawable.anime_1)
                .into(imageView)

            // Xử lý click
            cardView.setOnClickListener {
                // TODO: mở chi tiết phim
            }

            container.addView(view)
        }
    }

    /** Inflate Poster item (Summer 2025) */
    fun setPosters(container: LinearLayout, posters: List<String>) {
        container.removeAllViews()
        for (posterUrl in posters) {
            val view = inflater.inflate(R.layout.item_movie_poster, container, false)
            val cardView = view.findViewById<CardView>(R.id.cardView)
            val imageView = view.findViewById<ImageView>(R.id.imageView)

            Glide.with(context)
                .load(posterUrl)
                .placeholder(R.drawable.anime_5)
                .into(imageView)

            cardView.setOnClickListener {
                // TODO: mở chi tiết phim
            }

            container.addView(view)
        }
    }
}