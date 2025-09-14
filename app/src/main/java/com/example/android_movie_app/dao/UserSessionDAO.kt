package com.example.android_movie_app.dao

import android.content.ContentValues
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.UserSession
import java.text.SimpleDateFormat
import java.util.*

class UserSessionDAO(private val dbHelper: DatabaseHelper) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    // ---------- ADD SESSION ----------
    fun addSession(session: UserSession): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("sessionToken", session.sessionToken)
            put("userId", session.userId)
            put("expiresAt", dateFormat.format(session.expiresAt))
        }
        return db.insert("user_sessions", null, values)
    }

    // ---------- GET SESSION BY TOKEN ----------
    fun getSession(token: String): UserSession? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM user_sessions WHERE sessionToken=?", arrayOf(token))
        cursor.use {
            if (it.moveToFirst()) {
                return UserSession(
                    sessionToken = it.getString(it.getColumnIndexOrThrow("sessionToken")),
                    userId = it.getInt(it.getColumnIndexOrThrow("userId")),
                    expiresAt = dateFormat.parse(it.getString(it.getColumnIndexOrThrow("expiresAt")))!!
                )
            }
        }
        return null
    }

    // ---------- GET ALL SESSIONS OF USER ----------
    fun getSessionsByUser(userId: Int): List<UserSession> {
        val sessions = mutableListOf<UserSession>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM user_sessions WHERE userId=?", arrayOf(userId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                sessions.add(
                    UserSession(
                        sessionToken = it.getString(it.getColumnIndexOrThrow("sessionToken")),
                        userId = it.getInt(it.getColumnIndexOrThrow("userId")),
                        expiresAt = dateFormat.parse(it.getString(it.getColumnIndexOrThrow("expiresAt")))!!
                    )
                )
            }
        }
        return sessions
    }

    // ---------- DELETE SESSION ----------
    fun deleteSession(token: String): Int {
        val db = dbHelper.writableDatabase
        return db.delete("user_sessions", "sessionToken=?", arrayOf(token))
    }

    // ---------- DELETE ALL EXPIRED SESSIONS ----------
    fun deleteExpiredSessions(): Int {
        val db = dbHelper.writableDatabase
        val now = dateFormat.format(Date())
        return db.delete("user_sessions", "expiresAt < ?", arrayOf(now))
    }
}
