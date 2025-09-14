package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.Review
import java.text.SimpleDateFormat
import java.util.*

class ReviewDAO(private val dbHelper: DatabaseHelper) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    /** Thêm review mới */
    fun addReview(review: Review): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userId", review.userId)
            put("movieId", review.movieId)
            put("episodeId", review.episodeId)
            put("rating", review.rating)
            put("createdAt", review.createdAt?.let { dateFormat.format(it) } ?: dateFormat.format(Date()))
        }
        return db.insertWithOnConflict("reviews", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    /** Cập nhật review đã tồn tại */
    fun updateReview(review: Review): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("rating", review.rating)
            put("updatedAt", dateFormat.format(Date()))
        }
        return db.update(
            "reviews",
            values,
            "userId=? AND movieId=? AND (episodeId=? OR (episodeId IS NULL AND ? IS NULL))",
            arrayOf(
                review.userId.toString(),
                review.movieId.toString(),
                review.episodeId?.toString() ?: "",
                review.episodeId?.toString() ?: ""
            )
        )
    }

    /** Xóa review */
    fun deleteReview(reviewId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("reviews", "id=?", arrayOf(reviewId.toString()))
    }

    /** Lấy review theo user + movie + optional episode */
    fun getReview(userId: Int, movieId: Int, episodeId: Int? = null): Review? {
        val db = dbHelper.readableDatabase
        val query = if (episodeId != null) {
            "SELECT * FROM reviews WHERE userId=? AND movieId=? AND episodeId=?"
        } else {
            "SELECT * FROM reviews WHERE userId=? AND movieId=? AND episodeId IS NULL"
        }
        val args = if (episodeId != null) arrayOf(userId.toString(), movieId.toString(), episodeId.toString())
        else arrayOf(userId.toString(), movieId.toString())

        val cursor = db.rawQuery(query, args)
        cursor.use {
            if (it.moveToFirst()) {
                return Review(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    userId = it.getInt(it.getColumnIndexOrThrow("userId")),
                    movieId = it.getInt(it.getColumnIndexOrThrow("movieId")),
                    episodeId = if (!it.isNull(it.getColumnIndexOrThrow("episodeId"))) it.getInt(
                        it.getColumnIndexOrThrow(
                            "episodeId"
                        )
                    ) else null,
                    rating = it.getInt(it.getColumnIndexOrThrow("rating")),
                    createdAt = it.getString(it.getColumnIndexOrThrow("createdAt"))
                        ?.let { d -> dateFormat.parse(d) },
                    updatedAt = it.getString(it.getColumnIndexOrThrow("updatedAt"))
                        ?.let { d -> dateFormat.parse(d) }
                )
            }
        }
        return null
    }

    /** Lấy tất cả review của một movie */
    fun getReviewsByMovie(movieId: Int): List<Review> {
        val list = mutableListOf<Review>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM reviews WHERE movieId=? ORDER BY createdAt DESC",
            arrayOf(movieId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Review(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        userId = it.getInt(it.getColumnIndexOrThrow("userId")),
                        movieId = it.getInt(it.getColumnIndexOrThrow("movieId")),
                        episodeId = if (!it.isNull(it.getColumnIndexOrThrow("episodeId"))) it.getInt(
                            it.getColumnIndexOrThrow("episodeId")
                        ) else null,
                        rating = it.getInt(it.getColumnIndexOrThrow("rating")),
                        createdAt = it.getString(it.getColumnIndexOrThrow("createdAt"))
                            ?.let { d -> dateFormat.parse(d) },
                        updatedAt = it.getString(it.getColumnIndexOrThrow("updatedAt"))
                            ?.let { d -> dateFormat.parse(d) }
                    )
                )
            }
        }
        return list
    }

    /** Tính điểm trung bình của một movie */
    fun getAverageRating(movieId: Int): Double {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT AVG(rating) AS avgRating FROM reviews WHERE movieId=?",
            arrayOf(movieId.toString())
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getDouble(it.getColumnIndexOrThrow("avgRating"))
            }
        }
        return 0.0
    }
}
