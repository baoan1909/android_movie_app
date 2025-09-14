package com.example.android_movie_app.model

import com.example.android_movie_app.Category
import com.example.android_movie_app.Movie
import com.example.android_movie_app.dao.CategoryDAO
import com.example.android_movie_app.dao.MovieCategoryDAO
import com.example.android_movie_app.dao.MovieDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class MovieRepository(
    private val movieDao: MovieDAO,
    private val categoryDao: CategoryDAO,
    private val movieCategoryDao: MovieCategoryDAO
) {

    /**
     * Lưu movies + categories + movie_categories trong 1 transaction
     * Nếu có lỗi, rollback toàn bộ
     */
    suspend fun saveMoviesWithCategories(apiMovies: List<ApiMovieItem>) = withContext(Dispatchers.IO) {
        val db = movieDao.dbHelper.writableDatabase // lấy database từ MovieDAO
        try {
            db.beginTransaction()

            apiMovies.forEach { apiMovie ->
                // 1. Lưu movie
                val movieEntity = apiMovie.toMovieEntity()
                val movieId = movieDao.addMovie(movieEntity).takeIf { it != -1L }?.toInt() ?: movieEntity.id

                // 2. Lưu category và quan hệ
                apiMovie.category.orEmpty().forEach { apiCategory ->
                    val existingCategory = categoryDao.getCategoryBySlug(apiCategory.slug)
                    val categoryId = existingCategory?.id ?: run {
                        val newCategory = apiCategory.toCategoryEntity()
                        val insertedCatId = categoryDao.addCategory(newCategory).toInt()
                        insertedCatId
                    }

                    // Lưu quan hệ movie ↔ category
                    movieCategoryDao.addMovieCategory(movieId, categoryId, Date())
                }
            }

            db.setTransactionSuccessful() // commit transaction
        } catch (e: Exception) {
            e.printStackTrace() // rollback tự động nếu không setTransactionSuccessful
        } finally {
            db.endTransaction()
        }
    }

    // Lấy tất cả movies kèm categories
    suspend fun getAllMoviesWithCategories() = withContext(Dispatchers.IO) {
        movieCategoryDao.getMoviesWithCategories()
    }
}

// --- Extension functions để mapping API → DB entities ---
fun ApiMovieItem.toMovieEntity() = Movie(
    id = id.toIntOrNull() ?: 0,
    slug = slug ?: "",
    name = name ?: "",
    originName = null,
    content = null,
    type = "single",
    thumbUrl = thumb_url ?: "",
    posterUrl = poster_url ?: "",
    year = year,
    viewCount = 0,
    rating = 0.0,
    createdAt = null
)

fun ApiCategory.toCategoryEntity() = Category(
    id = id.toIntOrNull() ?: 0,
    name = name ?: "",
    slug = slug ?: "",
    description = null,
    createdAt = null
)
