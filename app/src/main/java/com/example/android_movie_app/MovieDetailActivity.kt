package com.example.android_movie_app

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import com.example.android_movie_app.databinding.LayoutMovieDetailBinding

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: LayoutMovieDetailBinding
    private lateinit var adapter: MovieDetailAdapter

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getIntExtra("movie_id", 0)
        val movieName = intent.getStringExtra("movie_name") ?: ""

        adapter = MovieDetailAdapter(this, binding, movieId, movieName)
        adapter.onCreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adapter.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
    }
}


