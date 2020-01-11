package com.movielibrary.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.movielibrary.network.MovieApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainFragmentViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private var viewModelJob = Job()


    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    fun getPopularMovies() {
        coroutineScope.launch {
            val getPropertiesDeferred =
                MovieApi.retrofitService.getPropertiesAsync("API_KEY")
            val listResult = getPropertiesDeferred.await().movieList
            Log.i("INFO", listResult.toString())
        }
    }
}
