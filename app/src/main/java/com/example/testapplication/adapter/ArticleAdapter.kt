package com.example.testapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.R
import com.example.testapplication.databinding.ArticleBinding
import com.example.testapplication.model.Article

class ArticleAdapter: RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    lateinit var binding: ArticleBinding
    val article: MutableList<Article> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 새로운 ViewHolder 를 생성할 때마다 호출.
//        val layout = LayoutInflater.from(parent.context).inflate(R.layout.article, parent, false) as ConstraintLayout
        binding = ArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = article.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // View 업데이트 시.
        val article = article[position]
        holder.bind(article)
    }

    fun add(article: List<Article>){
        this.article.addAll(article)
        notifyDataSetChanged()
    }

    inner class ViewHolder(_binding: ArticleBinding): RecyclerView.ViewHolder(_binding.root){
        private var binding: ArticleBinding = _binding

        fun bind(article: Article){
            binding.feed.text = article.feed
            binding.title.text = article.title
            binding.summary.text = article.summary
        }
    }


}