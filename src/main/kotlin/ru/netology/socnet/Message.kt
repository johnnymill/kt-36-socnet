package ru.netology.socnet

import java.time.LocalDateTime
import java.time.ZoneOffset

data class Message(
    val id: Int,
    val senderId: Int,
    var text: String,
    var isRead: Boolean = false,
    val date: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
)
