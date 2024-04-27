package com.example.submissionaplikasistory.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionaplikasistory.databinding.ItemPostWidgetBinding
import com.example.submissionaplikasistory.datasource.local.EntityDaoStory
import com.example.submissionaplikasistory.utils.Utils

class StoryAdapter(
    val callback: (String?) -> Unit
): PagingDataAdapter<EntityDaoStory, StoryAdapter.StoryViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<EntityDaoStory>() {
            override fun areItemsTheSame(
                oldItem: EntityDaoStory,
                newItem: EntityDaoStory
            ): Boolean {
                return newItem == oldItem
            }

            override fun areContentsTheSame(
                oldItem: EntityDaoStory,
                newItem: EntityDaoStory
            ): Boolean {
                return newItem.id == oldItem.id
            }
        }
    }

    class StoryViewHolder(private val binding: ItemPostWidgetBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EntityDaoStory, call: (String) -> Unit) {
            binding.apply {
                tvItemName.text = data.name
                tvDate.text = Utils().formatDate(data.createdAt!!)
                tvDescriptionPost.text = data.description
                Glide.with(itemView.context)
                    .load(data.photoUrl)
                    .into(ivItemPhoto)

                itemView.setOnClickListener { call(data.id) }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val view = ItemPostWidgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(view)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data) {
                callback(it)
            }
        }

    }
}