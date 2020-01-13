package com.movielibrary.ui.movieDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.movielibrary.database.MovieEntity

class MovieDetailsViewModel : ViewModel() {
    var movie = MutableLiveData<MovieEntity>()
}
