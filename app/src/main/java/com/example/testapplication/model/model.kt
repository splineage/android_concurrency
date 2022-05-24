package com.example.testapplication.model

data class Feed (
    val name: String,
    val url: String
)

data class Article(
    val feed: String,
    val title: String,
    val summary: String
)

data class User(
    val name: String,
    val age: Int,
    val profession: String
)