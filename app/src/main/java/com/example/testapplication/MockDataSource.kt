package com.example.testapplication

import kotlinx.coroutines.*
import java.util.Calendar

class MockDataSource: DataSource {
    override fun getNameAsync(id: Int)= CoroutineScope(Dispatchers.Default).async{
        delay(200)
        "Kim"
    }

    override fun getAgeAsync(id: Int) = CoroutineScope(Dispatchers.Default).async{
        delay(500)
        Calendar.getInstance().get(Calendar.YEAR) - 1982
    }

    override fun getProfessionAsync(id: Int) = CoroutineScope(Dispatchers.Default).async{
        delay(2000)
        "Professions"
    }
}