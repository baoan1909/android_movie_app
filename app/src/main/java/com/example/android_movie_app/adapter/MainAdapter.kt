package com.example.android_movie_app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_movie_app.GenreDetailActivity
import com.example.android_movie_app.Movie
import com.example.android_movie_app.MovieBanner
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
                .placeholder(R.drawable.anime_1)
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
                .placeholder(R.drawable.anime_5)
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

class BannerSliderAdapter(
    private val context: Context,
    private val movies: List<MovieBanner>
) : RecyclerView.Adapter<BannerSliderAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.bannerImage)
        val cardView: CardView = view.findViewById(R.id.cardView)
        val tvTitle: TextView = view.findViewById(R.id.tvMovieTitle)
        val tvInfo: TextView = view.findViewById(R.id.tvMovieInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner_slider, parent, false)
        return BannerViewHolder(view)
    }

    @UnstableApi
    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val movie = movies[position]

        // Load poster
        val fullPosterUrl = if (movie.posterUrl?.startsWith("http") == true) {
            movie.posterUrl
        } else {
            "https://img.ophim.live/uploads/movies/${movie.posterUrl}"
        }

        Glide.with(holder.itemView.context)
            .load(fullPosterUrl)
            .placeholder(R.drawable.anime_8)
            .error(R.drawable.anime_8)
            .into(holder.imageView)

        // Tiêu đề phim
        holder.tvTitle.text = movie.name

        // Info: nếu phim series thì hiện số tập, nếu phim lẻ thì hiện duration
        holder.tvInfo.text = if (movie.totalEpisodes > 1) {
            "${movie.year} • ${movie.currentEpisodes}/${movie.totalEpisodes} tập"
        } else {
            movie.duration?.let { durationInSeconds ->
                val durationInMinutes = durationInSeconds / 60  // đổi sang phút
                if (durationInMinutes >= 60) {
                    val hours = durationInMinutes / 60
                    val minutes = durationInMinutes % 60
                    "${movie.year} • ${hours} giờ ${minutes} phút"
                } else {
                    "${movie.year} • ${durationInMinutes} phút"
                }
            } ?: "${movie.year} • 0 phút"

        }


        // Click mở MovieDetailActivity
        holder.cardView.setOnClickListener {
            val intent = Intent(context, MovieDetailActivity::class.java).apply {
                putExtra("movie_id", movie.id)
                putExtra("movie_name", movie.name)
                putExtra("movie_poster", movie.posterUrl)
                putExtra("movie_thumb", movie.thumbUrl)
                putExtra("movie_year", movie.year)
                putExtra("movie_content", movie.content)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = movies.size
}