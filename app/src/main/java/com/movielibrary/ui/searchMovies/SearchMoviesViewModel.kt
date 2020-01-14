package com.movielibrary.ui.searchMovies

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.movielibrary.database.MovieEntity
import com.movielibrary.database.MoviesDao
import com.movielibrary.network.MovieApiService
import com.movielibrary.network.toEntity
import com.movielibrary.ui.recyclerAdapters.FragmentAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.LinkedList

class SearchMoviesViewModel(
    private val database: MoviesDao,
    private val retrofitService: MovieApiService
) : ViewModel() {
    var searchMoviesList: LinkedList<MovieEntity> = LinkedList()
    lateinit var adapter: FragmentAdapter
    private val _navigateToDetailView = MutableLiveData<MovieEntity>()
    val navigateToDetailView: LiveData<MovieEntity>
        get() = _navigateToDetailView

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    init {
        coroutineScope.launch {
            searchMoviesList.addAll(database.getRecentlyViewedMovies())
            notifyAdapter()
        }
    }

    fun onMovieClicked(movie: MovieEntity) {
        _navigateToDetailView.value = movie
    }

    fun onMovieNavigated() {
        _navigateToDetailView.value = null
    }

    fun getSearchResult(query: String?) {
        coroutineScope.launch {
            try {
                if (!query.isNullOrEmpty()) {
                    val getResponseDeferred = retrofitService.searchMoviesAsync(query)
                    val movieList = getResponseDeferred.await().movieList

                    database.insertMovies(*movieList.toEntity().toTypedArray())
                    val foundMovies = database.searchMovies("%$query%")

                    searchMoviesList.clear()
                    searchMoviesList.addAll(foundMovies)

                    notifyAdapter()
                }
            } catch (e: Exception) {
                Log.i("MOVIES/EXCEPTION", e.toString())
            }
        }
    }

    private fun notifyAdapter() {
        coroutineScope.launch(Dispatchers.Main.immediate) {
            adapter.notifyDataSetChanged()
        }
    }
}
