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
class MovieListViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {
    
    private val _popularMovies = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())
    val popularMovies: StateFlow<Resource<List<Movie>>> = _popularMovies.asStateFlow()
    
    private val _topRatedMovies = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())
    val topRatedMovies: StateFlow<Resource<List<Movie>>> = _topRatedMovies.asStateFlow()
    
    private val _nowPlayingMovies = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())
    val nowPlayingMovies: StateFlow<Resource<List<Movie>>> = _nowPlayingMovies.asStateFlow()
    
    private val _upcomingMovies = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())
    val upcomingMovies: StateFlow<Resource<List<Movie>>> = _upcomingMovies.asStateFlow()
    
    private val _searchResults = MutableStateFlow<Resource<List<Movie>>>(Resource.Success(emptyList()))
    val searchResults: StateFlow<Resource<List<Movie>>> = _searchResults.asStateFlow()
    
    init {
        loadAllMovies()
    }
    
    private fun loadAllMovies() {
        loadPopularMovies()
        loadTopRatedMovies()
        loadNowPlayingMovies()
        loadUpcomingMovies()
    }
    
    fun loadPopularMovies() {
        viewModelScope.launch {
            repository.getPopularMovies().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _popularMovies.value = Resource.Success(resource.data?.results ?: emptyList())
                    }
                    is Resource.Error -> {
                        _popularMovies.value = Resource.Error(resource.message ?: "Unknown error")
                    }
                    is Resource.Loading -> {
                        _popularMovies.value = Resource.Loading()
                    }
                }
            }
        }
    }
    
    fun loadTopRatedMovies() {
        viewModelScope.launch {
            repository.getTopRatedMovies().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _topRatedMovies.value = Resource.Success(resource.data?.results ?: emptyList())
                    }
                    is Resource.Error -> {
                        _topRatedMovies.value = Resource.Error(resource.message ?: "Unknown error")
                    }
                    is Resource.Loading -> {
                        _topRatedMovies.value = Resource.Loading()
                    }
                }
            }
        }
    }
    
    fun loadNowPlayingMovies() {
        viewModelScope.launch {
            repository.getNowPlayingMovies().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _nowPlayingMovies.value = Resource.Success(resource.data?.results ?: emptyList())
                    }
                    is Resource.Error -> {
                        _nowPlayingMovies.value = Resource.Error(resource.message ?: "Unknown error")
                    }
                    is Resource.Loading -> {
                        _nowPlayingMovies.value = Resource.Loading()
                    }
                }
            }
        }
    }
    
    fun loadUpcomingMovies() {
        viewModelScope.launch {
            repository.getUpcomingMovies().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _upcomingMovies.value = Resource.Success(resource.data?.results ?: emptyList())
                    }
                    is Resource.Error -> {
                        _upcomingMovies.value = Resource.Error(resource.message ?: "Unknown error")
                    }
                    is Resource.Loading -> {
                        _upcomingMovies.value = Resource.Loading()
                    }
                }
            }
        }
    }
    
    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _searchResults.value = Resource.Success(emptyList())
            return
        }
        
        viewModelScope.launch {
            repository.searchMovies(query).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _searchResults.value = Resource.Success(resource.data?.results ?: emptyList())
                    }
                    is Resource.Error -> {
                        _searchResults.value = Resource.Error(resource.message ?: "Unknown error")
                    }
                    is Resource.Loading -> {
                        _searchResults.value = Resource.Loading()
                    }
                }
            }
        }
    }
    
    fun clearSearchResults() {
        _searchResults.value = Resource.Success(emptyList())
    }
}