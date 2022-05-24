package com.example.testapplication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.newSingleThreadContext

/**
 * Singleton
 */
object ResultsCounter {
    @OptIn(DelicateCoroutinesApi::class)
    private val context = newSingleThreadContext("counter")
    private var counter = 0

    @OptIn(ObsoleteCoroutinesApi::class)
    private val actor = CoroutineScope(context).actor<Action>{
        for (msg in channel){
            when(msg){
                Action.INCREASE -> counter++
                Action.RESET -> counter = 0
            }
            notifications.send(counter)
        }
    }

    private val notifications = Channel<Int> { Channel.CONFLATED }

    suspend fun increment() = actor.send(Action.INCREASE)

    suspend fun reset() = actor.send(Action.RESET)

    fun getNotificationChannel(): ReceiveChannel<Int> = notifications
}

enum class Action{
    INCREASE,
    RESET
}