package com.example.android_movie_app.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("genre_ids")
    val genreIds: List<Int> = emptyList(),
    @SerializedName("adult")
    val adult: Boolean = false,
    @SerializedName("popularity")
    val popularity: Double = 0.0
) {
    val fullPosterPath: String?
        get() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    
    val fullBackdropPath: String?
        get() = backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
}