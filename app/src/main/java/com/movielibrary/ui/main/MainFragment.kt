package com.movielibrary.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.movielibrary.R
import com.movielibrary.databinding.MainFragmentBinding
import com.movielibrary.network.Movie
import com.movielibrary.network.MovieApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch



class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainFragmentViewModel

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: MainFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)

        val mainFragmentViewModel =
            ViewModelProviders.of(requireActivity()).get(MainFragmentViewModel::class.java)
        binding.mainFragmentViewModel = mainFragmentViewModel
        binding.lifecycleOwner = this

        val query = FirebaseFirestore.getInstance()
            .collection("movies")

        val options =
            FirestoreRecyclerOptions.Builder<Movie>()
                .setQuery(query, Movie::class.java)
                .setLifecycleOwner(this)
                .build()

        val adapter = MainFragmentAdapter(options)

        binding.movieList.adapter = adapter


        Log.i("QUERY", adapter.itemCount.toString())
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)

        getPopularMovies()
        getMovieDetails()
    }

    fun getPopularMovies() {
        coroutineScope.launch {
            val getResponseDeferred =
                MovieApi.retrofitService.getPopularMoviesAsync()
            val movieList = getResponseDeferred.await().movieList
            movieList.forEach {
                Log.i("POPULAR MOVIE", it.toString())
            }
        }
    }

    fun getMovieDetails() {
        coroutineScope.launch {
            val getResponseDeferred =
                MovieApi.retrofitService.getMovieDetailsAsync(419704)
            val movieDetails = getResponseDeferred.await()
            Log.i("MOVIE DETAIL", movieDetails.toString())
        }
    }

}
