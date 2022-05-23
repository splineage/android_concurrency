package com.example.testapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.R
import com.example.testapplication.databinding.ArticleBinding
import com.example.testapplication.model.Article
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArticleAdapter(private val loader: ArticleLoader): RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    lateinit var binding: ArticleBinding
    private val articles: MutableList<Article> = mutableListOf()
    private var loading: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 새로운 ViewHolder 를 생성할 때마다 호출.
//        val layout = LayoutInflater.from(parent.context).inflate(R.layout.article, parent, false) as ConstraintLayout
        binding = ArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // View 업데이트 시.
        val article = articles[position]
        // request more articles when needed
        if (!loading && position >= articles.size - 2){
            loading = true
            GlobalScope.launch{
                loader.loadMore()
                loading = false
            }
        }
        holder.bind(article)
    }

    fun add(article: List<Article>){
        this.articles.addAll(article)
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

interface ArticleLoader{
    suspend fun loadMore()
}