package com.example.android_movie_app.model

data class MovieResponse(
    val items: List<ApiMovieItem>
)

data class ApiMovieItem(
    val id: String,
    val name: String,
    val slug: String,
    val thumb_url: String?,
    val poster_url: String?,
    val year: Int?,
    val category: List<ApiCategory>?
)

data class ApiCategory(
    val id: String,
    val name: String,
    val slug: String
)


