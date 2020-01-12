package com.movielibrary.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.movielibrary.database.MovieEntity
import com.movielibrary.databinding.MovieOverviewBinding

class FragmentAdapter :
    ListAdapter<MovieEntity, MovieViewHolder>(MovieDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }
}

class MovieViewHolder private constructor(private val binding: MovieOverviewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(movie: MovieEntity) {
        binding.movie = movie
    }

    companion object {
        fun from(parent: ViewGroup): MovieViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = MovieOverviewBinding.inflate(layoutInflater, parent, false)
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