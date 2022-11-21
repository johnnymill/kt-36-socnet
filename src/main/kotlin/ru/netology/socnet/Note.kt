package ru.netology.socnet

import java.time.LocalDateTime
import java.time.ZoneOffset

data class Note(
    val id: Int,
    val ownerId: Int,
    var title: String,
    var text: String,
    var comments: Int = 0,
    val date: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
)
