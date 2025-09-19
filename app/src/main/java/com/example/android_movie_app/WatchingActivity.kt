package com.example.android_movie_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_movie_app.adapter.WatchingProgressAdapter
import com.example.android_movie_app.dao.MovieDAO
import com.example.android_movie_app.dao.WatchProgressDAO
import kotlinx.coroutines.*

class WatchingActivity : BaseActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnClearAll: ImageView
    private lateinit var tvStats: TextView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var layoutLoading: LinearLayout
    private lateinit var recyclerViewWatching: RecyclerView
    private lateinit var btnBrowseMovies: CardView
    
    private lateinit var watchingAdapter: WatchingProgressAdapter
    private lateinit var watchProgressDAO: WatchProgressDAO
    private lateinit var movieDAO: MovieDAO
    private lateinit var sessionManager: SessionManager
    
    private var userId: Int = -1
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watching)
        
        initViews()
        initDatabase()
        setupRecyclerView()
        setupListeners()
        loadWatchingData()
    }
    
    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnClearAll = findViewById(R.id.btnClearAll)
        tvStats = findViewById(R.id.tvStats)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        layoutLoading = findViewById(R.id.layoutLoading)
        recyclerViewWatching = findViewById(R.id.recyclerViewWatching)
        btnBrowseMovies = findViewById(R.id.btnBrowseMovies)
    }
    
    private fun initDatabase() {
        val dbHelper = DatabaseHelper(this)
        watchProgressDAO = WatchProgressDAO(dbHelper)
        movieDAO = MovieDAO(dbHelper)
        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()
    }
    
    private fun setupRecyclerView() {
        watchingAdapter = WatchingProgressAdapter(
            context = this,
            watchingList = mutableListOf(),
            onRemoveClick = { item ->
                removeWatchingItem(item)
            }
        )
        
        recyclerViewWatching.apply {
            layoutManager = LinearLayoutManager(this@WatchingActivity)
            adapter = watchingAdapter
        }
    }
    
    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }
        
        btnClearAll.setOnClickListener {
            clearAllWatchingProgress()
        }
        
        btnBrowseMovies.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    private fun loadWatchingData() {
        if (userId == -1) {
            showEmptyState()
            return
        }
        
        showLoading()
        
        scope.launch {
            try {
                val watchingItems = withContext(Dispatchers.IO) {
                    watchProgressDAO.getContinueWatchingWithMovies(userId)
                }
                
                updateUI(watchingItems)
                
            } catch (e: Exception) {
                e.printStackTrace()
                showEmptyState()
                CustomToast.show(this@WatchingActivity, "Có lỗi xảy ra khi tải dữ liệu", ToastType.ERROR)
            }
        }
    }
    
    private fun updateUI(watchingItems: List<ContinueWatchingItem>) {
        hideLoading()
        
        if (watchingItems.isEmpty()) {
            showEmptyState()
        } else {
            showWatchingList(watchingItems)
        }
        
        updateStats(watchingItems.size)
    }
    
    private fun showLoading() {
        layoutLoading.visibility = View.VISIBLE
        layoutEmptyState.visibility = View.GONE
        recyclerViewWatching.visibility = View.GONE
    }
    
    private fun hideLoading() {
        layoutLoading.visibility = View.GONE
    }
    
    private fun showEmptyState() {
        layoutEmptyState.visibility = View.VISIBLE
        recyclerViewWatching.visibility = View.GONE
        btnClearAll.visibility = View.GONE
    }
    
    private fun showWatchingList(watchingItems: List<ContinueWatchingItem>) {
        layoutEmptyState.visibility = View.GONE
        recyclerViewWatching.visibility = View.VISIBLE
        btnClearAll.visibility = View.VISIBLE
        
        watchingAdapter.updateList(watchingItems)
    }
    
    private fun updateStats(count: Int) {
        tvStats.text = if (count == 0) {
            "Bạn chưa có phim nào đang xem dở"
        } else {
            "Bạn có $count phim đang xem dở"
        }
    }
    
    private fun removeWatchingItem(item: ContinueWatchingItem) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Remove from database
                    item.progress.episodeId?.let { episodeId ->
                        watchProgressDAO.resetWatchProgress(userId, episodeId)
                    }
                }
                
                // Update UI
                watchingAdapter.removeItem(item)
                updateStats(watchingAdapter.itemCount)
                
                if (watchingAdapter.itemCount == 0) {
                    showEmptyState()
                }
                
                CustomToast.show(this@WatchingActivity, "Đã xóa khỏi danh sách theo dõi", ToastType.SUCCESS)
                
            } catch (e: Exception) {
                e.printStackTrace()
                CustomToast.show(this@WatchingActivity, "Có lỗi xảy ra khi xóa", ToastType.ERROR)
            }
        }
    }
    
    private fun clearAllWatchingProgress() {
        if (userId == -1) return
        
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Get all current watching items
                    val watchingItems = watchProgressDAO.getContinueWatching(userId)
                    
                    // Remove all from database
                    watchingItems.forEach { progress ->
                        progress.episodeId?.let { episodeId ->
                            watchProgressDAO.resetWatchProgress(userId, episodeId)
                        }
                    }
                }
                
                // Update UI
                watchingAdapter.updateList(emptyList())
                showEmptyState()
                updateStats(0)
                
                CustomToast.show(this@WatchingActivity, "Đã xóa tất cả tiến độ xem phim", ToastType.SUCCESS)
                
            } catch (e: Exception) {
                e.printStackTrace()
                CustomToast.show(this@WatchingActivity, "Có lỗi xảy ra khi xóa", ToastType.ERROR)
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Reload data when coming back to this activity
        loadWatchingData()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}