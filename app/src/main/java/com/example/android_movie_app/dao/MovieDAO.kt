package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.Movie
import java.text.SimpleDateFormat
import java.util.*

class MovieDAO(val dbHelper: DatabaseHelper) {

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

    fun getGenres(): List<String> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT name FROM categories ORDER BY name", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun getPosters(): List<String> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<String>()
        val cursor = db.rawQuery(
            "SELECT posterUrl FROM movies ORDER BY createdAt DESC LIMIT 10",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val posterUrl = "https://img.ophim.live/uploads/movies/${cursor.getString(0)}"
                list.add(posterUrl)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }

    fun getRecentMovies(): List<Movie> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Movie>()
        val cursor = db.rawQuery(
            "SELECT * FROM movies ORDER BY createdAt DESC LIMIT 10",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToMovie(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }
    
    fun getMovieById(movieId: Int): Movie? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM movies WHERE id = ?",
            arrayOf(movieId.toString())
        )
        
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToMovie(it)
            }
        }
        return null
    }
    
    fun getTopMovies(): List<Movie> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Movie>()
        val cursor = db.rawQuery(
            """
        SELECT m.id, m.name, m.posterUrl, m.thumbUrl, m.rating
        FROM movies m
        ORDER BY m.rating DESC
        LIMIT 10
        """.trimIndent(), null
        )

        if (cursor.moveToFirst()) {
            do {
                val movie = Movie(
                    id = cursor.getInt(0),
                    slug = "",
                    name = cursor.getString(1),
                    originName = null,
                    content = null,
                    type = "",
                    thumbUrl = "https://img.ophim.live/uploads/movies/${cursor.getString(3)}",
                    posterUrl = "https://img.ophim.live/uploads/movies/${cursor.getString(2)}",
                    year = null,
                    viewCount = 0,
                    rating = cursor.getDouble(4),
                    createdAt = null
                )
                list.add(movie)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
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
