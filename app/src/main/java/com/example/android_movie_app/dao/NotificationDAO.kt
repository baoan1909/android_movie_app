package com.example.android_movie_app.dao
import android.content.ContentValues
import android.database.Cursor
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.Notifications
import java.text.SimpleDateFormat
import java.util.*

class NotificationDAO(private val dbHelper: DatabaseHelper) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    // Thêm thông báo mới
    fun insertNotification(notification: Notifications): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", notification.title)
            put("content", notification.content)
            put("createdAt", notification.createdAt?.let { dateFormat.format(it) })
            put("type", notification.type)
            notification.userId?.let { put("userId", it) }
        }
        return db.insert("notifications", null, values)
    }

    // Lấy thông báo cho 1 user cụ thể

    fun getSystemNotifications(): List<Notifications> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM notifications WHERE type='ht' ORDER BY createdAt DESC", null
        )
        return cursorToList(cursor)
    }

    fun getUserNotifications(userId: Int): List<Notifications> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM notifications WHERE type='cn' AND userId=? ORDER BY createdAt DESC",
            arrayOf(userId.toString())
        )
        return cursorToList(cursor)
    }
    private fun cursorToList(cursor: Cursor): List<Notifications> {
        val list = mutableListOf<Notifications>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("notificationId"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
                val createdAtStr = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))
                val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))

                // userId có thể NULL
                val userIdIndex = cursor.getColumnIndex("userId")
                val userId = if (userIdIndex != -1 && !cursor.isNull(userIdIndex)) {
                    cursor.getInt(userIdIndex)
                } else null

                val createdAt = createdAtStr?.let { dateFormat.parse(it) }

                list.add(
                    Notifications(id, title, content, createdAt, type, userId)
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }



    // Xóa thông báo theo ID
    fun deleteNotification(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("notifications", "notificationId=?", arrayOf(id.toString()))
    }

    // Xóa tất cả thông báo
    fun clearAll(): Int {
        val db = dbHelper.writableDatabase
        return db.delete("notifications", null, null)
    }
}