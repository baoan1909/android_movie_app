package com.example.android_movie_app

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_movie_app.adapter.NotificationAdapter
import com.example.android_movie_app.dao.NotificationDAO

class NotificationsActivity : BaseActivity() {

    private lateinit var notificationDAO: NotificationDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // init DAO
        val dbHelper = DatabaseHelper(this)
        notificationDAO = NotificationDAO(dbHelper)

        val recyclerView = findViewById<RecyclerView>(R.id.rvNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Lấy dữ liệu từ DB
        val notifications: List<Notifications> = notificationDAO.getAllNotifications()

        // Set adapter
        recyclerView.adapter = NotificationAdapter(notifications)
    }
}
