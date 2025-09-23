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
import java.util.Locale
import com.bumptech.glide.Glide
import com.example.android_movie_app.Movie
import com.example.android_movie_app.R
import java.text.Normalizer
import java.util.regex.Pattern

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
            .error(R.drawable.ic_movie)
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
                    val query = removeAccent(constraint.toString().lowercase(Locale.getDefault())).trim()
                    val filtered = movies.filter {
                        val movieName = removeAccent(it.name.lowercase(Locale.getDefault()))
                        movieName.contains(query)
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

    // Hàm bỏ dấu tiếng Việt
    private fun removeAccent(s: String): String {
        val temp = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("")
    }
}

