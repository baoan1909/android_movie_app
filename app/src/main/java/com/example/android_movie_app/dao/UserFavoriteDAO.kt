package com.example.android_movie_app.dao

import android.content.ContentValues
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.UserFavorite
import java.text.SimpleDateFormat
import java.util.Locale

class UserFavoriteDAO(private val dbHelper: DatabaseHelper) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    // Thêm yêu thích
    fun addFavorite(fav: UserFavorite): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userId", fav.userId)
            put("movieId", fav.movieId)
            put("createdAt", fav.createdAt?.let { dateFormat.format(it) })
        }
        return db.insert("user_favorites", null, values)
    }

    // Lấy danh sách user_favorites theo userId
    fun getFavoritesByUser(userId: Int): List<UserFavorite> {
        val list = mutableListOf<UserFavorite>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM user_favorites WHERE userId=?",
            arrayOf(userId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    UserFavorite(
                        userId = it.getInt(it.getColumnIndexOrThrow("userId")),
                        movieId = it.getInt(it.getColumnIndexOrThrow("movieId")),
                        createdAt = it.getString(it.getColumnIndexOrThrow("createdAt"))?.let { d ->
                            dateFormat.parse(d)
                        }
                    )
                )
            }
        }
        return list
    }

    // Kiểm tra phim đã yêu thích chưa
    fun isMovieFavorited(userId: Int, movieId: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM user_favorites WHERE userId=? AND movieId=? LIMIT 1",
            arrayOf(userId.toString(), movieId.toString())
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Lấy danh sách phim yêu thích (JOIN với movies)
//    fun getFavoriteMovies(userId: Int): List<Movie> {
//        val list = mutableListOf<Movie>()
//        val db = dbHelper.readableDatabase
//        val query = """
//            SELECT m.* FROM user_favorites uf
//            INNER JOIN movies m ON uf.movieId = m.id
//            WHERE uf.userId=?
//            ORDER BY uf.createdAt DESC
//        """
//        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
//        cursor.use {
//            while (it.moveToNext()) {
//                list.add(dbHelper.cursorToMovie(it))
//            }
//        }
//        return list
//    }

    // Đếm tổng số phim yêu thích
    fun countFavorites(userId: Int): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM user_favorites WHERE userId=?",
            arrayOf(userId.toString())
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    // Xóa yêu thích theo movieId
    fun removeFavorite(userId: Int, movieId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            "user_favorites",
            "userId=? AND movieId=?",
            arrayOf(userId.toString(), movieId.toString())
        )
    }

    // Lấy danh sách phim yêu thích kèm thời điểm add
//    fun getFavoriteMovieItems(userId: Int): List<FavoriteMovieItem> {
//        val list = mutableListOf<FavoriteMovieItem>()
//        val db = dbHelper.readableDatabase
//        val query = """
//            SELECT m.*, uf.createdAt as favCreatedAt
//            FROM user_favorites uf
//            INNER JOIN movies m ON uf.movieId = m.id
//            WHERE uf.userId=?
//            ORDER BY uf.createdAt DESC
//        """
//        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
//        cursor.use {
//            while (it.moveToNext()) {
//                val movie = dbHelper.cursorToMovie(it)
//                val favCreatedAt = it.getString(it.getColumnIndexOrThrow("favCreatedAt"))?.let { d ->
//                    dateFormat.parse(d)
//                }
//                list.add(FavoriteMovieItem(movie, favCreatedAt))
//            }
//        }
//        return list
//    }
}
