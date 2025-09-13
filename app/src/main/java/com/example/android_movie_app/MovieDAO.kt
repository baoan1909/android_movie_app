package com.example.android_movie_app

import android.content.ContentValues
import android.database.Cursor
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.Movie
import java.text.SimpleDateFormat
import java.util.*

class MovieDAO(private val dbHelper: DatabaseHelper) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // ---------------- CRUD Movies ----------------

    fun addMovie(movie: Movie): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("slug", movie.slug)
            put("name", movie.name)
            put("originName", movie.originName)
            put("content", movie.content)
            put("type", movie.type)
            put("thumbUrl", movie.thumbUrl)
            put("posterUrl", movie.posterUrl)
            put("year", movie.year)
            put("viewCount", movie.viewCount)
            put("rating", movie.rating)
            put("createdAt", movie.createdAt?.let { dateFormat.format(it) })
        }
        return db.insert("movies", null, values)
    }

    fun getAllMovies(): List<Movie> {
        val list = mutableListOf<Movie>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM movies ORDER BY id DESC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToMovie(it))
            }
        }
        return list
    }

    fun updateMovie(movie: Movie): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("slug", movie.slug)
            put("name", movie.name)
            put("originName", movie.originName)
            put("content", movie.content)
            put("type", movie.type)
            put("thumbUrl", movie.thumbUrl)
            put("posterUrl", movie.posterUrl)
            put("year", movie.year)
            put("viewCount", movie.viewCount)
            put("rating", movie.rating)
            put("createdAt", movie.createdAt?.let { dateFormat.format(it) })
        }
        return db.update("movies", values, "id=?", arrayOf(movie.id.toString()))
    }

    fun deleteMovie(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("movies", "id=?", arrayOf(id.toString()))
    }

    // ---------------- Custom Queries ----------------

    // Tìm kiếm phim theo tên, slug hoặc năm
    fun searchMovies(keyword: String): List<Movie> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Movie>()
        val cursor = db.rawQuery(
            """
            SELECT * FROM movies 
            WHERE name LIKE ? OR slug LIKE ? OR year LIKE ?
            """.trimIndent(),
            arrayOf("%$keyword%", "%$keyword%", keyword)
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToMovie(it))
            }
        }
        return list
    }

    // Xem phim phổ biến (sắp xếp theo viewCount giảm dần)
    fun getPopularMovies(limit: Int = 10): List<Movie> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Movie>()
        val cursor = db.rawQuery(
            "SELECT * FROM movies ORDER BY viewCount DESC LIMIT ?",
            arrayOf(limit.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToMovie(it))
            }
        }
        return list
    }

    // Xem phim mới thêm (sắp xếp theo createdAt mới nhất)
    fun getLatestMovies(limit: Int = 10): List<Movie> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Movie>()
        val cursor = db.rawQuery(
            "SELECT * FROM movies ORDER BY datetime(createdAt) DESC LIMIT ?",
            arrayOf(limit.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToMovie(it))
            }
        }
        return list
    }

    // Xem chi tiết 1 phim theo id
    fun getMovieById(id: Int): Movie? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM movies WHERE id = ?", arrayOf(id.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToMovie(it)
            }
        }
        return null
    }

    // Xem chi tiết 1 phim theo slug
    fun getMovieBySlug(slug: String): Movie? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM movies WHERE slug = ?", arrayOf(slug))
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToMovie(it)
            }
        }
        return null
    }

    /** Cập nhật rating của movie từ bảng reviews */
    fun updateMovieRatingFromReviews(movieId: Int): Int {
        val db = dbHelper.writableDatabase

        // Lấy điểm trung bình từ bảng reviews
        val cursor = db.rawQuery(
            "SELECT AVG(rating) AS avgRating FROM reviews WHERE movieId=?",
            arrayOf(movieId.toString())
        )

        var avgRating = 0.0
        cursor.use {
            if (it.moveToFirst()) {
                avgRating = it.getDouble(it.getColumnIndexOrThrow("avgRating"))
            }
        }

        // Cập nhật vào bảng movies
        val values = ContentValues().apply {
            put("rating", avgRating)
        }

        return db.update(
            "movies",
            values,
            "id=?",
            arrayOf(movieId.toString())
        )
    }

    public fun cursorToMovie(cursor: Cursor): Movie {
        return Movie(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            slug = cursor.getString(cursor.getColumnIndexOrThrow("slug")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            originName = cursor.getString(cursor.getColumnIndexOrThrow("originName")),
            content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
            type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
            thumbUrl = cursor.getString(cursor.getColumnIndexOrThrow("thumbUrl")),
            posterUrl = cursor.getString(cursor.getColumnIndexOrThrow("posterUrl")),
            year = if (!cursor.isNull(cursor.getColumnIndexOrThrow("year"))) cursor.getInt(cursor.getColumnIndexOrThrow("year")) else null,
            viewCount = cursor.getInt(cursor.getColumnIndexOrThrow("viewCount")),
            rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
            createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))?.let {
                dateFormat.parse(it)
            }
        )
    }

}
