package com.example.testapplication.producer

import com.example.testapplication.ELEMENT_TAG_NAME_CHANNEL
import com.example.testapplication.ELEMENT_TAG_NAME_DESCRIPTION
import com.example.testapplication.ELEMENT_TAG_NAME_TITLE
import com.example.testapplication.model.Article
import com.example.testapplication.model.Feed
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.newFixedThreadPoolContext
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

object ArticleProducer {
    private val feeds = listOf(
        Feed("npr","https://www.npr.org/rss/rss.php?id=1001"),
        Feed("cnn", "http://rss.cnn.com/rss/cnn_topstories.rss"),
        Feed("fox", "http://feeds.foxnews.com/foxnews/politics?format=xml")
    )
    private val dispatcher = newFixedThreadPoolContext(2, "IO")
    private val factory = DocumentBuilderFactory.newInstance()
    private fun fetchArticles(feed: Feed): List<Article>{
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed.url)
        val news = xml.getElementsByTagName(ELEMENT_TAG_NAME_CHANNEL).item(0)
        return (0 until news.childNodes.length)
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
    val producer = GlobalScope.produce(dispatcher) {
        feeds.forEach{
            send(fetchArticles(it))
        }
    }
}