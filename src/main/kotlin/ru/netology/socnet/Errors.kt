package ru.netology.socnet

class NoteNotFoundException(message: String) : RuntimeException(message)
class PostNotFoundException(message: String) : RuntimeException(message)

enum class Error {
    OK, NOT_FOUND
}
