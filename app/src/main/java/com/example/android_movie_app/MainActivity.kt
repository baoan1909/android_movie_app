package com.example.android_movie_app

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.android_movie_app.dao.CategoryDAO
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.bumptech.glide.Glide
import com.example.android_movie_app.dao.MovieDAO

class MainActivity : AppCompatActivity() {

    private lateinit var genreContainer: LinearLayout
    private lateinit var topContainer: LinearLayout
    private lateinit var posterContainer: LinearLayout
    private lateinit var bannerImage: ImageView
    private lateinit var bannerTag: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ view
        genreContainer = findViewById(R.id.genreContainer)
        topContainer = findViewById(R.id.topContainer)
        posterContainer = findViewById(R.id.posterContainer)
        bannerImage = findViewById(R.id.bannerImage)
        bannerTag = findViewById(R.id.bannerTag)
        bottomNavigation = findViewById(R.id.bottomNavigationView)

        // DB
        dbHelper = DatabaseHelper(this)
        categoryDAO = CategoryDAO(dbHelper)

        // Hiển thị dữ liệu
        showGenres()
        showTopAnime()
        showPosterAnime()

    }

    // ---------- Load thể loại từ DB ----------
    private fun showGenres() {
        genreContainer.removeAllViews()
        val categories = categoryDAO.getAllCategories()
        val inflater = LayoutInflater.from(this)

        for (cat in categories) {
            val chipView = inflater.inflate(R.layout.item_genre_container, genreContainer, false) as TextView
            chipView.text = cat.name
            genreContainer.addView(chipView)
        }
    }

    // ---------- Fake Top 10 Anime ----------
    private fun showTopAnime() {
        topContainer.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val sampleImages = listOf(
            R.drawable.anime_1, R.drawable.anime_2, R.drawable.anime_3,
            R.drawable.anime_4, R.drawable.anime_5
        )

        for (resId in sampleImages) {
            val imageView = ImageView(this)
            imageView.setImageResource(resId)
            val params = LinearLayout.LayoutParams(300, 400) // kích thước poster
            params.setMargins(16, 0, 16, 0)
            imageView.layoutParams = params
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            topContainer.addView(imageView)
        }
    }


    // ---------- Hiển thị Poster Anime mùa 2025 từ DB ----------
    private fun showPosterAnime() {
        posterContainer.removeAllViews()
        val movieDAO = MovieDAO(DatabaseHelper(this))
        val summer2025Movies = movieDAO.getMoviesByYear(2025)

        for (movie in summer2025Movies) {
            val imageView = ImageView(this)
            val params = LinearLayout.LayoutParams(300, 450)
            params.setMargins(16, 0, 16, 0)
            imageView.layoutParams = params
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP

            // Load ảnh poster bằng Glide
            Glide.with(this)
                .load(movie.posterUrl) // nếu posterUrl null có thể thay bằng thumbUrl
                .placeholder(R.drawable.ic_launcher_foreground) // ảnh tạm nếu chưa load xong
                .error(R.drawable.ic_launcher_foreground) // ảnh nếu load lỗi
                .into(imageView)

            posterContainer.addView(imageView)
        }
    }

}
