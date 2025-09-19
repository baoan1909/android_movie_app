package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.WatchProgress
import java.text.SimpleDateFormat
import java.util.*

class WatchProgressDAO(private val dbHelper: DatabaseHelper) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    /** Thêm mới hoặc cập nhật tiến trình xem */
    fun upsertWatchProgress(wp: WatchProgress): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userId", wp.userId)
            put("movieId", wp.movieId)
            put("episodeId", wp.episodeId)
            put("currentTime", wp.currentTime)
            put("totalTime", wp.totalTime)
            put("isCompleted", if (wp.isCompleted) 1 else 0)
            put("lastWatchedAt", wp.lastWatchedAt?.let { dateFormat.format(it) })
        }
        return db.insertWithOnConflict(
            "watch_progress",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    /** Lấy tiến trình xem theo movie + episode */
    fun getWatchProgress(userId: Int, movieId: Int, episodeId: Int? = null): WatchProgress? {
        val db = dbHelper.readableDatabase
        val query = if (episodeId != null) {
            "SELECT * FROM watch_progress WHERE userId=? AND movieId=? AND episodeId=?"
        } else {
            "SELECT * FROM watch_progress WHERE userId=? AND movieId=?"
        }
        val args = if (episodeId != null)
            arrayOf(userId.toString(), movieId.toString(), episodeId.toString())
        else arrayOf(userId.toString(), movieId.toString())

        val cursor = db.rawQuery(query, args)
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToWatchProgress(it)
            }
        }
        return null
    }

    /** Lấy tiến trình xem theo episode */
    fun getWatchProgress(userId: Int, episodeId: Int): WatchProgress? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM watch_progress WHERE userId=? AND episodeId=? LIMIT 1",
            arrayOf(userId.toString(), episodeId.toString())
        )
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToWatchProgress(it)
            }
        }
        return null
    }

    /** Lấy tiến trình xem mới nhất của 1 phim (cho cả single và series) */
    fun getLatestWatchProgressForMovie(userId: Int, movieId: Int): WatchProgress? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "watch_progress", // TABLE_NAME
            null,
            "userId=? AND movieId=?",
            arrayOf(userId.toString(), movieId.toString()),
            null,
            null,
            "lastWatchedAt DESC", // sắp xếp mới nhất
            "1" // chỉ lấy 1 dòng
        )
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToWatchProgress(it)
            }
        }
        return null
    }

    /** Helper: chuyển Cursor -> WatchProgress */
    private fun cursorToWatchProgress(it: Cursor): WatchProgress {
        return WatchProgress(
            userId = it.getInt(it.getColumnIndexOrThrow("userId")),
            movieId = it.getInt(it.getColumnIndexOrThrow("movieId")),
            episodeId = if (!it.isNull(it.getColumnIndexOrThrow("episodeId")))
                it.getInt(it.getColumnIndexOrThrow("episodeId")) else null,
            currentTime = it.getInt(it.getColumnIndexOrThrow("currentTime")),
            totalTime = it.getInt(it.getColumnIndexOrThrow("totalTime")),
            isCompleted = it.getInt(it.getColumnIndexOrThrow("isCompleted")) == 1,
            lastWatchedAt = it.getString(it.getColumnIndexOrThrow("lastWatchedAt"))?.let { d ->
                dateFormat.parse(d)
            }
        )
    }
}
