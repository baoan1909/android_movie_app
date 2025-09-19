package com.example.android_movie_app

import java.util.Date

// Bảng users
data class User(
    var id: Int = 0,
    var username: String,
    var email: String,
    var passwordHash: String,
    var createdAt: Date? = null,   // có ngày + giờ
    var isActive: Boolean = true,
    val avatarPath: String? = null // thêm avatarPath
)

// Bảng movies
data class Movie(
    var id: Int = 0,
    var slug: String,
    var name: String,
    var originName: String? = null,
    var content: String? = null,
    var type: String = "single",  // single | series
    var thumbUrl: String? = null,
    var posterUrl: String? = null,
    var year: Int? = null,
    var viewCount: Int = 0,
    var rating: Double = 0.0,
    var createdAt: Date? = null
)

// Dùng riêng cho banner hiển thị
data class MovieBanner(
    var id: Int = 0,
    var name: String,
    var content: String? = null,
    var type: String = "single",  // single | series
    var thumbUrl: String? = null,
    var posterUrl: String? = null,
    var year: Int? = null,
    var viewCount: Int = 0,
    var rating: Double = 0.0,
    val duration: Int? = null,     // thời lượng (phút) cho phim lẻ
    val currentEpisodes: Int = 0,  // số tập đã chiếu
    val totalEpisodes: Int = 0     // tổng số tập
)



// ---------- CATEGORIES ----------
data class Category(
    var id: Int = 0,
    var name: String,
    var slug: String,
    var description: String? = null,
    var createdAt: Date? = null
)

// ---------- MOVIE_CATEGORIES (Junction Many-to-Many) ----------
data class MovieCategory(
    var movieId: Int,
    var categoryId: Int,
    var createdAt: Date? = null
)

// Bảng episodes
data class Episode(
    var id: Int = 0,
    var movieId: Int,
    var name: String,
    var episodeNumber: Int = 1,
    var videoUrl: String,
    var duration: Int? = null,
    var createdAt: Date? = null
)

// Bảng user_favorites
data class UserFavorite(
    var userId: Int,
    var movieId: Int,
    var createdAt: Date? = null
)

// Bảng watch_progress
data class WatchProgress(
    var userId: Int,
    var movieId: Int,
    var episodeId: Int? = null,
    var currentTime: Int = 0,
    var totalTime: Int = 0,
    var isCompleted: Boolean = false,
    var lastWatchedAt: Date? = null
)

// Bảng user_sessions
data class UserSession(
    var sessionToken: String,
    var userId: Int,
    var expiresAt: Date
)

// ---------- COMMENTS ----------
data class Comment(
    var id: Int = 0,
    var userId: Int,
    var movieId: Int,
    var episodeId: Int? = null,
    var parentCommentId: Int? = null,
    var content: String,
    var createdAt: Date? = null
)

// ---------- REVIEWS ----------
data class Review(
    var id: Int = 0,
    var userId: Int,
    var movieId: Int,
    var episodeId: Int? = null,
    var rating: Int,
    var createdAt: Date? = null,
    var updatedAt: Date? = null
)

data class ContinueWatchingItem(
    val progress: WatchProgress,
    val movie: Movie
)

data class FavoriteMovieItem(
    val movie: Movie,
    val createdAt: Date? = null
)

data class MovieWithCategories(
    val movieId: Int,
    val slug: String,
    val name: String,
    val originName: String? = null,
    val type: String,
    val thumbUrl: String? = null,
    val posterUrl: String? = null,
    val year: Int? = null,
    val rating: Double = 0.0,
    val createdAt: Date? = null,
    val categories: List<Category> = emptyList()
)

data class Notifications(
    val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: Date?,
    val type: String,
    val userId: Int?
)



