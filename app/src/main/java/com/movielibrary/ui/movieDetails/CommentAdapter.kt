package com.movielibrary.ui.movieDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.movielibrary.database.CommentEntity
import com.movielibrary.databinding.MovieCommentFragmentBinding

class CommentAdapter : ListAdapter<CommentEntity, CommentViewHolder>(CommentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment)
    }
}

class CommentViewHolder private constructor(private val binding: MovieCommentFragmentBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(comment: CommentEntity) {
        binding.comment = comment
    }

    companion object {
        fun from(parent: ViewGroup): CommentViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = MovieCommentFragmentBinding.inflate(layoutInflater, parent, false)
            return CommentViewHolder(view)
        }
    }
}

class CommentDiffCallback : DiffUtil.ItemCallback<CommentEntity>() {
    override fun areItemsTheSame(oldItem: CommentEntity, newItem: CommentEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CommentEntity, newItem: CommentEntity): Boolean {
        return oldItem == newItem
    }
}
