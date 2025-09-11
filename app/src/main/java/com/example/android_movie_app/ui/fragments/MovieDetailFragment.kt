package com.example.android_movie_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.android_movie_app.R
import com.example.android_movie_app.data.model.Movie
import com.example.android_movie_app.databinding.FragmentMovieDetailBinding
import com.example.android_movie_app.ui.viewmodels.MovieDetailViewModel
import com.example.android_movie_app.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {
    
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: MovieDetailFragmentArgs by navArgs()
    private val viewModel: MovieDetailViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Load movie details using the movieId from arguments
        viewModel.loadMovieDetails(args.movieId)
        
        observeViewModel()
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieDetail.collect { resource ->
                    handleMovieDetailResource(resource)
                }
            }
        }
    }
    
    private fun handleMovieDetailResource(resource: Resource<Movie>) {
        when (resource) {
            is Resource.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
            }
            is Resource.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                resource.data?.let { movie ->
                    bindMovieData(movie)
                }
            }
            is Resource.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun bindMovieData(movie: Movie) {
        binding.apply {
            tvTitle.text = movie.title
            tvOverview.text = movie.overview
            tvReleaseDate.text = "Release Date: ${movie.releaseDate}"
            tvRating.text = "★ ${String.format("%.1f", movie.voteAverage)} (${movie.voteCount} votes)"
            
            // Load backdrop image
            Glide.with(requireContext())
                .load(movie.fullBackdropPath)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivBackdrop)
            
            // Load poster image
            Glide.with(requireContext())
                .load(movie.fullPosterPath)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPoster)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}