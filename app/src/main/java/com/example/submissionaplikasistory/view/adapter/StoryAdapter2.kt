package com.example.submissionaplikasistory.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionaplikasistory.databinding.ItemPostWidgetBinding
import com.example.submissionaplikasistory.datasource.model.ListStoryItem

class StoryAdapter2(
    val callback: (String?) -> Unit
): PagingDataAdapter<ListStoryItem, StoryAdapter2.StoryViewHolder>(diffUtil) {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return newItem == oldItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return newItem.id == oldItem.id
            }
        }
    }

    class StoryViewHolder(private val binding: ItemPostWidgetBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            println("data $data")
            binding.apply {
                tvItemName.text = data.name
                tvDate.text = data.createdAt
                tvDescriptionPost.text = data.description
                Glide.with(itemView.context)
                    .load(data.photoUrl)
                    .into(ivItemPhoto)

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
        println("adapter $data")
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener { callback(data.id) }
        }

    }
}