package com.movielibrary.ui.movieDetails

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.movielibrary.R
import com.movielibrary.database.CommentEntity
import com.movielibrary.databinding.AddCommentFragmentBinding
import com.movielibrary.databinding.MovieDetailsFragmentBinding
import com.movielibrary.ui.recyclerAdapters.CommentAdapter
import kotlinx.android.synthetic.main.rating_popup_view.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MovieDetailsFragment : Fragment() {

    private val args: MovieDetailsFragmentArgs by navArgs()
    private val movieDetailsViewModel: MovieDetailsViewModel by viewModel { parametersOf(args.movie.id) }
    lateinit var binding: MovieDetailsFragmentBinding
    lateinit var popupView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.movie_details_fragment, container, false
        )

        val addCommentBinding: AddCommentFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.add_comment_fragment, container, false
        )

        val addCommentDialog = MaterialAlertDialogBuilder(context)
            .setView(addCommentBinding.root)
            .create();

        val adapter = CommentAdapter(addCommentBinding, addCommentDialog)

        binding.lifecycleOwner = this
        binding.movieDetailsViewModel = movieDetailsViewModel
        binding.commentList.adapter = adapter
        binding.movieDetailsViewModel!!.movie.value = args.movie

        binding.movieDetailsViewModel!!.commentsList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.addCommentButton.setOnClickListener {
            addCommentBinding.commentTitle.setText("")
            addCommentBinding.commentBody.setText("")
            addCommentBinding.materialButton.text = "Add comment"
            addCommentBinding.materialButton.setOnClickListener {
                val title = addCommentBinding.commentTitle.text.toString()
                val body = addCommentBinding.commentBody.text.toString()
                val movieId = args.movie.id
                movieDetailsViewModel.insertComment(CommentEntity(title, body, movieId))
                addCommentBinding.commentTitle.setText("")
                addCommentBinding.commentBody.setText("")
                addCommentDialog.dismiss()
            }
            addCommentDialog.show()
        }

        popupView = LayoutInflater.from(activity).inflate(R.layout.rating_popup_view, null)
        val ratingAlert = AlertDialog.Builder(context).setView(popupView).create()

        binding.userRatingIcon.setOnClickListener {
            if(movieDetailsViewModel.user.email.isNotEmpty()) {
                ratingAlert.show()

                popupView.rate_button.setOnClickListener {
                    val rating = popupView.rating_bar.rating
                    movieDetailsViewModel.rateMovie(rating)
                    ratingAlert?.dismiss()
                }
                popupView.remove_button.setOnClickListener {
                    movieDetailsViewModel.removeRating()
                    ratingAlert?.dismiss()
                }
            }
        }

        binding.userFavouriteIcon.setOnClickListener {
            movieDetailsViewModel.addToFavourite()
        }

        movieDetailsViewModel.addRecentlyViewedMovie(args.movie.id)

        movieDetailsViewModel.liked.observe(this, Observer { liked ->
            liked?.let {
                if (it) {
                    binding.userFavouriteIcon.setImageResource(R.drawable.favourite_red)
                } else {
                    binding.userFavouriteIcon.setImageResource(R.drawable.favorite_border)
                }
            }
        })

        movieDetailsViewModel.rating.observe(this, Observer { rating ->
            if (rating != null) {
                binding.userRatingIcon.setImageResource(R.drawable.star_blue)
                binding.userRating.text = getString(R.string.user_rating_text, rating.toString())
                popupView.rating_bar.rating = rating / 2
                popupView.remove_button.visibility = View.VISIBLE
            } else {
                binding.userRatingIcon.setImageResource(R.drawable.star_border)
                binding.userRating.setText(R.string.rate_text)
                popupView.remove_button.visibility = View.INVISIBLE
                popupView.rating_bar.rating = 0f
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val args = MovieDetailsFragmentArgs.fromBundle(it)
            binding.movieDetailsViewModel?.movie?.value = args.movie
            movieDetailsViewModel.addRecentlyViewedMovie(args.movie.id)
        }
    }

    override fun onDestroyView() {
        movieDetailsViewModel.repository.detachSubscription(movieDetailsViewModel.commentsListener)
        super.onDestroyView()
    }
}