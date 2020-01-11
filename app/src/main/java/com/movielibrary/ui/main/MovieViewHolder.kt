package com.movielibrary.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.movielibrary.databinding.MessageBinding
import com.movielibrary.network.Movie

class MovieViewHolder private constructor(private val binding: MessageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(movie: Movie) {
        binding.movie = movie
    }

    companion object {
        fun from(parent: ViewGroup): MovieViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = MessageBinding.inflate(layoutInflater, parent, false)
            return MovieViewHolder(view)
        }
    }
}