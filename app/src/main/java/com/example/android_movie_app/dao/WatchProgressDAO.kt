package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.WatchProgress
import com.example.android_movie_app.ContinueWatchingItem
import com.example.android_movie_app.Movie
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

    /** Lấy danh sách tiến trình xem của user (chưa hoàn thành) */
    fun getContinueWatching(userId: Int): List<WatchProgress> {
        val list = mutableListOf<WatchProgress>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "watch_progress",
            null,
            "userId=? AND isCompleted=0",
            arrayOf(userId.toString()),
            null,
            null,
            "lastWatchedAt DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToWatchProgress(it))
            }
        }
        return list
    }

    /** Lấy danh sách tiến trình xem kèm thông tin phim */
    fun getContinueWatchingWithMovies(userId: Int): List<ContinueWatchingItem> {
        val list = mutableListOf<ContinueWatchingItem>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT wp.*, m.*, e.episodeNumber
            FROM watch_progress wp
            INNER JOIN movies m ON wp.movieId = m.id
            LEFT JOIN episodes e ON wp.episodeId = e.id
            WHERE wp.userId = ? AND wp.isCompleted = 0
            ORDER BY wp.lastWatchedAt DESC
            """.trimIndent(),
            arrayOf(userId.toString())
        )
        
        cursor.use {
            while (it.moveToNext()) {
                val progress = cursorToWatchProgress(it)
                val movie = cursorToMovie(it)
                val episodeNumber = if (!it.isNull(it.getColumnIndexOrThrow("episodeNumber"))) {
                    it.getInt(it.getColumnIndexOrThrow("episodeNumber"))
                } else null
                list.add(ContinueWatchingItem(progress, movie, episodeNumber))
            }
        }
        return list
    }

    /** Reset tiến trình xem của user cho episode cụ thể */
    fun resetWatchProgress(userId: Int, episodeId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            "watch_progress",
            "userId=? AND episodeId=?",
            arrayOf(userId.toString(), episodeId.toString())
        )
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

    /** Helper: chuyển Cursor -> Movie */
    private fun cursorToMovie(it: Cursor): Movie {
        return Movie(
            id = it.getInt(it.getColumnIndexOrThrow("id")),
            slug = it.getString(it.getColumnIndexOrThrow("slug")),
            name = it.getString(it.getColumnIndexOrThrow("name")),
            originName = it.getString(it.getColumnIndexOrThrow("originName")),
            content = it.getString(it.getColumnIndexOrThrow("content")),
            type = it.getString(it.getColumnIndexOrThrow("type")),
            thumbUrl = it.getString(it.getColumnIndexOrThrow("thumbUrl")),
            posterUrl = it.getString(it.getColumnIndexOrThrow("posterUrl")),
            year = if (!it.isNull(it.getColumnIndexOrThrow("year"))) 
                it.getInt(it.getColumnIndexOrThrow("year")) else null,
            viewCount = it.getInt(it.getColumnIndexOrThrow("viewCount")),
            rating = if (!it.isNull(it.getColumnIndexOrThrow("rating"))) 
                it.getDouble(it.getColumnIndexOrThrow("rating")) else 0.0,
            createdAt = it.getString(it.getColumnIndexOrThrow("createdAt"))?.let { d ->
                dateFormat.parse(d)
            }
        )
    }
}
