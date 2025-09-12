package com.example.android_movie_app

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
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

    /** Cập nhật tiến trình xem (resume point) */
    fun updateWatchProgress(progress: WatchProgress): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("movieId", progress.movieId)
            put("episodeId", progress.episodeId)
            put("currentTime", progress.currentTime)
            put("totalTime", progress.totalTime)
            put("isCompleted", if (progress.isCompleted) 1 else 0)
            put("lastWatchedAt", progress.lastWatchedAt?.let { dateFormat.format(it) })
        }
        val rows = db.update(
            "watch_progress",
            values,
            "userId=? AND episodeId=?",
            arrayOf(progress.userId.toString(), progress.episodeId.toString())
        )
        if (rows == 0) {
            values.put("userId", progress.userId)
            return db.insert("watch_progress", null, values)
        }
        return rows.toLong()
    }

    /** Đánh dấu 1 tập đã xem xong */
    fun markEpisodeCompleted(userId: Int, episodeId: Int): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("isCompleted", 1)
            put("lastWatchedAt", dateFormat.format(Date()))
        }
        return db.update(
            "watch_progress",
            values,
            "userId=? AND episodeId=?",
            arrayOf(userId.toString(), episodeId.toString())
        )
    }

    /** Danh sách phim đang xem dở (chưa completed) */
    fun getContinueWatching(userId: Int): List<WatchProgress> {
        val list = mutableListOf<WatchProgress>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM watch_progress WHERE userId=? AND isCompleted=0 ORDER BY lastWatchedAt DESC",
            arrayOf(userId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToWatchProgress(it))
            }
        }
        return list
    }

    /** Lịch sử xem phim (bao gồm cả completed) */
    fun getWatchHistory(userId: Int): List<WatchProgress> {
        val list = mutableListOf<WatchProgress>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM watch_progress WHERE userId=? ORDER BY lastWatchedAt DESC",
            arrayOf(userId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToWatchProgress(it))
            }
        }
        return list
    }

    /** Xóa/reset tiến trình xem */
    fun resetWatchProgress(userId: Int, episodeId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            "watch_progress",
            "userId=? AND episodeId=?",
            arrayOf(userId.toString(), episodeId.toString())
        )
    }

    /** Lấy danh sách continue watching kèm Movie */
    fun getContinueWatchingWithMovies(userId: Int): List<ContinueWatchingItem> {
        val list = mutableListOf<ContinueWatchingItem>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT wp.*, m.id AS movieId, m.slug, m.name, m.originName, m.content,
                   m.type, m.thumbUrl, m.posterUrl, m.year, m.viewCount, m.rating, m.createdAt AS movieCreatedAt
            FROM watch_progress wp
            INNER JOIN movies m ON wp.movieId = m.id
            WHERE wp.userId=? AND wp.isCompleted=0
            ORDER BY wp.lastWatchedAt DESC
        """
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                val progress = cursorToWatchProgress(it)
                val movie = dbHelper.cursorToMovie(it)
                list.add(ContinueWatchingItem(progress, movie))
            }
        }
        return list
    }

    /** Helper: chuyển Cursor -> WatchProgress */
    private fun cursorToWatchProgress(it: android.database.Cursor): WatchProgress {
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
