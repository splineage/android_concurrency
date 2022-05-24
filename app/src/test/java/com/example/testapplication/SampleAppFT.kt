package com.example.testapplication


import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue

import org.junit.Test
import java.util.Calendar

class SampleAppFT {
    @Test
    fun testSuccess() = runBlocking {
        val manager = UserManager(MockDataSource())
        val user = manager.getUser(10)
        assertTrue(user.name == "Kim")
        assertTrue(user.age == Calendar.getInstance().get(Calendar.YEAR)-1982)
        assertTrue(user.profession == "Professions")
    }

    @Test
    fun testFail() = runBlocking {
        val manager = UserManager(MockSlowDataSource())
        val user = manager.getUser(10)
        assertTrue(user.name == "Kim")
        assertTrue(user.age == Calendar.getInstance().get(Calendar.YEAR)-1982)
        assertTrue(user.profession == "Professions")
    }
}