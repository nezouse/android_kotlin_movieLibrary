package com.movielibrary.ui.movieDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.movielibrary.R
import com.movielibrary.database.CommentEntity
import com.movielibrary.database.Repository
import com.movielibrary.databinding.MovieDetailsFragmentBinding

class MovieDetailsFragment : Fragment() {

    lateinit var binding: MovieDetailsFragmentBinding
    lateinit var viewModel: MovieDetailsViewModel
    private val args: MovieDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.movie_details_fragment, container, false
        )

        val application = requireNotNull(this.activity).application
        val repository = Repository(application)
        val viewModelFactory = MovieDetailsViewModelFactory(repository, args.movie.id)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MovieDetailsViewModel::class.java)


        binding.lifecycleOwner = this
        binding.movieDetailsViewModel = viewModel
        val adapter = CommentAdapter()
        binding.commentList.adapter = adapter
        binding.movieDetailsViewModel?.movie?.value = args.movie
        viewModel.addRecentlyViewedMovie(args.movie.id)


        binding.userRatingIcon.setOnClickListener {
            viewModel.rateMovie(it as ImageView)
        }

        binding.movieDetailsViewModel!!.commentsList.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.materialButton.setOnClickListener {
            val title = binding.commentTitle.text.toString()
            val body = binding.commentBody.text.toString()
            val movieId = args.movie.id
            viewModel.insertComment(CommentEntity(title, body, movieId))
            Log.i("I/COMMENT", "Title: ${binding.commentTitle.text}")
            Log.i("I/COMMENT", "Bocy: ${binding.commentBody.text}")
        }

        return binding.root
    }

    override fun onDestroyView() {
        viewModel.repository.detachSubscription(viewModel.commentsListener)
        super.onDestroyView()
    }
}
