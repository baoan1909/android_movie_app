package com.example.android_movie_app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import com.example.android_movie_app.GenreDetailActivity
import com.example.android_movie_app.Movie
import com.example.android_movie_app.MovieDetailActivity
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

            // Xử lý sự kiện click mở GenreDetailActivity
            textView.setOnClickListener {
                val intent = Intent(context, GenreDetailActivity::class.java).apply {
                    putExtra("genreName", genre)
                }
                context.startActivity(intent)
            }

            container.addView(view)
        }
    }

    /** Inflate Top item (Top 10 Today) */
    @UnstableApi
    fun setTopMovies(container: LinearLayout, movies: List<Movie>) {
        container.removeAllViews()
        for ((index, movie) in movies.withIndex()) {
            val view = inflater.inflate(R.layout.item_movie_card_top_10, container, false)

            val numberView = view.findViewById<TextView>(R.id.numberView)
            val cardView = view.findViewById<CardView>(R.id.cardView)
            val imageView = view.findViewById<ImageView>(R.id.imageView)

            // Số thứ tự
            numberView.text = (index + 1).toString()

            // Load poster với URL đầy đủ
            val fullPosterUrl = if (movie.thumbUrl?.startsWith("http") == true) {
                movie.thumbUrl
            } else {
                "https://img.ophim.live/uploads/movies/${movie.thumbUrl}"
            }
            
            Glide.with(context)
                .load(fullPosterUrl)
                .placeholder(R.drawable.gradient_thumb)
                .into(imageView)

            // Xử lý click
            cardView.setOnClickListener {
                val intent = Intent(context, MovieDetailActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                intent.putExtra("movie_name", movie.name)
                intent.putExtra("movie_poster", movie.posterUrl)
                intent.putExtra("movie_thumb", movie.thumbUrl)
                intent.putExtra("movie_rating", movie.rating)
                intent.putExtra("movie_year", movie.year)
                intent.putExtra("movie_content", movie.content)
                context.startActivity(intent)
            }

            container.addView(view)
        }
    }

    /** Inflate Recent Movies (Summer 2025) */
    @UnstableApi
    fun setRecentMovies(container: LinearLayout, movies: List<Movie>) {
        container.removeAllViews()
        for (movie in movies) {
            val view = inflater.inflate(R.layout.item_movie_poster, container, false)
            val cardView = view.findViewById<CardView>(R.id.cardView)
            val imageView = view.findViewById<ImageView>(R.id.imageView)

            // Load poster với URL đầy đủ
            val fullPosterUrl = if (movie.thumbUrl?.startsWith("http") == true) {
                movie.thumbUrl
            } else {
                "https://img.ophim.live/uploads/movies/${movie.thumbUrl}"
            }

            Glide.with(context)
                .load(fullPosterUrl)
                .placeholder(R.drawable.gradient_thumb)
                .into(imageView)

            cardView.setOnClickListener {
                val intent = Intent(context, MovieDetailActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                intent.putExtra("movie_name", movie.name)
                intent.putExtra("movie_poster", movie.posterUrl)
                intent.putExtra("movie_thumb", movie.thumbUrl)
                intent.putExtra("movie_rating", movie.rating)
                intent.putExtra("movie_year", movie.year)
                intent.putExtra("movie_content", movie.content)
                context.startActivity(intent)
            }

            container.addView(view)
        }
    }
}