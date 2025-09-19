package com.example.android_movie_app

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_movie_app.adapter.NotificationAdapter
import com.example.android_movie_app.dao.NotificationDAO
import com.example.android_movie_app.dao.UserDAO
import com.example.android_movie_app.dao.UserSessionDAO
import com.google.android.material.tabs.TabLayout

class NotificationsActivity : BaseActivity() {

    private lateinit var notificationDAO: NotificationDAO
    private lateinit var sessionDAO: UserSessionDAO
    private lateinit var userDAO: UserDAO

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private var currentUserId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        recyclerView = findViewById(R.id.rvNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // init DAO
        val dbHelper = DatabaseHelper(this)
        notificationDAO = NotificationDAO(dbHelper)
        sessionDAO = UserSessionDAO(dbHelper)
        userDAO = UserDAO(dbHelper)

        // Lấy session hiện tại
        val latestValidSession = sessionDAO.getLatestValidSession()
        if (latestValidSession != null) {
            val user = userDAO.getUserById(latestValidSession.userId)
            if (user != null) {
                currentUserId = user.id
                // mặc định load "Hệ thống"
                loadSystemNotifications()
            }
        } else {
            CustomToast.show(this, "Bạn chưa đăng nhập", ToastType.ERROR)
        }

        // TabLayout filter
        val tabLayout = findViewById<TabLayout>(R.id.tabLayoutFilters)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadSystemNotifications()   // Hệ thống
                    1 -> loadUserNotifications()     // Cá nhân
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadSystemNotifications() {
        val system = notificationDAO.getSystemNotifications()
        adapter = NotificationAdapter(system)
        recyclerView.adapter = adapter
    }

    private fun loadUserNotifications() {
        val uid = currentUserId ?: return
        val personal = notificationDAO.getUserNotifications(uid)
        adapter = NotificationAdapter(personal)
        recyclerView.adapter = adapter
    }
}
