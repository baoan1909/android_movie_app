package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.Movie
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

    // Lấy danh sách phim yêu thích của user
    fun getFavoritesByUserId(userId: Int): List<Movie> {
        val db = dbHelper.readableDatabase
        val movies = mutableListOf<Movie>()

        val query = """
        SELECT m.id, m.slug, m.name, m.originName, m.content, m.type,
               m.thumbUrl, m.posterUrl, m.year, m.viewCount, m.rating, m.createdAt
        FROM user_favorites uf
        INNER JOIN movies m ON uf.movieId = m.id
        WHERE uf.userId = ?
        ORDER BY uf.createdAt DESC
    """

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val movie = Movie(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    slug = cursor.getString(cursor.getColumnIndexOrThrow("slug")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    originName = cursor.getString(cursor.getColumnIndexOrThrow("originName")),
                    content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
                    type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                    thumbUrl = cursor.getString(cursor.getColumnIndexOrThrow("thumbUrl")),
                    posterUrl = cursor.getString(cursor.getColumnIndexOrThrow("posterUrl")),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow("year")),
                    viewCount = cursor.getInt(cursor.getColumnIndexOrThrow("viewCount")),
                    rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))?.let {
                        dateFormat.parse(it)
                    }
                )
                movies.add(movie)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return movies
    }
    fun clearAllFavorites(userId: Int) {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        db.delete(
            "user_favorites",
            "userId = ?",
            arrayOf(userId.toString())
        )
        db.close()
    }

}
