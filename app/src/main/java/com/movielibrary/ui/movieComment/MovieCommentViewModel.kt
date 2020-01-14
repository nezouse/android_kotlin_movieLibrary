package com.movielibrary.ui.movieComment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.movielibrary.database.MoviesDao

class MovieCommentViewModel(
    val database: MoviesDao,
    application: Application
) : AndroidViewModel(application) {
}