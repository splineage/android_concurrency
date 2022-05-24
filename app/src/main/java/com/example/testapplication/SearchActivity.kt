package com.example.testapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.adapter.ArticleAdapter
import com.example.testapplication.adapter.ArticleLoader
import com.example.testapplication.databinding.ActivitySearchBinding
import kotlinx.coroutines.*

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
                ResultsCounter.reset()
                search()
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            updateCounter()
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

    private suspend fun updateCounter(){
        val notifications = ResultsCounter.getNotificationChannel()
        while (!notifications.isClosedForReceive){
            val newAmount = notifications.receive()
            withContext(Dispatchers.Main){
                binding.result.text = "Results: $newAmount"
            }
        }
    }

    override suspend fun loadMore() {

    }
}