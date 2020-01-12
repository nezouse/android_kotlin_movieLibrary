package com.movielibrary.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.movielibrary.database.MovieEntity
import com.movielibrary.databinding.MovieOverviewFragmentBinding

class FragmentAdapter(private val clickListener: MovieListener) :
    ListAdapter<MovieEntity, MovieViewHolder>(MovieDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie, clickListener)
    }


}

class MovieViewHolder private constructor(private val binding: MovieOverviewFragmentBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(movie: MovieEntity, clickListener: MovieListener) {
        binding.movie = movie
        binding.clickListener = clickListener
    }

    companion object {
        fun from(parent: ViewGroup): MovieViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = MovieOverviewFragmentBinding.inflate(layoutInflater, parent, false)
            return MovieViewHolder(view)
        }
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<MovieEntity>() {
    override fun areItemsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
        return oldItem == newItem
    }
}

class MovieListener(val clickListener: (movieTitle: String) -> Unit) {
    fun onClick(movie: MovieEntity) = clickListener(movie.title)
}