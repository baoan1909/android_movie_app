package com.example.android_movie_app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.widget.ViewPager2
import com.example.android_movie_app.adapter.BannerSliderAdapter
import com.example.android_movie_app.adapter.MainAdapter
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
    // Handler để tự động cuộn
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderRunnable: Runnable

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // 1. Khởi tạo DatabaseHelper và DAO trước
        val dbHelper = DatabaseHelper(this)
        movieDAO = MovieDAO(dbHelper)
        mainAdapter = MainAdapter(this)

        viewPager = findViewById(R.id.viewPagerBanner)
        tabLayout = findViewById(R.id.tabLayoutIndicator)

        // 2. Sau đó mới gọi findViewById
        genreContainer = findViewById(R.id.genreContainer)
        topContainer = findViewById(R.id.topContainer)
        posterContainer = findViewById(R.id.posterContainer)
        posterSchoolContainer = findViewById(R.id.posterSchoolContainer)
        posterFictionContainer = findViewById(R.id.posterFictionContainer)
        posterHumorousContainer = findViewById(R.id.posterHumorousContainer)


        // 3. Lấy dữ liệu từ database
        val genres = movieDAO.getGenres()
        val recentMovies = movieDAO.getRecentMovies()
        val topMovies = movieDAO.getTopMovies()
        val schoolMovies = movieDAO.getMoviesByGenre("Học Đường")
        val fictionMovies = movieDAO.getMoviesByGenre("Viễn Tưởng")
        val XianxiaMovies = movieDAO.getMoviesByGenre("Hài Hước")

        // 4. Đưa dữ liệu lên UI
        mainAdapter.setGenres(genreContainer, genres)
        mainAdapter.setRecentMovies(posterContainer, recentMovies)
        mainAdapter.setTopMovies(topContainer, topMovies)
        mainAdapter.setRecentMovies(posterSchoolContainer, schoolMovies)
        mainAdapter.setRecentMovies(posterFictionContainer, fictionMovies)
        mainAdapter.setRecentMovies(posterHumorousContainer, XianxiaMovies)

        if (topMovies.isNotEmpty()) {
            val bannerMovies = movieDAO.getMoviesForBanner(5) // Lấy 5 phim đầu tiên
            bannerAdapter = BannerSliderAdapter(this, bannerMovies)
            viewPager.adapter = bannerAdapter


            // Kết nối lại TabLayoutMediator nếu cần
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                // ...
            }.attach()
        }

        // Logic tự động cuộn
        setupAutoSlider()
    }

    private fun setupAutoSlider() {
        sliderRunnable = Runnable {
            var currentItem = viewPager.currentItem
            currentItem++
            // Nếu là banner cuối cùng, quay lại banner đầu tiên
            if (currentItem >= bannerAdapter.itemCount) {
                currentItem = 0
            }
            viewPager.setCurrentItem(currentItem, true) // Cuộn mượt
        }

        // Đăng ký callback để bắt đầu cuộn lại khi người dùng tương tác
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Xóa callback cũ và đặt lại timer
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 5000) // 5 giây
            }
        })
    }

    // Bắt đầu và dừng auto-slide theo vòng đời của Activity
    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }
}
