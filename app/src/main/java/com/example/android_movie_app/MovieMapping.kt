package com.example.android_movie_app

import java.util.Date

// Bảng users
data class User(
    var id: Int = 0,
    var username: String,
    var email: String,
    var passwordHash: String,
    var displayName: String? = null,
    var createdAt: Date? = null,   // có ngày + giờ
    var isActive: Boolean = true
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

// Bảng episodes
data class Episode(
    var id: Int = 0,
    var movieId: Int,
    var name: String,
    var episodeNumber: Int = 1,
    var videoUrl: String,
    var duration: Int? = null,
    var viewCount: Int = 0,
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

data class ContinueWatchingItem(
    val progress: WatchProgress,
    val movie: Movie
)

data class FavoriteMovieItem(
    val movie: Movie,
    val createdAt: Date? = null
)

