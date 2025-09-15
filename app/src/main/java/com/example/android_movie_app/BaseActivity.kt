package com.example.android_movie_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {

    override fun setContentView(layoutResID: Int) {
        // Inflate layout cha (chứa bottom nav)
        val fullView = layoutInflater.inflate(R.layout.activity_base, null)
        val container = fullView.findViewById<FrameLayout>(R.id.container)

        // Inflate layout con vào container
        layoutInflater.inflate(layoutResID, container, true)

        // Gọi super.setContentView 1 lần duy nhất
        super.setContentView(fullView)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set item được chọn theo Activity hiện tại
        when (this) {
            is MainActivity -> bottomNav.selectedItemId = R.id.navigation_home
            is FavoritesActivity -> bottomNav.selectedItemId = R.id.navigation_favorites
            is WatchingActivity -> bottomNav.selectedItemId = R.id.navigation_watching
            is NotificationsActivity -> bottomNav.selectedItemId = R.id.navigation_notifications
            is ProfileActivity -> bottomNav.selectedItemId = R.id.navigation_profile
        }
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (this !is MainActivity) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navigation_favorites -> {
                    if (this !is FavoritesActivity) {
                        startActivity(Intent(this, FavoritesActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navigation_watching -> {
                    if (this !is WatchingActivity) {
                        startActivity(Intent(this, WatchingActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navigation_notifications -> {
                    if (this !is NotificationsActivity) {
                        startActivity(Intent(this, NotificationsActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navigation_profile -> {
                    if (this !is ProfileActivity) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}

