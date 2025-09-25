package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.Review
import java.text.SimpleDateFormat
import java.util.*

class ReviewDAO(private val dbHelper: DatabaseHelper) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun getAverageRatingForMovie(movieId: Int): Double {
        val db = dbHelper.readableDatabase
        var averageRating = 0.0
        val cursor = db.rawQuery("SELECT AVG(rating) FROM reviews WHERE movieId = ?", arrayOf(movieId.toString()))

        try {
            if (cursor.moveToFirst()) {
                // Lấy giá trị từ cột đầu tiên (AVG(rating))
                averageRating = cursor.getDouble(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }
        return averageRating
    }
    fun insertOrUpdateReview(userId: Int, movieId: Int, episodeId: Int?, rating: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("userId", userId)
                put("movieId", movieId)
                put("episodeId", episodeId)
                put("rating", rating)
                put("updatedAt", System.currentTimeMillis().toString())
            }

            // Nếu đã tồn tại -> update
            val updated = db.update(
                "reviews",
                values,
                "userId=? AND movieId=? AND (episodeId IS ? OR episodeId=?)",
                arrayOf(userId.toString(), movieId.toString(), episodeId?.toString(), episodeId?.toString())
            )

            if (updated == 0) {
                // Chưa có -> insert
                db.insert("reviews", null, values) != -1L
            } else {
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

}
