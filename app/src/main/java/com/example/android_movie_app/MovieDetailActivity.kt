package com.example.android_movie_app

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.example.android_movie_app.dao.EpisodeDAO
import com.example.android_movie_app.dao.WatchProgressDAO

@UnstableApi
class MovieDetailActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var movieTitle: TextView
    private lateinit var movieRating: TextView
    private lateinit var movieYear: TextView
    private lateinit var movieInfo: TextView
    private lateinit var backButton: ImageView
    private var movieInfoOverlay: LinearLayout? = null
    
    private var player: ExoPlayer? = null
    private lateinit var episodeDAO: EpisodeDAO
    private lateinit var watchProgressDAO: WatchProgressDAO
    
    private var movieId: Int = 0
    private var movieName: String = ""
    private var currentEpisodeId: Int = 0
    private var isLandscape: Boolean = false
    
    // Continue watching variables
    private var savedPosition: Long = 0
    private var isUserPause: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_movie_detail)

        // Khởi tạo DAO
        val dbHelper = DatabaseHelper(this)
        episodeDAO = EpisodeDAO(dbHelper)
        watchProgressDAO = WatchProgressDAO(dbHelper)
        
        // Kiểm tra orientation
        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        
        // Setup fullscreen nếu landscape
        if (isLandscape) {
            setupFullscreen()
        }

        // Lấy dữ liệu từ Intent
        movieId = intent.getIntExtra("movie_id", 0)
        movieName = intent.getStringExtra("movie_name") ?: ""

        initViews()
        setupPlayer()
        loadMovieDetailsAndPlay()
    }

    private fun initViews() {
        // Tìm views trong layout - sử dụng safe approach
        try {
            movieTitle = findViewById(R.id.movieTitle)
            movieRating = findViewById(R.id.movieRating)
            movieYear = findViewById(R.id.movieYear)
            movieInfo = findViewById(R.id.movieInfo)
            backButton = findViewById(R.id.backButton)
        } catch (e: Exception) {
            // Nếu không tìm thấy views, tạo default
            movieTitle = TextView(this)
            movieRating = TextView(this)
            movieYear = TextView(this)
            movieInfo = TextView(this)
            backButton = ImageView(this)
        }
        
        // Tạo PlayerView và thêm vào container
        val videoContainer = findViewById<android.widget.FrameLayout>(R.id.videoContainer)
        playerView = PlayerView(this)
        playerView.useController = true  // Enable ExoPlayer default controls
        playerView.controllerAutoShow = true
        playerView.showController()
        
        if (videoContainer != null) {
            // Clear container trước khi add player view
            videoContainer.removeAllViews()
            videoContainer.addView(playerView, android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT
            ))
        }

        backButton.setOnClickListener {
            finish()
        }
        
        // Setup overlay cho landscape
        movieInfoOverlay = findViewById(R.id.movieInfoOverlay)
    }
    
    private fun setupFullscreen() {
        // Ẩn status bar và navigation bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        
        // Giữ màn hình sáng
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    
    private fun exitFullscreen() {
        // Hiện lại system bars
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.show(WindowInsetsCompat.Type.systemBars())
        
        // Bỏ flag giữ màn hình sáng
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    
    

    private fun setupMovieInfo(title: String, rating: Double, year: Int, content: String?) {
        movieTitle.text = title
        movieRating.text = "⭐ $rating • Đánh giá"
        movieYear.text = if (year > 0) "$year | T18" else "T18"
        movieInfo.text = content ?: "Đang cập nhật thông tin phim..."
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        // Đang buffer
                    }
                    Player.STATE_READY -> {
                        // Sẵn sàng phát
                    }
                    Player.STATE_ENDED -> {
                        // Kết thúc
                    }
                }
            }
        })
    }

    private fun loadMovieDetailsAndPlay() {
        if (movieId == 0) {
            Toast.makeText(this, "Không tìm thấy thông tin phim", Toast.LENGTH_SHORT).show()
            return
        }

        // Lấy thông tin chi tiết phim từ database
        val movieDAO = com.example.android_movie_app.dao.MovieDAO(DatabaseHelper(this))
        val movie = movieDAO.getMovieById(movieId)
        
        if (movie != null) {
            setupMovieInfo(movie.name, movie.rating, movie.year ?: 0, movie.content)
        } else {
            setupMovieInfo(movieName, 0.0, 0, "Đang cập nhật thông tin phim...")
        }

        loadEpisodeAndPlay()
    }

    private fun loadEpisodeAndPlay() {
        // Lấy danh sách tập phim
        val episodes = episodeDAO.getEpisodesByMovieAsc(movieId)
        
        if (episodes.isNotEmpty()) {
            // Kiểm tra continue watching trước
            val watchProgress = watchProgressDAO.getWatchProgress(1, movieId) // userId = 1 tạm thời
            
            val episodeToPlay = if (watchProgress != null && watchProgress.episodeId != null) {
                // Có tiến độ xem, tìm episode đó
                episodes.find { it.id == watchProgress.episodeId } ?: episodes[0]
            } else {
                // Chưa có tiến độ, phát tập đầu tiên
                episodes[0]
            }
            
            currentEpisodeId = episodeToPlay.id
            val videoUrl = episodeToPlay.videoUrl
            
            if (videoUrl.isNotEmpty()) {
                // Lấy position đã lưu
                savedPosition = (watchProgress?.currentTime?.toLong() ?: 0) * 1000 // Convert to milliseconds
                playVideo(videoUrl)
                
                // Hiện thông báo continue watching nếu có
                if (savedPosition > 0) {
                    Toast.makeText(this, "Tiếp tục từ ${formatTime(savedPosition)}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Không tìm thấy video", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Phim chưa có tập nào", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playVideo(videoUrl: String) {
        try {
            // Hiển thị thông báo đang load
            
            val mediaItem = MediaItem.fromUri(videoUrl)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            
            // Kiểm tra nếu có position từ orientation change
            val prefs = getSharedPreferences("video_state", MODE_PRIVATE)
            val orientationPosition = prefs.getLong("current_position", 0)
            val playWhenReady = prefs.getBoolean("play_when_ready", true)
            
            // Ưu tiên position từ orientation change, sau đó đến continue watching
            val positionToSeek = if (orientationPosition > 0) {
                // Clear preference sau khi sử dụng
                prefs.edit().clear().apply()
                orientationPosition
            } else {
                savedPosition
            }
            
            // Seek tới vị trí đã lưu nếu có
            if (positionToSeek > 0) {
                player?.seekTo(positionToSeek)
            }
            
            player?.playWhenReady = playWhenReady
            
            // Log để debug
            android.util.Log.d("MovieDetail", "Playing video: $videoUrl at position: $positionToSeek")
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi phát video: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("MovieDetail", "Error playing video", e)
        }
    }

    override fun onStart() {
        super.onStart()
        if (android.os.Build.VERSION.SDK_INT > 23) {
            player?.playWhenReady = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (android.os.Build.VERSION.SDK_INT <= 23) {
            player?.playWhenReady = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (android.os.Build.VERSION.SDK_INT <= 23) {
            isUserPause = true
            player?.pause()
        }
        // Lưu tiến độ xem khi pause
        saveWatchProgress()
    }

    override fun onStop() {
        super.onStop()
        if (android.os.Build.VERSION.SDK_INT > 23) {
            player?.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Lưu tiến độ trước khi thoát
        saveWatchProgress()
        player?.release()
        player = null
        
        // Exit fullscreen nếu đang ở landscape
        if (isLandscape) {
            exitFullscreen()
        }
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        
        val newIsLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
        
        if (newIsLandscape != isLandscape) {
            isLandscape = newIsLandscape
            
            // Lưu current position và play state trước khi recreate
            val currentPosition = player?.currentPosition ?: 0
            val playWhenReady = player?.playWhenReady ?: false
            
            // Lưu vào shared preferences để khôi phục sau recreate
            val prefs = getSharedPreferences("video_state", MODE_PRIVATE)
            prefs.edit()
                .putLong("current_position", currentPosition)
                .putBoolean("play_when_ready", playWhenReady)
                .apply()
            
            // Recreate activity để load layout mới
            recreate()
        }
    }
    
    private fun saveWatchProgress() {
        if (movieId > 0 && currentEpisodeId > 0 && player != null) {
            try {
                val currentPosition = player!!.currentPosition / 1000 // Convert to seconds
                val duration = player!!.duration / 1000 // Convert to seconds
                
                if (currentPosition > 0 && duration > 0) {
                    val progress = com.example.android_movie_app.WatchProgress(
                        userId = 1, // Tạm thời dùng userId = 1
                        movieId = movieId,
                        episodeId = currentEpisodeId,
                        currentTime = currentPosition.toInt(),
                        totalTime = duration.toInt(),
                        isCompleted = currentPosition >= duration * 0.95, // Coi như hoàn thành nếu xem >= 95%
                        lastWatchedAt = java.util.Date()
                    )
                    
                    watchProgressDAO.upsertWatchProgress(progress)
                    android.util.Log.d("MovieDetail", "Saved watch progress: ${currentPosition}s / ${duration}s")
                }
            } catch (e: Exception) {
                android.util.Log.e("MovieDetail", "Error saving watch progress", e)
            }
        }
    }
    
    private fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60)
        } else {
            String.format("%d:%02d", minutes, seconds % 60)
        }
    }
}