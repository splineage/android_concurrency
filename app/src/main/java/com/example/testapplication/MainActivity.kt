package com.example.testapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.testapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    @OptIn(DelicateCoroutinesApi::class)
    private val serviceCallDispatcher = newSingleThreadContext(name = "ServiceCall")
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
        asyncLoadNews()

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