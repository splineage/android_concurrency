package com.example.testapplication

import com.example.testapplication.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi

class UserManager(private val dataSource: DataSource) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getUser(id: Int): User{
        val name = dataSource.getNameAsync(id)
        val age = dataSource.getAgeAsync(id)
        val profession = dataSource.getProfessionAsync(id)

        return User(
            name.await(),
            age.await(),
            profession.await()
        )
    }
}