package com.example.android_movie_app.data.repository

import com.example.android_movie_app.data.api.MovieApiService
import com.example.android_movie_app.data.model.Movie
import com.example.android_movie_app.data.model.MovieResponse
import com.example.android_movie_app.data.model.GenreResponse
import com.example.android_movie_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: MovieApiService
) {
    companion object {
        private const val API_KEY = "YOUR_TMDB_API_KEY" // Replace with actual API key
    }
    
    fun getPopularMovies(page: Int = 1): Flow<Resource<MovieResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getPopularMovies(API_KEY, page)
            emit(handleMovieResponse(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.localizedMessage ?: "An unknown error occurred"))
        }
    }
    
    fun getTopRatedMovies(page: Int = 1): Flow<Resource<MovieResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getTopRatedMovies(API_KEY, page)
            emit(handleMovieResponse(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.localizedMessage ?: "An unknown error occurred"))
        }
    }
    
    fun getNowPlayingMovies(page: Int = 1): Flow<Resource<MovieResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getNowPlayingMovies(API_KEY, page)
            emit(handleMovieResponse(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.localizedMessage ?: "An unknown error occurred"))
        }
    }
    
    fun getUpcomingMovies(page: Int = 1): Flow<Resource<MovieResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getUpcomingMovies(API_KEY, page)
            emit(handleMovieResponse(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.localizedMessage ?: "An unknown error occurred"))
        }
    }
    
    fun getMovieDetails(movieId: Int): Flow<Resource<Movie>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMovieDetails(movieId, API_KEY)
            emit(handleSingleMovieResponse(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.localizedMessage ?: "An unknown error occurred"))
        }
    }
    
    fun searchMovies(query: String, page: Int = 1): Flow<Resource<MovieResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.searchMovies(API_KEY, query, page)
            emit(handleMovieResponse(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.localizedMessage ?: "An unknown error occurred"))
        }
    }
    
    fun getGenres(): Flow<Resource<GenreResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getGenres(API_KEY)
            emit(handleGenreResponse(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.localizedMessage ?: "An unknown error occurred"))
        }
    }
    
    private fun handleMovieResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        return if (response.isSuccessful) {
            response.body()?.let { result ->
                Resource.Success(result)
            } ?: Resource.Error("Empty response body")
        } else {
            Resource.Error("Error: ${response.code()} - ${response.message()}")
        }
    }
    
    private fun handleSingleMovieResponse(response: Response<Movie>): Resource<Movie> {
        return if (response.isSuccessful) {
            response.body()?.let { result ->
                Resource.Success(result)
            } ?: Resource.Error("Empty response body")
        } else {
            Resource.Error("Error: ${response.code()} - ${response.message()}")
        }
    }
    
    private fun handleGenreResponse(response: Response<GenreResponse>): Resource<GenreResponse> {
        return if (response.isSuccessful) {
            response.body()?.let { result ->
                Resource.Success(result)
            } ?: Resource.Error("Empty response body")
        } else {
            Resource.Error("Error: ${response.code()} - ${response.message()}")
        }
    }
}