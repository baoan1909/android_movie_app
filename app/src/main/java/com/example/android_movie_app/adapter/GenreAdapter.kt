package com.example.android_movie_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import com.example.android_movie_app.Movie
import com.example.android_movie_app.R

class GenreAdapter(
    private val context: Context,
    private var movieList: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = movieList.size

    override fun getItem(position: Int): Any = movieList[position]

    override fun getItemId(position: Int): Long = movieList[position].id.toLong()

    @UnstableApi
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_genre_movie, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val movie = movieList[position]

        // Hiển thị tên phim
        holder.txtMovieName.text = movie.name

        // Load ảnh bằng Glide
        val fullPosterUrl = if (movie.posterUrl?.startsWith("http") == true) {
            movie.posterUrl
        } else {
            "https://img.ophim.live/uploads/movies/${movie.posterUrl}"
        }

        Glide.with(context)
            .load(fullPosterUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.bannerImage)

        // Sử dụng callback onItemClick
        view.setOnClickListener {
            onItemClick(movie)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val bannerImage: ImageView = view.findViewById(R.id.bannerImage)
        val txtMovieName: TextView = view.findViewById(R.id.txtMovieName)
    }
}
