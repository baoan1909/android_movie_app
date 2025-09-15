package com.example.android_movie_app

import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import com.example.android_movie_app.adapter.GenreAdapter
import com.example.android_movie_app.dao.MovieDAO

class GenreDetailActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var txtTitle: TextView
    private lateinit var genreAdapter: GenreAdapter
    private lateinit var movieDAO: MovieDAO

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_genre_detail)

        // Ánh xạ view
        gridView = findViewById(R.id.gridViewMovie)
        txtTitle = findViewById(R.id.txtGenreTitle)

        // Lấy genreName từ Intent
        val genreName = intent.getStringExtra("genreName") ?: "Thể loại"

        // Set title
        txtTitle.text = "$genreName"

        // Khởi tạo DAO
        val dbHelper = DatabaseHelper(this)
        movieDAO = MovieDAO(dbHelper)

        // Lấy danh sách phim theo thể loại
        val movies = movieDAO.getMoviesByGenre(genreName)

        // Gắn adapter
        genreAdapter = GenreAdapter(this, movies) { movie ->
            val intent = Intent(this, MovieDetailActivity::class.java).apply {
                putExtra("movie_id", movie.id)
                putExtra("movie_name", movie.name)
                putExtra("movie_poster", movie.posterUrl)
                putExtra("movie_thumb", movie.thumbUrl)
                putExtra("movie_rating", movie.rating)
                putExtra("movie_year", movie.year)
                putExtra("movie_content", movie.content)
            }
            startActivity(intent)
        }
        gridView.adapter = genreAdapter
    }
}
