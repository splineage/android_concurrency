package com.example.testapplication

import com.example.testapplication.model.Feed

val newFeeds = listOf<Feed>(
    Feed("npr","https://www.npr.org/rss/rss.php?id=1001"),
    Feed("cnn", "http://rss.cnn.com/rss/cnn_topstories.rss"),
    Feed("fox", "http://feeds.foxnews.com/foxnews/politics?format=xml"),
    Feed("error", "http://error")
)

const val ELEMENT_TAG_NAME_CHANNEL = "channel"
const val ELEMENT_TAG_NAME_TITLE = "title"
const val ELEMENT_TAG_NAME_DESCRIPTION = "description"
