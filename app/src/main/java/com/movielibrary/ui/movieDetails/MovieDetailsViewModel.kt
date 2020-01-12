package com.movielibrary.ui.movieDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MovieDetailsViewModel : ViewModel() {
    var movieId = MutableLiveData<String>()
}