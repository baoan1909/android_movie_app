package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.android_movie_app.Category
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.Movie
import com.example.android_movie_app.MovieCategory
import com.example.android_movie_app.MovieWithCategories
import java.text.SimpleDateFormat
import java.util.*

class MovieCategoryDAO(private val dbHelper: DatabaseHelper) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // ---------- ADD RELATION ----------

    fun addMovieCategory(movieId: Int, categoryId: Int, createdAt: Date? = null): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("movieId", movieId)
            put("categoryId", categoryId)
            put("createdAt", createdAt?.let { dateFormat.format(it) })
        }
        return db.insertWithOnConflict("movie_categories", null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    // ---------- REMOVE RELATION ----------
    fun removeMovieCategory(movieId: Int, categoryId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("movie_categories", "movieId=? AND categoryId=?", arrayOf(movieId.toString(), categoryId.toString()))
    }

    // ---------- GET CATEGORIES BY MOVIE ----------
    fun getCategoriesByMovie(movieId: Int): List<Category> {
        val list = mutableListOf<Category>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT c.* FROM categories c
            INNER JOIN movie_categories mc ON c.id = mc.categoryId
            WHERE mc.movieId=?
            ORDER BY c.name ASC
            """.trimIndent(),
            arrayOf(movieId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Category(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    name = it.getString(it.getColumnIndexOrThrow("name")),
                    slug = it.getString(it.getColumnIndexOrThrow("slug")),
                    description = it.getString(it.getColumnIndexOrThrow("description")),
                    createdAt = it.getString(it.getColumnIndexOrThrow("createdAt"))
                        ?.let { d -> dateFormat.parse(d) }
                ))
            }
        }
        return list
    }

    // ---------- GET MOVIES BY CATEGORY ----------
    fun getMoviesByCategory(categoryId: Int): List<Movie> {
        val list = mutableListOf<Movie>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT m.* FROM movies m
            INNER JOIN movie_categories mc ON m.id = mc.movieId
            WHERE mc.categoryId=?
            ORDER BY m.name ASC
            """.trimIndent(),
            arrayOf(categoryId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Movie(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    slug = it.getString(it.getColumnIndexOrThrow("slug")),
                    name = it.getString(it.getColumnIndexOrThrow("name")),
                    originName = it.getString(it.getColumnIndexOrThrow("originName")),
                    content = it.getString(it.getColumnIndexOrThrow("content")),
                    type = it.getString(it.getColumnIndexOrThrow("type")),
                    thumbUrl = it.getString(it.getColumnIndexOrThrow("thumbUrl")),
                    posterUrl = it.getString(it.getColumnIndexOrThrow("posterUrl")),
                    year = if (!it.isNull(it.getColumnIndexOrThrow("year"))) it.getInt(
                        it.getColumnIndexOrThrow(
                            "year"
                        )
                    ) else null,
                    viewCount = it.getInt(it.getColumnIndexOrThrow("viewCount")),
                    rating = it.getDouble(it.getColumnIndexOrThrow("rating")),
                    createdAt = it.getString(it.getColumnIndexOrThrow("createdAt"))
                        ?.let { d -> dateFormat.parse(d) }
                ))
            }
        }
        return list
    }

    // ---------- GET ALL RELATIONS ----------
    fun getAllRelations(): List<MovieCategory> {
        val list = mutableListOf<MovieCategory>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM movie_categories", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    MovieCategory(
                    movieId = it.getInt(it.getColumnIndexOrThrow("movieId")),
                    categoryId = it.getInt(it.getColumnIndexOrThrow("categoryId")),
                    createdAt = it.getString(it.getColumnIndexOrThrow("createdAt"))
                        ?.let { d -> dateFormat.parse(d) }
                ))
            }
        }
        return list
    }

    // ---------- REMOVE ALL CATEGORIES OF A MOVIE ----------
    fun removeAllCategoriesOfMovie(movieId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("movie_categories", "movieId=?", arrayOf(movieId.toString()))
    }

    // ---------- REMOVE ALL MOVIES OF A CATEGORY ----------
    fun removeAllMoviesOfCategory(categoryId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("movie_categories", "categoryId=?", arrayOf(categoryId.toString()))
    }

    //hàm getMoviesWithCategories() trả về List<MovieWithCategories>
    fun getMoviesWithCategories(): List<MovieWithCategories> {
        val list = mutableListOf<MovieWithCategories>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT 
            m.id AS movieId,
            m.slug,
            m.name,
            m.originName,
            m.type,
            m.thumbUrl,
            m.posterUrl,
            m.year,
            m.rating,
            m.createdAt
        FROM movies m
        """.trimIndent(),
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                val movieId = it.getInt(it.getColumnIndexOrThrow("movieId"))

                // Lấy categories của movie này
                val categories = getCategoriesByMovie(movieId)

                list.add(
                    MovieWithCategories(
                        movieId = movieId,
                        slug = it.getString(it.getColumnIndexOrThrow("slug")),
                        name = it.getString(it.getColumnIndexOrThrow("name")),
                        originName = it.getString(it.getColumnIndexOrThrow("originName")),
                        type = it.getString(it.getColumnIndexOrThrow("type")),
                        thumbUrl = it.getString(it.getColumnIndexOrThrow("thumbUrl")),
                        posterUrl = it.getString(it.getColumnIndexOrThrow("posterUrl")),
                        year = if (!it.isNull(it.getColumnIndexOrThrow("year")))
                            it.getInt(it.getColumnIndexOrThrow("year")) else null,
                        rating = it.getDouble(it.getColumnIndexOrThrow("rating")),
                        createdAt = it.getString(it.getColumnIndexOrThrow("createdAt"))?.let { d ->
                            dateFormat.parse(d)
                        },
                        categories = categories // <-- bây giờ là List<Category>
                    )
                )
            }
        }

        return list
    }
}
