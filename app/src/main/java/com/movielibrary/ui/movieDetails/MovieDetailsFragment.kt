package com.movielibrary.ui.movieDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.movielibrary.R
import com.movielibrary.databinding.MovieDetailsFragmentBinding

class MovieDetailsFragment : Fragment() {

    lateinit var binding: MovieDetailsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.movie_details_fragment, container, false)

        val viewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel::class.java)

        binding.lifecycleOwner = this
        binding.movieDetailsViewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val args = MovieDetailsFragmentArgs.fromBundle(it)
            binding.movieDetailsViewModel?.movie?.value = args.movie
        }
    }
}
