package com.example.android_movie_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_movie_app.data.model.Movie
import com.example.android_movie_app.data.repository.MovieRepository
import com.example.android_movie_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {
    
    private val _movieDetail = MutableStateFlow<Resource<Movie>>(Resource.Loading())
    val movieDetail: StateFlow<Resource<Movie>> = _movieDetail.asStateFlow()
    
    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            repository.getMovieDetails(movieId).collect { resource ->
                _movieDetail.value = resource
            }
        }
    }
}