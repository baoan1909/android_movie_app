package com.example.android_movie_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import android.util.Log

import com.bumptech.glide.Glide
import com.example.android_movie_app.Movie
import com.example.android_movie_app.R

class SearchResultAdapter(
    context: Context,
    private val movies: List<Movie>
) : ArrayAdapter<Movie>(context, 0, movies) {

    private var filteredMovies: List<Movie> = movies

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_search, parent, false)

        val movie = getItem(position)
        val imgPoster = view.findViewById<ImageView>(R.id.imgPosterSearch)
        val tvMovieName = view.findViewById<TextView>(R.id.tvMovieNameSearch)

        tvMovieName.text = movie?.name
        Glide.with(parent.context)
            .load("https://img.ophim.live/uploads/movies/${movie?.posterUrl}")
            .placeholder(R.drawable.ic_movie)
            .error(R.drawable.ic_error_circle)
            .into(imgPoster)

        return view
    }

    override fun getItem(position: Int): Movie? {
        return filteredMovies[position]
    }

    override fun getCount(): Int {
        return filteredMovies.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint.isNullOrEmpty()) {
                    results.values = movies
                    results.count = movies.size
                } else {
                    val filtered = movies.filter {
                        it.name.contains(constraint, ignoreCase = true)
                        // Nếu muốn tìm theo category thì phải truyền MovieWithCategories
                    }
                    results.values = filtered
                    results.count = filtered.size
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filteredMovies = results?.values as? List<Movie> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}
