package com.movielibrary.ui.movieDetails

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.movielibrary.R
import com.movielibrary.database.CommentEntity
import com.movielibrary.database.MovieEntity
import com.movielibrary.database.Repository

class MovieDetailsViewModel(val repository: Repository, val movieId: Int) :
    ViewModel() {
    var movie = MutableLiveData<MovieEntity>()
    var commentsList: LiveData<List<CommentEntity>> = repository.getMovieComments(movieId)
    val commentsListener = repository.subscribeToComments(movieId)

    fun addRecentlyViewedMovie(id: Int) {
        repository.addRecentlyViewedMovie(id)
    }

    fun insertComment(comment: CommentEntity) {
        repository.insertUserComment(comment)
    }

    fun rateMovie(imageView: ImageView) {
        //TODO add movie rating
        imageView.setImageResource(R.drawable.star_blue)
    }
}
