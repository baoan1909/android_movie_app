package com.example.android_movie_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.widget.ViewPager2
import com.example.android_movie_app.adapter.BannerSliderAdapter
import com.example.android_movie_app.adapter.MainAdapter
import com.example.android_movie_app.adapter.SearchResultAdapter
import com.example.android_movie_app.dao.MovieDAO
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity() {

    private lateinit var genreContainer: LinearLayout
    private lateinit var topContainer: LinearLayout
    private lateinit var posterContainer: LinearLayout
    private lateinit var posterSchoolContainer: LinearLayout
    private lateinit var posterFictionContainer: LinearLayout
    private lateinit var posterHumorousContainer: LinearLayout

    private lateinit var mainAdapter: MainAdapter
    private lateinit var movieDAO: MovieDAO
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var bannerAdapter: BannerSliderAdapter

    // Search
    private lateinit var etSearch: AutoCompleteTextView
    private lateinit var btnSearch: ImageView
    private lateinit var searchAdapter: SearchResultAdapter

    // Handler để tự động cuộn
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderRunnable: Runnable

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DatabaseHelper(this)
        movieDAO = MovieDAO(dbHelper)
        mainAdapter = MainAdapter(this)

        // Ánh xạ view
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        viewPager = findViewById(R.id.viewPagerBanner)
        tabLayout = findViewById(R.id.tabLayoutIndicator)
        genreContainer = findViewById(R.id.genreContainer)
        topContainer = findViewById(R.id.topContainer)
        posterContainer = findViewById(R.id.posterContainer)
        posterSchoolContainer = findViewById(R.id.posterSchoolContainer)
        posterFictionContainer = findViewById(R.id.posterFictionContainer)
        posterHumorousContainer = findViewById(R.id.posterHumorousContainer)

        // Search
        val allMovies = movieDAO.getAllMovies()
        searchAdapter = SearchResultAdapter(this, allMovies)
        etSearch.setAdapter(searchAdapter)
        etSearch.threshold = 1 // ✅ hiển thị từ 1 ký tự

        // Khi chọn 1 phim trong dropdown
        etSearch.setOnItemClickListener { parent, _, position, _ ->
            val movie = parent.getItemAtPosition(position) as Movie
            etSearch.setText(movie.name, false)
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra("movie_id", movie.id)
            startActivity(intent)
        }

        // Nút search -> show dropdown
        btnSearch.setOnClickListener {
            if (etSearch.text.isNotEmpty()) {
                etSearch.showDropDown()
            } else {
                Toast.makeText(this, "Nhập tên phim cần tìm!", Toast.LENGTH_SHORT).show()
            }
        }

        // Luôn show dropdown khi text thay đổi
        etSearch.addTextChangedListener {
            if (!it.isNullOrEmpty()) etSearch.showDropDown()
        }

        // Load dữ liệu
        val genres = movieDAO.getGenres()
        val recentMovies = movieDAO.getRecentMovies()
        val topMovies = movieDAO.getTopMovies()
        val schoolMovies = movieDAO.getMoviesByGenre("Học Đường")
        val fictionMovies = movieDAO.getMoviesByGenre("Viễn Tưởng")
        val humorousMovies = movieDAO.getMoviesByGenre("Hài Hước")

        mainAdapter.setGenres(genreContainer, genres)
        mainAdapter.setRecentMovies(posterContainer, recentMovies)
        mainAdapter.setTopMovies(topContainer, topMovies)
        mainAdapter.setRecentMovies(posterSchoolContainer, schoolMovies)
        mainAdapter.setRecentMovies(posterFictionContainer, fictionMovies)
        mainAdapter.setRecentMovies(posterHumorousContainer, humorousMovies)

        // Banner
        if (topMovies.isNotEmpty()) {
            val bannerMovies = movieDAO.getMoviesForBanner(5)
            bannerAdapter = BannerSliderAdapter(this, bannerMovies)
            viewPager.adapter = bannerAdapter
            TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
        }

        setupAutoSlider()
    }

    private fun setupAutoSlider() {
        sliderRunnable = Runnable {
            var currentItem = viewPager.currentItem
            currentItem++
            if (currentItem >= bannerAdapter.itemCount) currentItem = 0
            viewPager.setCurrentItem(currentItem, true)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 5000)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }
}

