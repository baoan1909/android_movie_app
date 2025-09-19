package com.example.android_movie_app.dao

import android.content.ContentValues
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.Episode
import com.example.android_movie_app.WatchProgress
import java.text.SimpleDateFormat
import java.util.*

class EpisodeDAO(private val dbHelper: DatabaseHelper) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // ---------------- CRUD Episodes ----------------
    fun addEpisode(ep: Episode): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("movieId", ep.movieId)
            put("name", ep.name)
            put("episodeNumber", ep.episodeNumber)
            put("videoUrl", ep.videoUrl)
            put("duration", ep.duration)
            put("createdAt", ep.createdAt?.let { dateFormat.format(it) })
        }
        return db.insert("episodes", null, values)
    }

    fun getEpisodesByMovie(movieId: Int): List<Episode> {
        val list = mutableListOf<Episode>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM episodes WHERE movieId=? ORDER BY episodeNumber ASC",
            arrayOf(movieId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val ep = Episode(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    movieId = it.getInt(it.getColumnIndexOrThrow("movieId")),
                    name = it.getString(it.getColumnIndexOrThrow("name")),
                    episodeNumber = it.getInt(it.getColumnIndexOrThrow("episodeNumber")),
                    videoUrl = it.getString(it.getColumnIndexOrThrow("videoUrl")),
                    duration = if (!it.isNull(it.getColumnIndexOrThrow("duration"))) it.getInt(
                        it.getColumnIndexOrThrow(
                            "duration"
                        )
                    ) else null,
                    createdAt = it.getString(it.getColumnIndexOrThrow("createdAt"))
                        ?.let { d -> dateFormat.parse(d) }
                )
                list.add(ep)
            }
        }
        return list
    }

    fun getEpisodeCountByMovieId(movieId: Int): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM episodes WHERE movieId = ?",
            arrayOf(movieId.toString())
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }


    fun updateEpisode(ep: Episode): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", ep.name)
            put("episodeNumber", ep.episodeNumber)
            put("videoUrl", ep.videoUrl)
            put("duration", ep.duration)
            put("createdAt", ep.createdAt?.let { dateFormat.format(it) })
        }
        return db.update("episodes", values, "id=?", arrayOf(ep.id.toString()))
    }

    fun deleteEpisode(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("episodes", "id=?", arrayOf(id.toString()))
    }

    // 1. Lấy danh sách tập theo movieId (sắp xếp tập 1 -> N)
    fun getEpisodesByMovieAsc(movieId: Int): List<Episode> {
        val list = mutableListOf<Episode>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM episodes WHERE movieId=? ORDER BY episodeNumber ASC",
            arrayOf(movieId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val ep = Episode(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    movieId = it.getInt(it.getColumnIndexOrThrow("movieId")),
                    name = it.getString(it.getColumnIndexOrThrow("name")),
                    episodeNumber = it.getInt(it.getColumnIndexOrThrow("episodeNumber")),
                    videoUrl = it.getString(it.getColumnIndexOrThrow("videoUrl")),
                    duration = if (!it.isNull(it.getColumnIndexOrThrow("duration"))) it.getInt(
                        it.getColumnIndexOrThrow(
                            "duration"
                        )
                    ) else null,
                    createdAt = it.getString(it.getColumnIndexOrThrow("createdAt"))
                        ?.let { d -> dateFormat.parse(d) }
                )
                list.add(ep)
            }
        }
        return list
    }

    // 2. Lấy danh sách tập kèm tiến độ xem (join với WatchProgress)
    fun getEpisodesWithProgress(movieId: Int, userId: Int): List<Pair<Episode, WatchProgress?>> {
        val result = mutableListOf<Pair<Episode, WatchProgress?>>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT e.*, w.currentTime, w.totalTime, w.isCompleted, w.lastWatchedAt
            FROM episodes e
            LEFT JOIN watch_progress w 
            ON e.id = w.episodeId AND w.userId = ?
            WHERE e.movieId = ?
            ORDER BY e.episodeNumber ASC
            """.trimIndent(),
            arrayOf(userId.toString(), movieId.toString())
        )

        cursor.use {
            while (it.moveToNext()) {
                val ep = Episode(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    movieId = it.getInt(it.getColumnIndexOrThrow("movieId")),
                    name = it.getString(it.getColumnIndexOrThrow("name")),
                    episodeNumber = it.getInt(it.getColumnIndexOrThrow("episodeNumber")),
                    videoUrl = it.getString(it.getColumnIndexOrThrow("videoUrl")),
                    duration = if (!it.isNull(it.getColumnIndexOrThrow("duration"))) it.getInt(
                        it.getColumnIndexOrThrow(
                            "duration"
                        )
                    ) else null,
                    createdAt = it.getString(it.getColumnIndexOrThrow("createdAt"))
                        ?.let { d -> dateFormat.parse(d) }
                )

                val wp = if (!it.isNull(it.getColumnIndexOrThrow("currentTime"))) {
                    WatchProgress(
                        userId = userId,
                        movieId = movieId,
                        episodeId = ep.id,
                        currentTime = it.getInt(it.getColumnIndexOrThrow("currentTime")),
                        totalTime = it.getInt(it.getColumnIndexOrThrow("totalTime")),
                        isCompleted = it.getInt(it.getColumnIndexOrThrow("isCompleted")) == 1,
                        lastWatchedAt = it.getString(it.getColumnIndexOrThrow("lastWatchedAt"))
                            ?.let { d -> dateFormat.parse(d) }
                    )
                } else null

                result.add(ep to wp)
            }
        }
        return result
    }
}
