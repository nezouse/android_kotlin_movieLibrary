package com.movielibrary.ui.movieDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.movielibrary.R
import com.movielibrary.database.CommentEntity
import com.movielibrary.databinding.MovieDetailsFragmentBinding
import com.movielibrary.ui.main.PopularMoviesFragmentDirections
import kotlinx.android.synthetic.main.rating_popup_view.view.*
import com.movielibrary.ui.recyclerAdapters.CommentAdapter
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MovieDetailsFragment : Fragment() {

    private val args: MovieDetailsFragmentArgs by navArgs()
    private val movieDetailsViewModel: MovieDetailsViewModel by viewModel { parametersOf(args.movie.id) }
    lateinit var binding: MovieDetailsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.movie_details_fragment, container, false
        )

        val adapter = CommentAdapter()

        binding.lifecycleOwner = this
        binding.movieDetailsViewModel = movieDetailsViewModel
        binding.commentList.adapter = adapter
        binding.movieDetailsViewModel!!.movie.value = args.movie

        binding.userRatingIcon.setOnClickListener {
            movieDetailsViewModel.rateMovie(it as ImageView)
        }

        movieDetailsViewModel.initIcons(binding.userRatingIcon,binding.userFavouriteIcon, binding.userRating)
        binding.movieDetailsViewModel!!.commentsList.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.materialButton.setOnClickListener {
            val title = binding.commentTitle.text.toString()
            val body = binding.commentBody.text.toString()
            val movieId = args.movie.id
            movieDetailsViewModel.insertComment(CommentEntity(title, body, movieId))
            binding.commentTitle.setText("")
            binding.commentBody.setText("")
        }

        movieDetailsViewModel.addRecentlyViewedMovie(args.movie.id)

        //obserers
        viewModel.liked2.observe(this, Observer { liked ->
            liked?.let {
                if (it) {
                    binding.userFavouriteIcon.setImageResource(R.drawable.favourite_red)
                    Log.i("OBSERVER", it.toString())
                } else {
                    binding.userFavouriteIcon.setImageResource(R.drawable.favorite_border)
                    Log.i("OBSERVER", it.toString())
                }
            }
        })

        viewModel.rated2.observe(this, Observer { rated ->
            rated?.let {
                if (it) {
                    binding.userRatingIcon.setImageResource(R.drawable.star_blue)
                } else {
                    binding.userRatingIcon.setImageResource(R.drawable.star_border)
                }
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

        binding.userRatingIcon.setOnClickListener {
            createPopup()
        }

        binding.userFavouriteIcon.setOnClickListener {
            movieDetailsViewModel.addToFavourite()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createPopup() {
        val popupView = LayoutInflater.from(activity).inflate(R.layout.rating_popup_view, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        popupWindow.elevation = 5.0f
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(binding.detailsLayout, Gravity.CENTER, 0, 0)
        if (!movieDetailsViewModel.rated) {
            popupView.remove_button.visibility = View.INVISIBLE
        }
        MaterialAlertDialogBuilder(
            context,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
        )
            .setTitle("Title")
            .setMessage("Message")
            .show()

        popupView.rate_button.setOnClickListener {
            val rating = popupView.rating_bar.rating
            movieDetailsViewModel.rateMovie(imageView, rating)
            val correctRating = rating * 2
            binding.userRating.text = "$correctRating/10"
            popupWindow.dismiss()
        }
        popupView.remove_button.setOnClickListener {
            movieDetailsViewModel.removeRating(imageView, binding.userRating)
            popupWindow.dismiss()
        }
    }


    override fun onDestroyView() {
        movieDetailsViewModel.repository.detachSubscription(movieDetailsViewModel.commentsListener)
        super.onDestroyView()

    }
}
