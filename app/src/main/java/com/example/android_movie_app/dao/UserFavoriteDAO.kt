package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.UserFavorite
import java.text.SimpleDateFormat
import java.util.Locale

class UserFavoriteDAO(private val dbHelper: DatabaseHelper) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // Thêm phim vào danh sách yêu thích
    fun addFavorite(userId: Int, movieId: Int): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userId", userId)
            put("movieId", movieId)
        }

        return try {
            val result = db.insert("user_favorites", null, values)
            result != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    // Xóa phim khỏi danh sách yêu thích
    fun removeFavorite(userId: Int, movieId: Int): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        return try {
            val rows = db.delete(
                "user_favorites",
                "userId=? AND movieId=?",
                arrayOf(userId.toString(), movieId.toString())
            )
            rows > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    // Kiểm tra xem user có follow phim này chưa
    fun isFavorite(userId: Int, movieId: Int): Boolean {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        return try {
            val cursor = db.rawQuery(
                "SELECT 1 FROM user_favorites WHERE userId=? AND movieId=? LIMIT 1",
                arrayOf(userId.toString(), movieId.toString())
            )
            val exists = cursor.moveToFirst()
            cursor.close()
            exists
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }
}
