package com.movielibrary.ui.main

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.movielibrary.R
import com.movielibrary.database.MoviesDatabase
import com.movielibrary.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSearchBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = MoviesDatabase.getInstance(application).moviesDao

        val viewModelFactory = SearchFragmentViewModelFactory(dataSource, application)

        val searchMoviesViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SearchMoviesViewModel::class.java)

        binding.searchFragmentViewModel = searchMoviesViewModel
        binding.lifecycleOwner = this


        val adapter = FragmentAdapter()

        searchMoviesViewModel.searchMoviesList.observe(viewLifecycleOwner, Observer {
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
        val menuItem = menu.findItem(R.id.searchFragment)
        menuItem.expandActionView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            view!!.findNavController()
        ) || super.onOptionsItemSelected(item)
    }
}
