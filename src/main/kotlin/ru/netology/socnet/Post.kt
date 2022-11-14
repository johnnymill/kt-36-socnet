package ru.netology.socnet

import java.time.LocalDateTime
import java.time.ZoneOffset

data class Post(
    val id: Int,
    val ownerId: Int,
    val fromId: Int,
    val text: String,
    val date: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val friendsOnly: Boolean = false,
    val isPinned: Boolean = false,
    val views: Views = Views(0),
    val attachments: Array<Attachment> = emptyArray()
)
