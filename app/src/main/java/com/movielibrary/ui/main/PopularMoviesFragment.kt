package com.movielibrary.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.movielibrary.R
import com.movielibrary.database.MoviesDatabase
import com.movielibrary.databinding.MainFragmentBinding

class PopularMoviesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: MainFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = MoviesDatabase.getInstance(application).moviesDao

        val viewModelFactory = MainFragmentViewModelFactory(dataSource, application)

        val mainFragmentViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(PopularMoviesViewModel::class.java)

        binding.mainFragmentViewModel = mainFragmentViewModel
        binding.lifecycleOwner = this

        val adapter = FragmentAdapter(MovieListener { movie ->
            mainFragmentViewModel.onMovieClicked(movie)
        })

        mainFragmentViewModel.navigateToDetailView.observe(this, Observer { movie ->
            movie?.let {
                this.findNavController()
                    .navigate(
                        PopularMoviesFragmentDirections.actionMainFragmentToMovieDetails(movie)
                    )
                mainFragmentViewModel.onMovieNavigated()
            }
        })

        mainFragmentViewModel.popularMoviesList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.movieList.adapter = adapter

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            view!!.findNavController()
        ) || super.onOptionsItemSelected(item)
    }
}
