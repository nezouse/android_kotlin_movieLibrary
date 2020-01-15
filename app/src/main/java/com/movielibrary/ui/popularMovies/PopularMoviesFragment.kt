package com.movielibrary.ui.popularMovies

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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.movielibrary.R
import com.movielibrary.databinding.MainFragmentBinding
import com.movielibrary.ui.recyclerAdapters.FragmentAdapter
import com.movielibrary.ui.recyclerAdapters.MovieListener
import org.koin.android.viewmodel.ext.android.viewModel

class PopularMoviesFragment : Fragment() {

    private val mainFragmentViewModel: PopularMoviesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: MainFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)

        binding.mainFragmentViewModel = mainFragmentViewModel
        binding.lifecycleOwner = this

        val adapter =
            FragmentAdapter(
                MovieListener { movie ->
                    mainFragmentViewModel.onMovieClicked(movie)
                })

        mainFragmentViewModel.navigateToDetailView.observe(this, Observer { movie ->
            movie?.let {
                this.findNavController()
                    .navigate(
                        PopularMoviesFragmentDirections.actionMainFragmentToMovieDetails(
                            movie
                        )
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
