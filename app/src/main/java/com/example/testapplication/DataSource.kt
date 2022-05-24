package com.example.testapplication

import kotlinx.coroutines.Deferred

interface DataSource {
    fun getNameAsync(id: Int): Deferred<String>
    fun getAgeAsync(id: Int): Deferred<Int>
    fun getProfessionAsync(id: Int): Deferred<String>
}