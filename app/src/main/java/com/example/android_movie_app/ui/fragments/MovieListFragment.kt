package com.example.android_movie_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_movie_app.data.model.Movie
import com.example.android_movie_app.databinding.FragmentMovieListBinding
import com.example.android_movie_app.ui.adapters.MovieAdapter
import com.example.android_movie_app.ui.viewmodels.MovieListViewModel
import com.example.android_movie_app.utils.Resource
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieListFragment : Fragment() {
    
    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MovieListViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter
    
    private var isSearchMode = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupTabLayout()
        setupSearchView()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter { movie ->
            navigateToMovieDetail(movie)
        }
        
        binding.recyclerViewMovies.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }
    
    private fun setupTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Popular"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Top Rated"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Now Playing"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Upcoming"))
        
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (!isSearchMode) {
                    when (tab?.position) {
                        0 -> observePopularMovies()
                        1 -> observeTopRatedMovies()
                        2 -> observeNowPlayingMovies()
                        3 -> observeUpcomingMovies()
                    }
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        isSearchMode = true
                        viewModel.searchMovies(it)
                    }
                }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    isSearchMode = false
                    viewModel.clearSearchResults()
                    // Show current tab content
                    val selectedTab = binding.tabLayout.selectedTabPosition
                    when (selectedTab) {
                        0 -> observePopularMovies()
                        1 -> observeTopRatedMovies()
                        2 -> observeNowPlayingMovies()
                        3 -> observeUpcomingMovies()
                    }
                }
                return true
            }
        })
    }
    
    private fun observeViewModel() {
        // Start by observing popular movies
        observePopularMovies()
        
        // Observe search results
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResults.collect { resource ->
                    if (isSearchMode) {
                        handleMovieListResource(resource)
                    }
                }
            }
        }
    }
    
    private fun observePopularMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.popularMovies.collect { resource ->
                    if (!isSearchMode) {
                        handleMovieListResource(resource)
                    }
                }
            }
        }
    }
    
    private fun observeTopRatedMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.topRatedMovies.collect { resource ->
                    if (!isSearchMode) {
                        handleMovieListResource(resource)
                    }
                }
            }
        }
    }
    
    private fun observeNowPlayingMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nowPlayingMovies.collect { resource ->
                    if (!isSearchMode) {
                        handleMovieListResource(resource)
                    }
                }
            }
        }
    }
    
    private fun observeUpcomingMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.upcomingMovies.collect { resource ->
                    if (!isSearchMode) {
                        handleMovieListResource(resource)
                    }
                }
            }
        }
    }
    
    private fun handleMovieListResource(resource: Resource<List<Movie>>) {
        when (resource) {
            is Resource.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerViewMovies.visibility = View.GONE
            }
            is Resource.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewMovies.visibility = View.VISIBLE
                movieAdapter.submitList(resource.data ?: emptyList())
            }
            is Resource.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewMovies.visibility = View.VISIBLE
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun navigateToMovieDetail(movie: Movie) {
        val action = MovieListFragmentDirections.actionMovieListFragmentToMovieDetailFragment(
            movieId = movie.id,
            movieTitle = movie.title
        )
        findNavController().navigate(action)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}