package ru.netology.socnet

class ChatNotFoundException(message: String) : RuntimeException(message)
class MessageNotFoundException(message: String) : RuntimeException(message)
class NoteNotFoundException(message: String) : RuntimeException(message)
class PostNotFoundException(message: String) : RuntimeException(message)

enum class Error {
    OK, NOT_FOUND
}
