package com.example.android_movie_app

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
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
import com.example.android_movie_app.dao.*
import com.example.android_movie_app.databinding.LayoutMovieDetailBinding

@UnstableApi
class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: LayoutMovieDetailBinding
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    private lateinit var episodeDAO: EpisodeDAO
    private lateinit var watchProgressDAO: WatchProgressDAO
    private lateinit var movieDAO: MovieDAO

    private var movieId: Int = 0
    private var movieName: String = ""
    private var currentEpisodeId: Int = 0
    private var isLandscape: Boolean = false

    // Continue watching
    private var savedPosition: Long = 0

    // SeekBar update
    private var updateSeekBarRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false

    // Auto-hide controls
    private var controlsVisible = true
    private val autoHideHandler = Handler(Looper.getMainLooper())
    private val autoHideRunnable = Runnable { hideControls() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = DatabaseHelper(this)
        episodeDAO = EpisodeDAO(dbHelper)
        watchProgressDAO = WatchProgressDAO(dbHelper)
        movieDAO = MovieDAO(dbHelper)

        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (isLandscape) setupFullscreen()

        movieId = intent.getIntExtra("movie_id", 0)
        movieName = intent.getStringExtra("movie_name") ?: ""

        initViews()
        setupPlayer()
        loadMovieDetailsAndPlay()
        setupTabs()
        setupCustomControls()
        setupSeekBar()
        startSeekBarUpdater()
        startAutoHide()
    }

    private fun initViews() {
        playerView = binding.playerView
        playerView.useController = false

        // Toggle controls khi chạm vào video
        playerView.setOnClickListener { toggleControlsWithTimer() }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Xem ngay phim $movieName trên MovieApp!")
            startActivity(Intent.createChooser(intent, "Chia sẻ với"))
            showControlsTemporarily()
        }
        binding.btnFollow.setOnClickListener {
            Toast.makeText(this, "Đã thêm vào danh sách theo dõi", Toast.LENGTH_SHORT).show()
            showControlsTemporarily()
        }
        binding.btnDownload.setOnClickListener {
            Toast.makeText(this, "Bắt đầu tải xuống...", Toast.LENGTH_SHORT).show()
            showControlsTemporarily()
        }
    }

    private fun setupCustomControls() {
        binding.btnPlayPause.setOnClickListener {
            if (player?.isPlaying == true) {
                player?.pause()
                binding.btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
            } else {
                player?.play()
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            }
            showControlsTemporarily()
        }

        binding.btnFullscreen.setOnClickListener {
            if (isLandscape) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            showControlsTemporarily()
        }


        binding.btnForward10.setOnClickListener {
            player?.let {
                val pos = it.currentPosition + 10_000
                it.seekTo(pos.coerceAtMost(it.duration))
            }
            showControlsTemporarily()
        }

        binding.btnReplay10.setOnClickListener {
            player?.let {
                val pos = (it.currentPosition - 10_000).coerceAtLeast(0)
                it.seekTo(pos)
            }
            showControlsTemporarily()
        }
    }

    private fun setupTabs() {
        val tabs = listOf(
            binding.tabEpisodes,
            binding.tabSeasons,
            binding.tabTrailers,
            binding.tabRelated
        )
        for (tab in tabs) {
            tab.setOnClickListener {
                highlightTab(tab)
                showControlsTemporarily()
                when (tab.id) {
                    R.id.tabEpisodes -> Toast.makeText(this, "Danh sách tập", Toast.LENGTH_SHORT).show()
                    R.id.tabSeasons -> Toast.makeText(this, "Danh sách season", Toast.LENGTH_SHORT).show()
                    R.id.tabTrailers -> Toast.makeText(this, "Trailer", Toast.LENGTH_SHORT).show()
                    R.id.tabRelated -> Toast.makeText(this, "Phim liên quan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun highlightTab(selectedTab: TextView) {
        val tabs = listOf(
            binding.tabEpisodes,
            binding.tabSeasons,
            binding.tabTrailers,
            binding.tabRelated
        )
        for (tab in tabs) {
            tab.alpha = if (tab == selectedTab) 1f else 0.5f
        }
        binding.dividerTabs.animate()
            .x(selectedTab.x)
            .setDuration(200)
            .start()
    }

    // ===== Controls fade in/out =====
    private fun showControls() {
        if (!controlsVisible) {
            controlsVisible = true
            fadeIn(binding.layoutTopControls)
            fadeIn(binding.layoutCenterControls)
            fadeIn(binding.layoutBottomControls)
        }
    }

    private fun hideControls() {
        if (controlsVisible && !isUserSeeking) { // không ẩn khi user đang kéo SeekBar
            controlsVisible = false
            fadeOut(binding.layoutTopControls)
            fadeOut(binding.layoutCenterControls)
            fadeOut(binding.layoutBottomControls)
        }
    }

    private fun toggleControlsWithTimer() {
        if (controlsVisible) {
            hideControls()
            cancelAutoHide()
        } else {
            showControls()
            startAutoHide()
        }
    }

    private fun showControlsTemporarily() {
        showControls()
        startAutoHide()
    }

    private fun fadeIn(view: View) {
        view.visibility = View.VISIBLE
        view.alpha = 0f
        view.animate().alpha(1f).setDuration(300).start()
    }

    private fun fadeOut(view: View) {
        view.animate().alpha(0f).setDuration(300)
            .withEndAction { view.visibility = View.GONE }
            .start()
    }

    private fun startAutoHide() {
        cancelAutoHide()
        autoHideHandler.postDelayed(autoHideRunnable, 3000)
    }

    private fun cancelAutoHide() {
        autoHideHandler.removeCallbacks(autoHideRunnable)
    }

    private fun setupFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun exitFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.show(WindowInsetsCompat.Type.systemBars())
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun setupMovieInfo(
        title: String,
        rating: Double,
        year: Int,
        content: String?,
        isSeries: Boolean = false,
        totalEpisodes: Int = 0,
        episodesReleased: Int = 0
    ) {
        binding.txtMovieTitle.text = title
        binding.txtMovieRating.text = "⭐ $rating • Đánh giá"

        binding.txtMovieYear.text = if (year > 0) {
            if (isSeries) "$year | T18 | $episodesReleased/$totalEpisodes tập"
            else "$year | T18 | single"
        } else {
            if (isSeries) "$episodesReleased/$totalEpisodes tập" else "single"
        }

        binding.txtMovieInfo.text = content ?: "Đang cập nhật thông tin phim..."
    }

    private fun loadMovieDetailsAndPlay() {
        if (movieId == 0) {
            Toast.makeText(this, "Không tìm thấy thông tin phim", Toast.LENGTH_SHORT).show()
            return
        }

        val movie = movieDAO.getMovieById(movieId)
        if (movie != null) {
            val episodes = episodeDAO.getEpisodesByMovieAsc(movieId)
            if (episodes.isNotEmpty()) {
                val totalEpisodes = episodes.size
                val releasedEpisodes = episodes.count { it.videoUrl.isNotEmpty() }
                setupMovieInfo(
                    title = movie.name,
                    rating = movie.rating,
                    year = movie.year ?: 0,
                    content = movie.content,
                    isSeries = true,
                    totalEpisodes = totalEpisodes,
                    episodesReleased = releasedEpisodes
                )
            } else {
                setupMovieInfo(
                    title = movie.name,
                    rating = movie.rating,
                    year = movie.year ?: 0,
                    content = movie.content,
                    isSeries = false
                )
            }
        } else {
            setupMovieInfo(
                title = movieName,
                rating = 0.0,
                year = 0,
                content = "Đang cập nhật thông tin phim...",
                isSeries = false
            )
        }

        loadEpisodeAndPlay()
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        player?.addListener(object : Player.Listener {})
    }

    private fun loadEpisodeAndPlay() {
        val episodes = episodeDAO.getEpisodesByMovieAsc(movieId)
        if (episodes.isNotEmpty()) {
            val watchProgress = watchProgressDAO.getWatchProgress(1, movieId)
            val episodeToPlay = if (watchProgress?.episodeId != null) {
                episodes.find { it.id == watchProgress.episodeId } ?: episodes[0]
            } else episodes[0]

            currentEpisodeId = episodeToPlay.id
            val videoUrl = episodeToPlay.videoUrl
            if (videoUrl.isNotEmpty()) {
                savedPosition = (watchProgress?.currentTime?.toLong() ?: 0) * 1000
                playVideo(videoUrl)
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
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        player?.setMediaItem(mediaItem)
        player?.prepare()

        val prefs = getSharedPreferences("video_state", MODE_PRIVATE)
        val orientationPosition = prefs.getLong("current_position", 0)
        val playWhenReady = prefs.getBoolean("play_when_ready", true)
        val positionToSeek = if (orientationPosition > 0) {
            prefs.edit().clear().apply()
            orientationPosition
        } else savedPosition

        if (positionToSeek > 0) player?.seekTo(positionToSeek)
        player?.playWhenReady = playWhenReady
    }

    private fun setupSeekBar() {
        binding.seekBar.max = 0
        binding.seekBar.progress = 0

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    isUserSeeking = true
                    binding.txtCurrentTime.text = formatTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
                cancelAutoHide()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                player?.seekTo(binding.seekBar.progress.toLong())
                isUserSeeking = false
                startAutoHide()
            }
        })
    }

    private fun startSeekBarUpdater() {
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                if (player != null && player!!.isPlaying && !isUserSeeking) {
                    val current = player!!.currentPosition
                    val total = player!!.duration

                    if (total > 0) {
                        binding.seekBar.max = total.toInt()
                        binding.seekBar.progress = current.toInt()
                        binding.txtCurrentTime.text = formatTime(current)
                        binding.txtTotalTime.text = formatTime(total)
                    }
                }
                handler.postDelayed(this, 500)
            }
        }
        handler.post(updateSeekBarRunnable!!)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val newIsLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (newIsLandscape != isLandscape) {
            isLandscape = newIsLandscape

            if (isLandscape) {
                setupFullscreen()
                binding.btnFullscreen.setImageResource(R.drawable.ic_fullscreen_exit)
            } else {
                exitFullscreen()
                binding.btnFullscreen.setImageResource(R.drawable.ic_fullscreen)
            }

            // Lưu lại state player
            val currentPosition = player?.currentPosition ?: 0
            val playWhenReady = player?.playWhenReady ?: false
            val prefs = getSharedPreferences("video_state", MODE_PRIVATE)
            prefs.edit()
                .putLong("current_position", currentPosition)
                .putBoolean("play_when_ready", playWhenReady)
                .apply()

            recreate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveWatchProgress()
        player?.release()
        player = null
        if (isLandscape) exitFullscreen()
        autoHideHandler.removeCallbacks(autoHideRunnable)
    }

    private fun saveWatchProgress() {
        if (movieId > 0 && currentEpisodeId > 0 && player != null) {
            val currentPosition = player!!.currentPosition / 1000
            val duration = player!!.duration / 1000
            if (currentPosition > 0 && duration > 0) {
                val progress = WatchProgress(
                    userId = 1,
                    movieId = movieId,
                    episodeId = currentEpisodeId,
                    currentTime = currentPosition.toInt(),
                    totalTime = duration.toInt(),
                    isCompleted = currentPosition >= duration * 0.95,
                    lastWatchedAt = java.util.Date()
                )
                watchProgressDAO.upsertWatchProgress(progress)
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
