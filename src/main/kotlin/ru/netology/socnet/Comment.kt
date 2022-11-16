package ru.netology.socnet

import java.time.LocalDateTime
import java.time.ZoneOffset

data class Comment(
    val id: Int,
    val toId: Int,
    val fromId: Int,
    val text: String,
    val date: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
)