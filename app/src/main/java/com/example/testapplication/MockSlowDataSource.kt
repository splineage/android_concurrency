package com.example.testapplication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.util.*

class MockSlowDataSource: DataSource {
    override fun getNameAsync(id: Int)= CoroutineScope(Dispatchers.Default).async{
        delay(1000)
        "Kim"
    }

    override fun getAgeAsync(id: Int) = CoroutineScope(Dispatchers.Default).async{
        delay(500)
        Calendar.getInstance().get(Calendar.YEAR) - 1982
    }

    override fun getProfessionAsync(id: Int) = CoroutineScope(Dispatchers.Default).async{
        delay(200)
        "Professions"
    }
}