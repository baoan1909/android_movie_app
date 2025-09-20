package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.android_movie_app.Category
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.Movie
import com.example.android_movie_app.MovieBanner
import com.example.android_movie_app.MovieWithCategories
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

    // Lấy danh sách phim theo tên thể loại
    fun getMoviesByGenre(genreName: String): List<Movie> {
        val db = dbHelper.readableDatabase
        val movies = mutableListOf<Movie>()

        val query = """
        SELECT m.*
        FROM movies m
        INNER JOIN movie_categories mc ON m.id = mc.movieId
        INNER JOIN categories c ON mc.categoryId = c.id
        WHERE c.name = ?
    """

        val cursor = db.rawQuery(query, arrayOf(genreName))

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    movies.add(cursorToMovie(it))
                } while (it.moveToNext())
            }
        }

        db.close()
        return movies
    }

    fun getMoviesForBanner(limit: Int = 5): List<MovieBanner> {
        val movies = mutableListOf<MovieBanner>()
        val db = dbHelper.readableDatabase

        // Query phim (ưu tiên lấy theo viewCount cao nhất hoặc mới nhất)
        val cursor = db.rawQuery(
            """
        SELECT m.id, m.name, m.posterUrl, m.type, m.year
        FROM movies m
        ORDER BY m.viewCount DESC, m.createdAt DESC
        LIMIT ?
        """.trimIndent(), arrayOf(limit.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val movieId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val posterUrl = cursor.getString(cursor.getColumnIndexOrThrow("posterUrl"))
                val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                val year = cursor.getInt(cursor.getColumnIndexOrThrow("year"))

                var duration: Int? = null
                var currentEpisodes = 0
                var totalEpisodes = 0

                if (type == "single") {
                    // Lấy thời lượng từ episode đầu tiên (nếu có)
                    val epCursor = db.rawQuery(
                        "SELECT duration FROM episodes WHERE movieId = ? LIMIT 1",
                        arrayOf(movieId.toString())
                    )
                    if (epCursor.moveToFirst()) {
                        duration = epCursor.getInt(epCursor.getColumnIndexOrThrow("duration"))
                    }
                    epCursor.close()
                } else {
                    // Lấy số tập hiện tại và tổng số tập (fix alias current -> currentEp)
                    val epCursor = db.rawQuery(
                        "SELECT COUNT(*) as total, MAX(episodeNumber) as currentEp FROM episodes WHERE movieId = ?",
                        arrayOf(movieId.toString())
                    )
                    if (epCursor.moveToFirst()) {
                        totalEpisodes = epCursor.getInt(epCursor.getColumnIndexOrThrow("total"))
                        currentEpisodes = epCursor.getInt(epCursor.getColumnIndexOrThrow("currentEp"))
                    }
                    epCursor.close()
                }

                movies.add(
                    MovieBanner(
                        id = movieId,
                        name = name,
                        posterUrl = posterUrl,
                        year = year,
                        type = type,
                        duration = duration,
                        currentEpisodes = currentEpisodes,
                        totalEpisodes = totalEpisodes
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return movies
    }

    // Lấy phim cùng thể loại (loại bỏ movie hiện tại)
    fun getRelatedMovies(movieId: Int): List<Movie> {
        val db = dbHelper.readableDatabase
        val movies = mutableListOf<Movie>()

        val query = """
        SELECT DISTINCT m.*
        FROM movies m
        INNER JOIN movie_categories mc ON m.id = mc.movieId
        WHERE mc.categoryId IN (
            SELECT categoryId 
            FROM movie_categories 
            WHERE movieId = ?
        )
        AND m.id != ?
        ORDER BY m.createdAt DESC
    """

        val cursor = db.rawQuery(query, arrayOf(movieId.toString(), movieId.toString()))

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    movies.add(cursorToMovie(it))
                } while (it.moveToNext())
            }
        }

        db.close()
        return movies
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
    fun searchMovies(keyword: String?, categorySlug: String?): List<MovieWithCategories> {
        val db = dbHelper.readableDatabase
        val movies = mutableListOf<MovieWithCategories>()

        val query = StringBuilder("""
        SELECT m.id, m.slug, m.name, m.originName, m.type, m.thumbUrl, m.posterUrl, 
               m.year, m.rating, m.createdAt,
               c.id as categoryId, c.name as categoryName, c.slug as categorySlug
        FROM movies m
        LEFT JOIN movie_categories mc ON m.id = mc.movieId
        LEFT JOIN categories c ON mc.categoryId = c.id
        WHERE 1=1
    """)

        val args = mutableListOf<String>()

        // Nếu có từ khóa => tìm theo tên hoặc originName
        if (!keyword.isNullOrEmpty()) {
            query.append(" AND (m.name LIKE ? OR m.originName LIKE ?)")
            args.add("%$keyword%")
            args.add("%$keyword%")
        }

        // Nếu có slug category => lọc thêm theo category
        if (!categorySlug.isNullOrEmpty()) {
            query.append(" AND c.slug = ?")
            args.add(categorySlug)
        }

        query.append(" ORDER BY m.createdAt DESC")

        val cursor = db.rawQuery(query.toString(), args.toTypedArray())

        cursor.use {
            val map = mutableMapOf<Int, MovieWithCategories>()

            if (it.moveToFirst()) {
                do {
                    val movieId = it.getInt(it.getColumnIndexOrThrow("id"))

                    val category = if (!it.isNull(it.getColumnIndexOrThrow("categoryId"))) {
                        Category(
                            id = it.getInt(it.getColumnIndexOrThrow("categoryId")),
                            name = it.getString(it.getColumnIndexOrThrow("categoryName")),
                            slug = it.getString(it.getColumnIndexOrThrow("categorySlug"))
                        )
                    } else null

                    val movie = map.getOrPut(movieId) {
                        MovieWithCategories(
                            movieId = movieId,
                            slug = it.getString(it.getColumnIndexOrThrow("slug")),
                            name = it.getString(it.getColumnIndexOrThrow("name")),
                            originName = it.getString(it.getColumnIndexOrThrow("originName")),
                            type = it.getString(it.getColumnIndexOrThrow("type")),
                            thumbUrl = it.getString(it.getColumnIndexOrThrow("thumbUrl")),
                            posterUrl = it.getString(it.getColumnIndexOrThrow("posterUrl")),
                            year = it.getInt(it.getColumnIndexOrThrow("year")),
                            rating = it.getDouble(it.getColumnIndexOrThrow("rating")),
                            createdAt = null,
                            categories = mutableListOf()
                        )
                    }

                    if (category != null) {
                        (movie.categories as MutableList).add(category)
                    }
                } while (it.moveToNext())
            }

            movies.addAll(map.values)
        }

        db.close()
        return movies
    }


}
