package com.example.submissionaplikasistory.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionaplikasistory.databinding.ItemPostWidgetBinding
import com.example.submissionaplikasistory.datasource.model.ListStoryItem

class StoryAdapter(
    val callback: (String?) -> Unit
): RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(
            oldItem: ListStoryItem,
            newItem: ListStoryItem
        ): Boolean {
            return newItem.id == oldItem.id
        }
    }

    inner class StoryViewHolder(val binding: ItemPostWidgetBinding): RecyclerView.ViewHolder(binding.root)

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun setList (listStoryItem: List<ListStoryItem?>) {
        asyncListDiffer.submitList(listStoryItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryAdapter.StoryViewHolder {
        val view = ItemPostWidgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(view)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: StoryAdapter.StoryViewHolder, position: Int) {
        val data = asyncListDiffer.currentList[position]
        holder.binding.tvItemName.text = data.name
        holder.binding.tvDate.text = data.createdAt
        holder.binding.tvDescriptionPost.text = data.description
        Glide.with(holder.itemView.context)
            .load(data.photoUrl)
            .into(holder.binding.ivItemPhoto)

        holder.itemView.setOnClickListener { callback(data.id) }
    }

    override fun getItemCount() = asyncListDiffer.currentList.size
}