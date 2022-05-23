package com.example.testapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.adapter.ArticleAdapter
import com.example.testapplication.adapter.ArticleLoader
import com.example.testapplication.databinding.ActivitySearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchActivity: AppCompatActivity(), ArticleLoader {
    lateinit var binding: ActivitySearchBinding
    lateinit var viewAdapter: ArticleAdapter
    lateinit var searcher: Searcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        searcher = Searcher()
        viewAdapter = ArticleAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = viewAdapter
        binding.btnSearch.setOnClickListener{
            viewAdapter.clear()
            GlobalScope.launch {
                search()
            }
        }

    }

    private suspend fun search(){
        val query = binding.etSearchText.text.toString()
        val channel = searcher.search(query)
        while (!channel.isClosedForReceive){
            val article = channel.receive()
            GlobalScope.launch(Dispatchers.Main) {
                viewAdapter.add(article)
            }
        }
    }

    override suspend fun loadMore() {

    }
}