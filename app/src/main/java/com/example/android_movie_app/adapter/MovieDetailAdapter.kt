package com.example.android_movie_app.adapter

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.android_movie_app.CustomToast
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.R
import com.example.android_movie_app.RatingDialog
import com.example.android_movie_app.ToastType
import com.example.android_movie_app.WatchProgress
import com.example.android_movie_app.dao.*
import com.example.android_movie_app.databinding.LayoutMovieDetailBinding
import java.util.Date

class MovieDetailAdapter(
    private val activity: Activity,
    private val binding: LayoutMovieDetailBinding,
    private val movieId: Int,
    private val movieName: String
) {

    private var player: ExoPlayer? = null
    private var isLandscape = false
    private var currentEpisodeId = 0
    private var savedPosition: Long = 0

    private val dbHelper = DatabaseHelper(activity)
    private val episodeDAO = EpisodeDAO(dbHelper)
    private val watchProgressDAO = WatchProgressDAO(dbHelper)
    private val movieDAO = MovieDAO(dbHelper)
    private val reviewDAO = ReviewDAO(dbHelper)

    private val handler = Handler(Looper.getMainLooper())
    private var updateSeekBarRunnable: Runnable? = null
    private var isUserSeeking = false

    private var controlsVisible = true
    private val autoHideHandler = Handler(Looper.getMainLooper())
    private val autoHideRunnable = Runnable { hideControls() }

    @UnstableApi
    fun onCreate() {
        isLandscape = activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        initViews()
        setupPlayer()
        loadMovieDetailsAndPlay()
        setupTabs()
        setupPlayerControls()
        setupSeekBar()
        startSeekBarUpdater()
        startAutoHide()
        updateFullscreenUI()
        setupRatingDialog()
    }

    private fun setupRatingDialog() {
        val txtRating = binding.txtRatingDialog // TextView trong layout

        txtRating.setOnClickListener {
            val ratingDialog = RatingDialog(
                context = activity, // dùng activity thay cho context
                movieId = movieId,
                episodeId = null
            ) { rating ->
                CustomToast.show(activity, "Đã đánh giá $rating sao", ToastType.SUCCESS)
            }
            ratingDialog.show()
        }
    }


    private fun initViews() {
        binding.playerView?.useController = false
        binding.playerView?.setOnClickListener { toggleControlsWithTimer() }

        binding.btnBack?.setOnClickListener { activity.finish() }
        binding.btnShare?.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Xem ngay phim $movieName trên MovieApp!")
            }
            activity.startActivity(Intent.createChooser(intent, "Chia sẻ với"))
            showControlsTemporarily()
        }
        binding.btnFollow?.setOnClickListener {
            Toast.makeText(activity, "Đã thêm vào danh sách theo dõi", Toast.LENGTH_SHORT).show()
            showControlsTemporarily()
        }
        binding.btnDownload?.setOnClickListener {
            Toast.makeText(activity, "Bắt đầu tải xuống...", Toast.LENGTH_SHORT).show()
            showControlsTemporarily()
        }
    }

    @UnstableApi
    private fun setupPlayerControls() {
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
            activity.requestedOrientation = if (isLandscape)
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
                TypedValue.COMPLEX_UNIT_DIP, 220f, activity.resources.displayMetrics
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
        val tabs = listOf(binding.tabEpisodes, binding.tabSeasons, binding.tabTrailers, binding.tabRelated)
        for (tab in tabs) {
            tab?.setOnClickListener {
                highlightTab(tab)
                showControlsTemporarily()
                when (tab.id) {
                    R.id.tabEpisodes -> Toast.makeText(activity, "Danh sách tập", Toast.LENGTH_SHORT).show()
                    R.id.tabSeasons -> Toast.makeText(activity, "Danh sách season", Toast.LENGTH_SHORT).show()
                    R.id.tabTrailers -> Toast.makeText(activity, "Trailer", Toast.LENGTH_SHORT).show()
                    R.id.tabRelated -> Toast.makeText(activity, "Phim liên quan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun highlightTab(selectedTab: TextView) {
        val tabs = listOf(binding.tabEpisodes, binding.tabSeasons, binding.tabTrailers, binding.tabRelated)
        for (tab in tabs) {
            tab?.alpha = if (tab == selectedTab) 1f else 0.5f
        }
        binding.dividerTabs?.animate()?.x(selectedTab.x)?.setDuration(200)?.start()
    }

    // ===== Player =====
    @UnstableApi
    private fun setupPlayer() {
        player = ExoPlayer.Builder(activity).build()
        binding.playerView?.player = player
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    binding.playerView?.setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
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
        binding.txtMovieRating?.text = "⭐ $rating • "
        binding.txtMovieYear?.text = if (isSeries) "$year | $episodesReleased/$totalEpisodes tập" else "$year | single"
        binding.txtMovieInfo?.text = content ?: "Đang cập nhật..."
    }

    private fun loadEpisodeAndPlay() {
        val episodes = episodeDAO.getEpisodesByMovieAsc(movieId)
        if (episodes.isNotEmpty()) {
            val watchProgress = watchProgressDAO.getWatchProgress(1, movieId)
            val episodeToPlay = watchProgress?.episodeId?.let { id ->
                episodes.find { it.id == id }
            } ?: episodes[0]
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

    // ===== Controls fade in/out =====
    private fun showControls() {
        if (!controlsVisible) {
            controlsVisible = true
            fadeIn(binding.layoutTopControls!!)
            fadeIn(binding.layoutCenterControls!!)
            fadeIn(binding.layoutBottomControls!!)
        }
    }

    private fun hideControls() {
        if (controlsVisible && !isUserSeeking) {
            controlsVisible = false
            fadeOut(binding.layoutTopControls!!)
            fadeOut(binding.layoutCenterControls!!)
            fadeOut(binding.layoutBottomControls!!)
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

    private fun showControlsTemporarily() { showControls(); startAutoHide() }

    private fun fadeIn(view: View) { view.visibility = View.VISIBLE; view.alpha = 0f; view.animate().alpha(1f).setDuration(300).start() }
    private fun fadeOut(view: View) { view.animate().alpha(0f).setDuration(300).withEndAction { view.visibility = View.GONE }.start() }
    private fun startAutoHide() { cancelAutoHide(); autoHideHandler.postDelayed(autoHideRunnable, 3000) }
    private fun cancelAutoHide() { autoHideHandler.removeCallbacks(autoHideRunnable) }

    private fun setupFullscreen() {
        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun exitFullscreen() {
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        WindowCompat.setDecorFitsSystemWindows(activity.window, true)
        val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        controller.show(WindowInsetsCompat.Type.systemBars())
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    @UnstableApi
    fun onConfigurationChanged(newConfig: Configuration) {
        val newIsLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (newIsLandscape != isLandscape) {
            isLandscape = newIsLandscape
            binding.root.post { updateFullscreenUI() }
        }
    }

    fun onDestroy() {
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
                    lastWatchedAt = Date()
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
