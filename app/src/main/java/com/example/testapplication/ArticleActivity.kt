package com.example.testapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.adapter.ArticleAdapter
import com.example.testapplication.databinding.ActivityArticleBinding
import com.example.testapplication.databinding.ActivityMainBinding
import com.example.testapplication.model.Article
import com.example.testapplication.model.Feed
import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class ArticleActivity: AppCompatActivity() {
    lateinit var binding: ActivityArticleBinding

    @OptIn(DelicateCoroutinesApi::class)
    private val serviceCallDispatcher = newFixedThreadPoolContext(2, "IO")

    private val factory = DocumentBuilderFactory.newInstance()

    private lateinit var viewAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article)
        viewAdapter = ArticleAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = viewAdapter
        asyncLoadArticle()
    }

    private fun asyncLoadArticle() = GlobalScope.launch {
        val requests = mutableListOf<Deferred<List<Article>>>()
        newFeeds.mapTo(requests){
            asyncFetchArticles(it, serviceCallDispatcher)
        }
        requests.forEach{
            it.join()
        }

        val article = requests.filter { !it.isCancelled }
            .flatMap { it.getCompleted() }

        val failedCount = requests.filter { it.isCancelled }.size
        val obtained = requests.size - failedCount

        launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.GONE
            viewAdapter.add(article)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun asyncFetchArticles(
        feed: Feed,
        dispatcher: CoroutineDispatcher
    ) = GlobalScope.async(dispatcher) {
        // for check progress bar
        delay(1000)
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed.url)
        val news = xml.getElementsByTagName(ELEMENT_TAG_NAME_CHANNEL).item(0)
        (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map {
                val title = it.getElementsByTagName(ELEMENT_TAG_NAME_TITLE)
                    .item(0)
                    .textContent
                var summary = it.getElementsByTagName(ELEMENT_TAG_NAME_DESCRIPTION)
                    .item(0)
                    .textContent
                if (!summary.startsWith("<div") && summary.contains("<div")){
                    summary = summary.substring(0, summary.indexOf("<div"))
                }
                Article(feed.name, title, summary)
            }

    }
}