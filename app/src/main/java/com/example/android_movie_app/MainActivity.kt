package com.example.android_movie_app

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import com.example.android_movie_app.adapter.MainAdapter
import com.example.android_movie_app.dao.MovieDAO
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var genreContainer: LinearLayout
    private lateinit var topContainer: LinearLayout
    private lateinit var posterContainer: LinearLayout
    private lateinit var bannerImage: ImageView
    private lateinit var bannerTag: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var mainAdapter: MainAdapter
    private lateinit var movieDAO: MovieDAO

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Khởi tạo DatabaseHelper và DAO trước
        val dbHelper = DatabaseHelper(this)
        movieDAO = MovieDAO(dbHelper)
        mainAdapter = MainAdapter(this)

        // 2. Sau đó mới gọi findViewById
        genreContainer = findViewById(R.id.genreContainer)
        topContainer = findViewById(R.id.topContainer)
        posterContainer = findViewById(R.id.posterContainer)
        bannerImage = findViewById(R.id.bannerImage)
        bannerTag = findViewById(R.id.bannerTag)
        bottomNavigation = findViewById(R.id.bottomNavigationView)

        // 3. Lấy dữ liệu từ database
        val genres = movieDAO.getGenres()
        val recentMovies = movieDAO.getRecentMovies()
        val topMovies = movieDAO.getTopMovies()

        // 4. Đưa dữ liệu lên UI
        mainAdapter.setGenres(genreContainer, genres)
        mainAdapter.setRecentMovies(posterContainer, recentMovies)
        mainAdapter.setTopMovies(topContainer, topMovies)
    }
}
