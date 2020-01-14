package com.movielibrary.ui.movieDetails

import android.os.Bundle
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

    private val args: MovieDetailsFragmentArgs by navArgs()
    lateinit var viewModel: MovieDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: MovieDetailsFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.movie_details_fragment, container, false
        )

        val application = requireNotNull(this.activity).application
        val repository = Repository(application)
        val viewModelFactory = MovieDetailsViewModelFactory(repository, args.movie.id)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MovieDetailsViewModel::class.java
        )

        val adapter = CommentAdapter()

        binding.lifecycleOwner = this
        binding.movieDetailsViewModel = viewModel
        binding.commentList.adapter = adapter
        binding.movieDetailsViewModel!!.movie.value = args.movie

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
            binding.commentTitle.setText("")
            binding.commentBody.setText("")
        }

        viewModel.addRecentlyViewedMovie(args.movie.id)

        return binding.root
    }

    override fun onDestroyView() {
        viewModel.repository.detachSubscription(viewModel.commentsListener)
        super.onDestroyView()
    }
}
