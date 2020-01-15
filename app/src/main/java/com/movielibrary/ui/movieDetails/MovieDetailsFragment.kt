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
import androidx.lifecycle.ViewModelProviders
import com.movielibrary.R
import com.movielibrary.database.MoviesDatabase
import com.movielibrary.databinding.MovieDetailsFragmentBinding
import kotlinx.android.synthetic.main.rating_popup_view.view.*

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
        viewModel.initIcons(binding.userRatingIcon,binding.userFavouriteIcon, binding.userRating)

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

        binding.userRatingIcon.setOnClickListener {
            createPopup(it as ImageView)
        }

        binding.userFavouriteIcon.setOnClickListener {
            viewModel.addToFavourite(it as ImageView)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createPopup(imageView: ImageView) {
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
        if (!viewModel.rated) {
            popupView.remove_button.visibility = View.INVISIBLE
        }

        popupView.rate_button.setOnClickListener {
            val rating = popupView.rating_bar.rating
            viewModel.rateMovie(imageView, rating)
            val correctRating = rating*2
            binding.userRating.text = "$correctRating/10"
            popupWindow.dismiss()
        }
        popupView.remove_button.setOnClickListener {
            viewModel.removeRating(imageView, binding.userRating)
            popupWindow.dismiss()
        }
    }
}
