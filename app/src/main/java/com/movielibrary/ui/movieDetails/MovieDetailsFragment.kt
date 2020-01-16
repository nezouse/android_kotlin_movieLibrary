package com.movielibrary.ui.movieDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
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

        binding.userRatingIcon.setOnClickListener {
            movieDetailsViewModel.rateMovie(it as ImageView)
        }

        movieDetailsViewModel.initIcons(
            binding.userRatingIcon,
            binding.userFavouriteIcon,
            binding.userRating
        )

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

        movieDetailsViewModel.addRecentlyViewedMovie(args.movie.id)

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
            createPopup(it as ImageView)
        }

        binding.userFavouriteIcon.setOnClickListener {
            movieDetailsViewModel.addToFavourite(it as ImageView)
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

        if (!movieDetailsViewModel.rated) {
            popupView.remove_button.visibility = View.INVISIBLE
        }

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
