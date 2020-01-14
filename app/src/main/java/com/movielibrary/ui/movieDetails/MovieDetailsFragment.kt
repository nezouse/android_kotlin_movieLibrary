package com.movielibrary.ui.movieDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.movielibrary.R
import com.movielibrary.database.MoviesDatabase
import com.movielibrary.databinding.MovieDetailsFragmentBinding

class MovieDetailsFragment : Fragment() {

    lateinit var binding: MovieDetailsFragmentBinding
    lateinit var viewModel: MovieDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.movie_details_fragment, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = MoviesDatabase.getInstance(application).moviesDao

        val viewModelFactory = MovieDetailsViewModelFactory(dataSource, application)

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MovieDetailsViewModel::class.java)

        binding.lifecycleOwner = this
        binding.movieDetailsViewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val args = MovieDetailsFragmentArgs.fromBundle(it)
            binding.movieDetailsViewModel?.movie?.value = args.movie
            viewModel.addRecentlyViewedMovie(args.movie.id)
        }

        binding.userRatingIcon.setOnClickListener{
            viewModel.rateMovie(it as ImageView)
        }
    }
}
