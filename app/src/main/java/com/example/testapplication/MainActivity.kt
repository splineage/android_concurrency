package com.example.testapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.testapplication.databinding.ActivityMainBinding
import com.example.testapplication.model.Feed
import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val feeds = listOf<String>(
        "https://www.npr.org/rss/rss.php?id=1001",
        "http://rss.cnn.com/rss/cnn_topstories.rss",
        "http://feeds.foxnews.com/foxnews/politics?format=xml",
        "http://error"
    )

    @OptIn(DelicateCoroutinesApi::class)
    private val serviceCallDispatcher = newSingleThreadContext(name = "ServiceCall")

    @OptIn(DelicateCoroutinesApi::class)
    private val multiServiceCallDispatcher = newFixedThreadPoolContext(2, "IO")

    private val factory = DocumentBuilderFactory.newInstance()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        GlobalScope.launch {
//            val headline = fetchRssHeadlines()
//            GlobalScope.launch(Dispatchers.Main) {// UI Dispatchers -> dependency kotlinx-coroutines-android
//                binding.textView.text = "Found ${headline.size} news"
//            }
//            Log.e("TEST",headline.toString())
//        }
//        asyncLoadNews()
        updateAsyncLoadNews()

    }
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    private fun asyncLoadNews(dispatcher: CoroutineDispatcher = serviceCallDispatcher){
        GlobalScope.launch(dispatcher) {
            val headlines = fetchRssHeadlines()
            launch(Dispatchers.Main) {
                binding.textView.text = "Found ${headlines.size} news"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private fun updateAsyncLoadNews() = GlobalScope.launch {
        val requests = mutableListOf<Deferred<List<String>>>()

        newFeeds.mapTo(requests){
            asyncFetchHeadlines(it, multiServiceCallDispatcher)
        }

        requests.forEach{
            it.join()
//            it.await() // error
        }
        val headlines = requests
            .filter { !it.isCancelled }
            .flatMap {
                it.getCompleted()
            }
        val failed = requests
            .filter { it.isCancelled }
            .size
        val obtained = requests.size - failed
        GlobalScope.launch(Dispatchers.Main) {
            binding.textView.text = "Found ${headlines.size} news in ${obtained} feeds"
            if (failed > 0){
                binding.textWarning.text = "Failed to fetch $failed feeds"
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun asyncFetchHeadlines(
        feed: Feed,
        dispatcher: CoroutineDispatcher
    ) = GlobalScope.async(dispatcher) {

        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed.url)
        val news = xml.getElementsByTagName("channel").item(0)
        (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map {
                it.getElementsByTagName("title").item(0).textContent
            }
    }



    private fun fetchRssHeadlines(): List<String>{
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse("https://www.npr.org/rss/rss.php?id=1001")
        val news = xml.getElementsByTagName("channel").item(0)
        return (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map { it.getElementsByTagName("title").item(0).textContent }
    }
}