package com.movielibrary.ui.recyclerAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.movielibrary.database.CommentEntity
import com.movielibrary.database.Repository
import com.movielibrary.databinding.AddCommentFragmentBinding
import com.movielibrary.databinding.MovieCommentFragmentBinding
import org.koin.core.KoinComponent
import org.koin.core.inject

class CommentAdapter(
    private val editCommentBinding: AddCommentFragmentBinding,
    private val editCommentDialog: AlertDialog
) : ListAdapter<CommentEntity, CommentViewHolder>(CommentDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment, editCommentBinding, editCommentDialog)
    }
}

class CommentViewHolder private constructor(private val binding: MovieCommentFragmentBinding) :
    RecyclerView.ViewHolder(binding.root), KoinComponent {
    private val repository: Repository by inject()

    fun bind(
        comment: CommentEntity,
        addCommentBinding: AddCommentFragmentBinding,
        addCommentDialog: AlertDialog
    ) {
        val isOwner = comment.userEmail == FirebaseAuth.getInstance().currentUser?.email
        if (isOwner) {
            binding.editCommentButton.visibility = View.VISIBLE
            binding.editCommentButton.setOnClickListener {
                addCommentBinding.commentTitle.setText(comment.title)
                addCommentBinding.commentBody.setText(comment.body)
                addCommentBinding.materialButton.text = "Edit comment"
                addCommentBinding.materialButton.setOnClickListener {
                    comment.title = addCommentBinding.commentTitle.text.toString()
                    comment.body = addCommentBinding.commentBody.text.toString()
                    repository.editUserComment(comment)
                    addCommentDialog.dismiss()
                }
                addCommentDialog.show()
            }

            binding.deleteCommentButton.visibility = View.VISIBLE
            binding.deleteCommentButton.setOnClickListener {
                repository.deleteUserComment(comment.id)
            }
        }
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
