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
import androidx.core.widget.NestedScrollView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.android_movie_app.CustomToast
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.Episode
import com.example.android_movie_app.GridSpacingItemDecoration
import com.example.android_movie_app.MovieDetailActivity
import com.example.android_movie_app.R
import com.example.android_movie_app.ToastType
import com.example.android_movie_app.WatchProgress
import com.example.android_movie_app.dao.*
import com.example.android_movie_app.databinding.LayoutMovieDetailBinding
import java.util.Date
import com.example.android_movie_app.data.CommentDAO


class MovieDetailAdapter(
    private val activity: Activity,
    private val binding: LayoutMovieDetailBinding,
    private val movieId: Int,
    private val movieName: String
) {

    private var player: ExoPlayer? = null
    private var isLandscape = false
    private var currentEpisodeId: Int? = null // single => null, series => id tập
    private var savedPosition: Long = 0

    private val dbHelper = DatabaseHelper(activity)
    private val episodeDAO = EpisodeDAO(dbHelper)
    private val watchProgressDAO = WatchProgressDAO(dbHelper)
    private val movieDAO = MovieDAO(dbHelper)
    private val commentDAO = CommentDAO(activity)
    private val favoriteDAO = UserFavoriteDAO(dbHelper)
    private var episodeList: List<Episode> = emptyList()
    private val handler = Handler(Looper.getMainLooper())
    private var updateSeekBarRunnable: Runnable? = null
    private var isUserSeeking = false

    private var controlsVisible = true
    private val autoHideHandler = Handler(Looper.getMainLooper())
    private val autoHideRunnable = Runnable { hideControls() }

    // ----------------- INIT -----------------
    @UnstableApi
    fun onCreate() {
        isLandscape = activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        initViews()
        setupPlayer()
        loadMovieDetailsAndPlay()
        setupTabs()
        loadRelatedMovies()
        setupPlayerControls()
        setupSeekBar()
        startSeekBarUpdater()
        startAutoHide()
        updateFullscreenUI()
        setupRatingDialog()
        updateFollowButtonUI()
        loadLatestComment(movieId)

        highlightTab(binding.tabEpisodes!!)
        binding.episodesRecyclerView.visibility = View.VISIBLE
        binding.gridViewTypeMovie.visibility = View.GONE
    }


    // ----------------- RATING -----------------
    private fun setupRatingDialog() {
        val txtRating = binding.txtRatingDialog
        txtRating.setOnClickListener {
            val ratingDialog = RatingDialog(
                context = activity,
                movieId = movieId,
                episodeId = null
            ) { rating ->
                CustomToast.show(activity, "Đã đánh giá $rating sao", ToastType.SUCCESS)
            }
            ratingDialog.show()
        }
    }

    // ----------------- INIT VIEWS -----------------
    @UnstableApi
    private fun initViews() {
        binding.playerView?.useController = false
        binding.playerView?.setOnClickListener { toggleControlsWithTimer() }

        binding.btnBack?.setOnClickListener { activity.finish() }
        binding.btnShare?.setOnClickListener {
            val dialog = ShareDialog(
                context = activity,
                movieName = movieName,
                movieId = movieId
            )
            dialog.show()
            showControlsTemporarily()
        }

        binding.btnFollow?.setOnClickListener {
            toggleFavorite()
            showControlsTemporarily()
        }
        binding.btnDownload?.setOnClickListener {
            Toast.makeText(activity, "Bắt đầu tải xuống...", Toast.LENGTH_SHORT).show()
            showControlsTemporarily()
        }

        binding.btnMore?.setOnClickListener {
            // Lấy tốc độ hiện tại của player, nếu null thì mặc định là 1.0f
            val currentSpeed = player?.playbackParameters?.speed ?: 1.0f

            // Thay thế cách gọi cũ
            val settingsSheet = MainSettingsBottomSheet(
                currentSpeed = currentSpeed, // <-- THÊM DÒNG NÀY
                onQualitySelected = { quality ->
                    changeVideoQuality(quality)
                },
                onSpeedSelected = { speed ->
                    player?.playbackParameters = player?.playbackParameters?.withSpeed(speed)!!
                    CustomToast.show(activity, "Tốc độ phát: ${speed}x", ToastType.INFO)
                    // CẬP NHẬT LẠI GIÁ TRỊ TỐC ĐỘ TRONG UI CỦA MAINSETTINGSBOTTOMSHEET
                }
            )
            settingsSheet.show(
                (activity as MovieDetailActivity).supportFragmentManager,
                "MainSettingsBottomSheet"
            )
            showControlsTemporarily()
        }
    }

    // ----------------- FAVORITES -----------------
    private fun toggleFavorite() {
        val userId = getCurrentUserIdOrNull()
        if (userId == null) {
            Toast.makeText(activity, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        if (favoriteDAO.isFavorite(userId, movieId)) {
            favoriteDAO.removeFavorite(userId, movieId)
            CustomToast.show(activity, "Đã bỏ theo dõi", ToastType.INFO)
        } else {
            favoriteDAO.addFavorite(userId, movieId)
            CustomToast.show(activity, "Đã thêm vào danh sách theo dõi", ToastType.SUCCESS)
        }
        updateFollowButtonUI()
    }

    private fun updateFollowButtonUI() {
        val userId = getCurrentUserIdOrNull()
        if (userId == null) {
            binding.btnFollow?.setCompoundDrawablesWithIntrinsicBounds(
                0, R.drawable.ic_favorite_border, 0, 0
            )
            binding.btnFollow?.text = "Theo dõi"
            return
        }

        val isFav = favoriteDAO.isFavorite(userId, movieId)
        if (isFav) {
            binding.btnFollow?.setCompoundDrawablesWithIntrinsicBounds(
                0, R.drawable.ic_favorite, 0, 0
            )
            binding.btnFollow?.text = "Đang theo dõi"
        } else {
            binding.btnFollow?.setCompoundDrawablesWithIntrinsicBounds(
                0, R.drawable.ic_favorite_border, 0, 0
            )
            binding.btnFollow?.text = "Theo dõi"
        }
    }

    private fun getCurrentUserIdOrNull(): Int? {
        val sessionDAO = UserSessionDAO(DatabaseHelper(activity))
        val session = sessionDAO.getLatestValidSession() ?: return null

        val userDAO = UserDAO(DatabaseHelper(activity))
        val user = userDAO.getUserById(session.userId) ?: return null

        return user.id
    }

    // ----------------- PLAYER CONTROLS -----------------
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

    // Thêm hàm này vào trong class MovieDetailAdapter

    @androidx.annotation.OptIn(UnstableApi::class)
    private fun changeVideoQuality(quality: String) {
        val qualityHeight = quality.removeSuffix("p").toIntOrNull() ?: return
        player?.let { exoPlayer ->
            // Lấy track group của video
            val trackGroup = exoPlayer.currentTracks.groups.firstOrNull {
                it.type == C.TRACK_TYPE_VIDEO
            }?.mediaTrackGroup ?: return

            // Tìm index của track có độ phân giải phù hợp
            var trackIndex = -1
            for (i in 0 until trackGroup.length) {
                val format = trackGroup.getFormat(i)
                if (format.height == qualityHeight) {
                    trackIndex = i
                    break
                }
            }

            if (trackIndex != -1) {
                // =================== PHẦN THAY ĐỔI ===================
                val override = TrackSelectionOverride(trackGroup, listOf(trackIndex))

                // Sử dụng clearOverridesOfType và addOverride
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .clearOverridesOfType(C.TRACK_TYPE_VIDEO) // 1. Xóa các lựa chọn chất lượng video cũ
                    .addOverride(override)                   // 2. Thêm lựa chọn chất lượng mới
                    .build()

                CustomToast.show(activity, "Đã đổi chất lượng sang $quality", ToastType.SUCCESS)
            } else {
                CustomToast.show(activity, "Không tìm thấy chất lượng $quality", ToastType.ERROR)
            }
        }
    }


    // ----------------- FULLSCREEN -----------------
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

    // ----------------- TABS -----------------
    private fun setupTabs() {
        val tabs = listOf(binding.tabEpisodes, binding.tabType)
        for (tab in tabs) {
            tab?.setOnClickListener {
                highlightTab(tab)
                showControlsTemporarily()
                when (tab.id) {
                    R.id.tabEpisodes -> {
                        Toast.makeText(activity, "Danh sách tập", Toast.LENGTH_SHORT).show()
                        binding.episodesRecyclerView.visibility = View.VISIBLE
                        binding.gridViewTypeMovie.visibility = View.GONE
                    }
                    R.id.tabType -> {
                        Toast.makeText(activity, "Danh sách liên quan", Toast.LENGTH_SHORT).show()
                        binding.episodesRecyclerView.visibility = View.GONE
                        binding.gridViewTypeMovie.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun highlightTab(selectedTab: TextView) {
        val tabs = listOf(binding.tabEpisodes, binding.tabType)
        for (tab in tabs) {
            if (tab == selectedTab) {
                tab?.setTextAppearance(R.style.TabButton_Selected)
                tab?.setBackgroundResource(R.drawable.tab_selected_indicator)
            } else {
                tab?.setTextAppearance(R.style.TabButton)
                tab?.background = null // hoặc set lại background mặc định
            }
        }

        // Animation cho divider
        binding.dividerTabs?.animate()
            ?.x(selectedTab.x)
            ?.setDuration(200)
            ?.start()
    }


    // ----------------- PLAYER -----------------
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

    @UnstableApi
    private fun loadMovieDetailsAndPlay() {
        val movie = movieDAO.getMovieById(movieId)
        if (movie != null) {
            val episodes = episodeDAO.getEpisodesByMovieAsc(movieId)
            this.episodeList = episodes
            if (episodes.isNotEmpty()) {
                val totalEpisodes = episodes.size
                val releasedEpisodes = episodes.count { it.videoUrl.isNotEmpty() }
                setupMovieInfo(movie.name, movie.rating, movie.year ?: 0, movie.content, true, totalEpisodes, releasedEpisodes)
                displayEpisodeList()
            } else {
                setupMovieInfo(movie.name, movie.rating, movie.year ?: 0, movie.content, false)
            }
        } else {
            setupMovieInfo(movieName, 0.0, 0, "Đang cập nhật...", false)
        }
        loadEpisodeAndPlay()
    }

    @UnstableApi
    private fun displayEpisodeList() {
        if (episodeList.isNotEmpty()) {
            val userId = getCurrentUserIdOrNull()
            val wp = userId?.let { watchProgressDAO.getLatestWatchProgressForMovie(it, movieId) }

            // Xác định tập đang xem (nếu có), nếu chưa có thì lấy tập 1
            val lastWatchedEpisodeNumber = wp?.episodeId?.let { id ->
                episodeList.find { it.id == id }?.episodeNumber
            } ?: 1

            val episodeAdapter = EpisodeAdapter(
                episodes = episodeList,
                lastWatchedEpisodeNumber = lastWatchedEpisodeNumber
            ) { selectedEpisode ->
                currentEpisodeId = selectedEpisode.id
                savedPosition = 0 // Bắt đầu tập mới từ đầu
                playVideo(selectedEpisode.videoUrl)
                CustomToast.show(activity, "Đang phát tập ${selectedEpisode.episodeNumber}", ToastType.INFO)

                (binding.root as? NestedScrollView)?.smoothScrollTo(0, binding.playerContainer!!.top)
            }

            binding.episodesRecyclerView.apply {
                layoutManager = GridLayoutManager(activity, 4)
                adapter = episodeAdapter
                visibility = View.VISIBLE
            }
        }
    }

    private fun loadRelatedMovies() {
        val relatedMovies = movieDAO.getRelatedMovies(movieId)

        binding.gridViewTypeMovie.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = GenreRecyclerAdapter(relatedMovies) { movie ->
                val intent = Intent(activity, MovieDetailActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                intent.putExtra("movie_name", movie.name)
                activity.startActivity(intent)
            }

            // Thêm khoảng cách 16dp (chuyển về px)
            val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
            if (itemDecorationCount == 0) { // tránh add nhiều lần
                addItemDecoration(GridSpacingItemDecoration(2, spacing, true))
            }
        }
    }


    private fun setupMovieInfo(title: String, rating: Double, year: Int, content: String?, isSeries: Boolean, totalEpisodes: Int = 0, episodesReleased: Int = 0) {
        binding.txtMovieTitle?.text = title
        binding.txtMovieRating?.text = "⭐ $rating • "
        binding.txtMovieYear?.text = if (isSeries) "$year | $episodesReleased/$totalEpisodes tập" else "$year | single"
        binding.txtMovieInfo?.text = content ?: "Đang cập nhật..."
    }

    fun loadLatestComment(movieId: Int) {
        val db = dbHelper.readableDatabase
        val parentComments = commentDAO.getCommentsByMovieId(movieId)

        // Tính tổng số comment (cha + con)
        var totalCount = parentComments.size
        for (c in parentComments) {
            totalCount += commentDAO.getRepliesByParentId(c.id, db).size
        }

        // Cập nhật tổng số bình luận
        binding.txtCommentsTitle.text = "Bình luận ($totalCount)"

        if (parentComments.isNotEmpty()) {
            val latestComment = parentComments.first()

            binding.txtComment.text = latestComment.content

            if (latestComment.avatarPath.isNotEmpty()) {
                Glide.with(activity)
                    .load(latestComment.avatarPath)
                    .placeholder(R.drawable.ic_account_circle)
                    .into(binding.imgUserAvatar)
            } else {
                binding.imgUserAvatar.setImageResource(R.drawable.ic_account_circle)
            }
        } else {
            binding.txtComment.text = "Chưa có bình luận nào"
            binding.imgUserAvatar.setImageResource(R.drawable.ic_account_circle)
        }

        db.close() // đóng DB ở cuối
    }


    private fun loadEpisodeAndPlay() {
        val userId = getCurrentUserIdOrNull()
        val episodes = episodeDAO.getEpisodesByMovieAsc(movieId)

        if (episodes.isEmpty()) {
            // single movie
            val wp = userId?.let { watchProgressDAO.getWatchProgress(it, movieId, null) }
            currentEpisodeId = null
            savedPosition = (wp?.currentTime?.toLong() ?: 0) * 1000
            movieDAO.getMovieById(movieId)?.slug?.let { if (it.isNotEmpty()) playVideo(it) }
        } else {
            // series
            val wp = userId?.let { watchProgressDAO.getLatestWatchProgressForMovie(it, movieId) }
            val episodeToPlay = wp?.episodeId?.let { id -> episodes.find { it.id == id } } ?: episodes[0]
            currentEpisodeId = episodeToPlay.id
            savedPosition = (wp?.currentTime?.toLong() ?: 0) * 1000
            if (episodeToPlay.videoUrl.isNotEmpty()) playVideo(episodeToPlay.videoUrl)
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

    // ----------------- SEEK BAR -----------------
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

    // ----------------- CONTROLS -----------------
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

    // ----------------- SAVE PROGRESS -----------------
    private fun saveWatchProgress() {
        val userId = getCurrentUserIdOrNull() ?: return
        val duration = (player?.duration ?: 0L) / 1000L
        val current = (player?.currentPosition ?: 0L) / 1000L
        if (duration <= 0) return

        val wp = WatchProgress(
            userId = userId,
            movieId = movieId,
            episodeId = currentEpisodeId, // null nếu single
            currentTime = current.toInt(),
            totalTime = duration.toInt(),
            isCompleted = current >= duration * 0.95,
            lastWatchedAt = Date()
        )
        watchProgressDAO.upsertWatchProgress(wp)
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return if (hours > 0) "%d:%02d:%02d".format(hours, minutes % 60, seconds % 60)
        else "%d:%02d".format(minutes, seconds % 60)
    }
}
