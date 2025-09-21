package com.example.android_movie_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_movie_app.adapter.FavoriteAdapter
import com.example.android_movie_app.dao.UserDAO
import com.example.android_movie_app.dao.UserFavoriteDAO
import com.example.android_movie_app.dao.UserSessionDAO

class FavoritesActivity : BaseActivity() {

    private lateinit var rvFavorites: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var btnClearAll: LinearLayout
    private lateinit var tvStats: TextView
    private lateinit var btnBrowseMovies: CardView

    private lateinit var adapter: FavoriteAdapter
    private lateinit var userFavoriteDAO: UserFavoriteDAO
    private var currentUserId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        // Ánh xạ view
        rvFavorites = findViewById(R.id.rv_favorites)
        layoutEmptyState = findViewById(R.id.layout_empty_state)
        btnClearAll = findViewById(R.id.btnClearAll)
        tvStats = findViewById(R.id.tvStats)
        btnBrowseMovies = findViewById(R.id.btnBrowseMovies)

        val dbHelper = DatabaseHelper(this)
        userFavoriteDAO = UserFavoriteDAO(dbHelper)

        currentUserId = getCurrentUserIdOrNull()

        rvFavorites.layoutManager = LinearLayoutManager(this)
        adapter = FavoriteAdapter(
            this,
            emptyList(),
            onItemClick = { movie ->
                val intent = Intent(this, MovieDetailActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                intent.putExtra("movie_name", movie.name)
                startActivity(intent)
            },
            onFavoriteClick = { movie ->
                currentUserId?.let { userId ->
                    userFavoriteDAO.removeFavorite(userId, movie.id)
                    loadFavorites()
                }
            }
        )
        rvFavorites.adapter = adapter

        setupListeners()
        loadFavorites()
    }

    private fun setupListeners() {
        // Nút "Xóa sạch"
        btnClearAll.setOnClickListener {
            currentUserId?.let { userId ->
                userFavoriteDAO.clearAllFavorites(userId)
                loadFavorites()
            }
        }

        // Nút "Khám phá phim" ở Empty State
        btnBrowseMovies.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadFavorites() {
        val userId = currentUserId
        if (userId == null) {
            rvFavorites.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
            tvStats.text = "Bạn chưa đăng nhập"
            return
        }

        val favorites = userFavoriteDAO.getFavoritesByUserId(userId)

        if (favorites.isEmpty()) {
            rvFavorites.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
            tvStats.text = "Bạn chưa có phim yêu thích nào"
        } else {
            rvFavorites.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
            adapter.updateData(favorites)
            tvStats.text = "Bạn có ${favorites.size} phim đang yêu thích"
        }
    }

    private fun getCurrentUserIdOrNull(): Int? {
        val sessionDAO = UserSessionDAO(DatabaseHelper(this))
        val session = sessionDAO.getLatestValidSession() ?: return null

        val userDAO = UserDAO(DatabaseHelper(this))
        val user = userDAO.getUserById(session.userId) ?: return null

        return user.id
    }
}
