package com.vlu.newsreader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vlu.newsreader.databinding.ItemNewsBinding
import com.vlu.newsreader.model.NewsItem

// Student Information
// Full Name: Bùi Duy Anh
// Student ID: 2474802010525

class NewsAdapter(
    private var newsList: List<NewsItem> = emptyList()
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    fun submitList(newList: List<NewsItem>) {
        this.newsList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NewsItem) {
            binding.tvPostId.text = "#ID: ${item.id}"
            binding.tvUserId.text = "Author: User ${item.userId}"
            binding.tvTitle.text = item.title
            binding.tvBody.text = item.body
        }
    }
}
