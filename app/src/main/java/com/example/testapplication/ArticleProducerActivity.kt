package com.example.testapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.adapter.ArticleAdapter
import com.example.testapplication.adapter.ArticleLoader
import com.example.testapplication.databinding.ActivityArticleBinding
import com.example.testapplication.producer.ArticleProducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArticleProducerActivity: AppCompatActivity(), ArticleLoader {
    lateinit var binding: ActivityArticleBinding
    private lateinit var viewAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article)
        viewAdapter = ArticleAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = viewAdapter
        GlobalScope.launch {
            loadMore()
        }
    }

    override suspend fun loadMore() {
        val producer = ArticleProducer.producer
        if (!producer.isClosedForReceive){
            val articles = producer.receive()
            GlobalScope.launch(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                viewAdapter.set(articles)
            }
        }
    }

}