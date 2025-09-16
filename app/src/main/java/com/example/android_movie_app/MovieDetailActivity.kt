package com.example.android_movie_app

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ScrollView
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
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.android_movie_app.dao.*
import com.example.android_movie_app.databinding.LayoutMovieDetailBinding

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: LayoutMovieDetailBinding
    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null

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

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = DatabaseHelper(this)
        episodeDAO = EpisodeDAO(dbHelper)
        watchProgressDAO = WatchProgressDAO(dbHelper)
        movieDAO = MovieDAO(dbHelper)

        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        movieId = intent.getIntExtra("movie_id", 0)
        movieName = intent.getStringExtra("movie_name") ?: ""

        initViews()
        setupPlayer()
        loadMovieDetailsAndPlay()
        setupTabs()
        setupPlayerControls()
        setupSeekBar()
        startSeekBarUpdater()
        startAutoHide()

        // Khởi tạo UI đúng orientation ban đầu
        updateFullscreenUI()
    }

    private fun initViews() {
        playerView = binding.playerView
        playerView?.useController = false
        playerView?.setOnClickListener { toggleControlsWithTimer() }

        binding.btnBack?.setOnClickListener { finish() }
        binding.btnShare?.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Xem ngay phim $movieName trên MovieApp!")
            startActivity(Intent.createChooser(intent, "Chia sẻ với"))
            showControlsTemporarily()
        }
        binding.btnFollow?.setOnClickListener {
            Toast.makeText(this, "Đã thêm vào danh sách theo dõi", Toast.LENGTH_SHORT).show()
            showControlsTemporarily()
        }
        binding.btnDownload?.setOnClickListener {
            Toast.makeText(this, "Bắt đầu tải xuống...", Toast.LENGTH_SHORT).show()
            showControlsTemporarily()
        }
    }

    @UnstableApi
    private fun setupPlayerControls() {
        binding.playerView?.useController = false

        binding.btnPlayPause?.setOnClickListener {
            player?.let {
                if (it.isPlaying) {
                    it.pause()
                    binding.btnPlayPause?.setImageResource(R.drawable.ic_play_arrow)
                } else {
                    it.play()
                    binding.btnPlayPause?.setImageResource(R.drawable.ic_pause)
                }
            }
            showControlsTemporarily()
        }

        binding.btnReplay10?.setOnClickListener {
            player?.seekTo((player?.currentPosition ?: 0) - 10_000)
            showControlsTemporarily()
        }
        binding.btnForward10?.setOnClickListener {
            player?.seekTo((player?.currentPosition ?: 0) + 10_000)
            showControlsTemporarily()
        }

        binding.btnFullscreen?.setOnClickListener {
            isLandscape = !isLandscape
            requestedOrientation = if (isLandscape)
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            updateFullscreenUI()
            showControlsTemporarily()
        }
    }

    @UnstableApi
    private fun updateFullscreenUI() {
        if (isLandscape) {
            binding.layoutMovieInfo?.visibility = View.GONE
            binding.layoutActionButtons?.visibility = View.GONE
            binding.layoutComments?.visibility = View.GONE
            binding.layoutTabs?.visibility = View.GONE
            binding.layoutAdBanner?.visibility = View.GONE

            (binding.root as? ScrollView)?.apply {
                isVerticalScrollBarEnabled = false
                scrollTo(0, 0)
            }

            binding.playerContainer?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.videoContainer?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.playerView?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

            binding.btnFullscreen?.setImageResource(R.drawable.ic_fullscreen_exit)
            setupFullscreen()
        } else {
            binding.layoutMovieInfo?.visibility = View.VISIBLE
            binding.layoutActionButtons?.visibility = View.VISIBLE
            binding.layoutComments?.visibility = View.VISIBLE
            binding.layoutTabs?.visibility = View.VISIBLE
            binding.layoutAdBanner?.visibility = View.VISIBLE

            (binding.root as? ScrollView)?.isVerticalScrollBarEnabled = true

            val portraitHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 220f, resources.displayMetrics
            ).toInt()

            binding.playerContainer?.layoutParams?.height = portraitHeight
            binding.videoContainer?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.playerView?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

            binding.btnFullscreen?.setImageResource(R.drawable.ic_fullscreen)
            exitFullscreen()
        }

        binding.playerContainer?.requestLayout()
        binding.videoContainer?.requestLayout()
        binding.playerView?.requestLayout()
    }

    private fun setupTabs() {
        val tabs = listOf(
            binding.tabEpisodes,
            binding.tabSeasons,
            binding.tabTrailers,
            binding.tabRelated
        )
        for (tab in tabs) {
            tab?.setOnClickListener {
                tab?.let { selectedTab ->
                    highlightTab(selectedTab)
                    showControlsTemporarily()
                    when (selectedTab.id) {
                        R.id.tabEpisodes -> Toast.makeText(this, "Danh sách tập", Toast.LENGTH_SHORT).show()
                        R.id.tabSeasons -> Toast.makeText(this, "Danh sách season", Toast.LENGTH_SHORT).show()
                        R.id.tabTrailers -> Toast.makeText(this, "Trailer", Toast.LENGTH_SHORT).show()
                        R.id.tabRelated -> Toast.makeText(this, "Phim liên quan", Toast.LENGTH_SHORT).show()
                    }
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
            tab?.alpha = if (tab == selectedTab) 1f else 0.5f
        }
        binding.dividerTabs?.animate()
            ?.x(selectedTab.x)
            ?.setDuration(200)
            ?.start()
    }

    // ===== Controls fade in/out =====
    private fun showControls() {
        if (!controlsVisible) {
            controlsVisible = true
            binding.layoutTopControls?.let { fadeIn(it) }
            binding.layoutCenterControls?.let { fadeIn(it) }
            binding.layoutBottomControls?.let { fadeIn(it) }
        }
    }

    private fun hideControls() {
        if (controlsVisible && !isUserSeeking) {
            controlsVisible = false
            binding.layoutTopControls?.let { fadeOut(it) }
            binding.layoutCenterControls?.let { fadeOut(it) }
            binding.layoutBottomControls?.let { fadeOut(it) }
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
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun exitFullscreen() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.show(WindowInsetsCompat.Type.systemBars())
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    // ====== Player setup ======
    @UnstableApi
    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView?.player = player
        playerView?.setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
        playerView?.setKeepContentOnPlayerReset(true)
        try {
            playerView?.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
        } catch (_: Throwable) { }
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    playerView?.setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            }
        })
    }

    private fun loadMovieDetailsAndPlay() {
        val movie = movieDAO.getMovieById(movieId)
        if (movie != null) {
            val episodes = episodeDAO.getEpisodesByMovieAsc(movieId)
            if (episodes.isNotEmpty()) {
                val totalEpisodes = episodes.size
                val releasedEpisodes = episodes.count { it.videoUrl.isNotEmpty() }
                setupMovieInfo(movie.name, movie.rating, movie.year ?: 0, movie.content, true, totalEpisodes, releasedEpisodes)
            } else {
                setupMovieInfo(movie.name, movie.rating, movie.year ?: 0, movie.content, false)
            }
        } else {
            setupMovieInfo(movieName, 0.0, 0, "Đang cập nhật...", false)
        }
        loadEpisodeAndPlay()
    }

    private fun setupMovieInfo(title: String, rating: Double, year: Int, content: String?, isSeries: Boolean, totalEpisodes: Int = 0, episodesReleased: Int = 0) {
        binding.txtMovieTitle?.text = title
        binding.txtMovieRating?.text = "⭐ $rating • Đánh giá"
        binding.txtMovieYear?.text = if (isSeries) "$year | $episodesReleased/$totalEpisodes tập" else "$year | single"
        binding.txtMovieInfo?.text = content ?: "Đang cập nhật..."
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
            }
        }
    }

    private fun playVideo(videoUrl: String) {
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        player?.setMediaItem(mediaItem)
        player?.prepare()
        if (savedPosition > 0) player?.seekTo(savedPosition)
        player?.playWhenReady = true
        binding.btnPlayPause?.setImageResource(R.drawable.ic_pause)
    }

    private fun setupSeekBar() {
        binding.seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    isUserSeeking = true
                    binding.txtCurrentTime?.text = formatTime(progress.toLong())
                }
            }
            override fun onStartTrackingTouch(sb: SeekBar?) { isUserSeeking = true; cancelAutoHide() }
            override fun onStopTrackingTouch(sb: SeekBar?) {
                player?.seekTo((binding.seekBar?.progress ?: 0).toLong())
                isUserSeeking = false
                startAutoHide()
            }
        })
    }

    private fun startSeekBarUpdater() {
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                player?.let {
                    if (it.isPlaying && !isUserSeeking) {
                        val current = it.currentPosition
                        val total = it.duration
                        if (total > 0) {
                            binding.seekBar?.max = total.toInt()
                            binding.seekBar?.progress = current.toInt()
                            binding.txtCurrentTime?.text = formatTime(current)
                            binding.txtTotalTime?.text = formatTime(total)
                        }
                    }
                }
                handler.postDelayed(this, 500)
            }
        }
        handler.post(updateSeekBarRunnable!!)
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
            val currentPosition = ((player?.currentPosition ?: 0L) / 1000L).toInt()
            val duration = ((player?.duration ?: 0L) / 1000L).toInt()
            if (currentPosition > 0 && duration > 0) {
                val progress = WatchProgress(
                    userId = 1,
                    movieId = movieId,
                    episodeId = currentEpisodeId,
                    currentTime = currentPosition,
                    totalTime = duration,
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
        return if (hours > 0) "%d:%02d:%02d".format(hours, minutes % 60, seconds % 60)
        else "%d:%02d".format(minutes, seconds % 60)
    }
}
