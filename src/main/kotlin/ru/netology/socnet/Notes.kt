package ru.netology.socnet

object Notes {
    private var noteId = 0
    private var commentId = 0
    // Notes' Key is an owner ID
    private var notes: MutableMap<Int, MutableMap<Int, Note>> = mutableMapOf()
    // Comments' Key is a note ID
    private var comments: MutableMap<Int, MutableMap<Int, Comment>> = mutableMapOf()
    // Mapping a comment ID (Key) to a note ID (Value), to accelerate an access
    private var mapCommentNote: MutableMap<Int, Int> = mutableMapOf()
    private var commentsTrash: MutableMap<Int, MutableMap<Int, Comment>> = mutableMapOf()

    fun clear() {
        noteId = 0
        commentId = 0
        notes = mutableMapOf()
        comments = mutableMapOf()
        commentsTrash = mutableMapOf()
        mapCommentNote = mutableMapOf()
    }

    /**
     * Создает новую заметку у текущего пользователя.
     *
     * @param ownerId       Идентификатор владельца заметки.
     * @param title         Заголовок заметки.
     * @param text          Текст заметки.
     *
     * @return Идентификатор созданной заметки.
     */
    fun add(ownerId: Int, title: String, text: String): Int {
        val note = Note(++noteId, ownerId, title, text)

        if (notes[ownerId] == null) {
            notes[ownerId] = mutableMapOf(Pair(note.id, note))
        } else {
            notes[ownerId]!![note.id] = note
        }

        return note.id
    }

    /**
     * Удаляет заметку текущего пользователя.
     *
     * @param ownerId       Идентификатор владельца заметки.
     * @param noteId        Идентификатор заметки.
     *
     * @return Код ошибки.
     */
    fun delete(ownerId: Int, noteId: Int) =
        when {
            notes[ownerId] == null -> Error.NOT_FOUND
            notes[ownerId]!![noteId] == null -> Error.NOT_FOUND
            else -> {
                notes[ownerId]!!.remove(noteId)
                for (cmt in comments[noteId]?.keys.orEmpty() + commentsTrash[noteId]?.keys.orEmpty()) {
                    mapCommentNote.remove(cmt)
                }
                comments.remove(noteId)
                commentsTrash.remove(noteId)
                Error.OK
            }
        }

    /**
     * Редактирует заметку текущего пользователя.
     *
     * @param ownerId       Идентификатор владельца заметки.
     * @param noteId        Идентификатор заметки.
     * @param title         Заголовок заметки.
     * @param text          Текст заметки.
     *
     * @return Код ошибки.
     */
    fun edit(ownerId: Int, noteId: Int, title: String, text: String) =
        when {
            notes[ownerId] == null -> Error.NOT_FOUND
            notes[ownerId]!![noteId] == null -> Error.NOT_FOUND
            else -> {
                val original = notes[ownerId]!![noteId]
                notes[ownerId]!![noteId] = original!!.copy(title = title, text = text)
                Error.OK
            }
        }

    /**
     * Возвращает список заметок, созданных пользователем.
     *
     * @param ownerId       Идентификатор владельца заметок.
     * @param noteIds       Идентификаторы заметок, информацию о которых необходимо получить,
     *                      или ничего, чтобы вернуть все заметки пользователя.
     *
     * @exception NoteNotFoundException
     *
     * @return Список объектов Note.
     */
    fun get(ownerId: Int, vararg noteIds: Int): List<Note> {
        if (notes[ownerId] == null)
            throw NoteNotFoundException("Unknown owner with ID $ownerId")

        var requestedNotes: List<Note> = listOf<Note>()
        if (noteIds.isEmpty()) {
            requestedNotes = notes[ownerId]?.values?.toList() ?: listOf<Note>()
        } else {
            for (id in noteIds) {
                requestedNotes += getById(ownerId, id) ?: throw NoteNotFoundException("Failed to find a note with ID $id")
            }
        }

        return requestedNotes
    }

    /**
     * Возвращает заметку по её ID.
     *
     * @param ownerId       Идентификатор владельца заметки.
     * @param noteId        Идентификатор заметки.
     *
     * @return Объект Note или @c null, если либо заметки с таким ID нет
     * или/и владелца не существует.
     */
    fun getById(ownerId: Int, noteId: Int): Note? =
        when {
            notes[ownerId] == null -> null
            notes[ownerId]!![noteId] == null -> null
            else -> notes[ownerId]!![noteId]
        }

    /**
     * Добавляет новый комментарий к заметке.
     *
     * @param ownerId       Идентификатор владельца заметки.
     * @param noteId        Идентификатор заметки.
     * @param fromId        Идентификатор владельца комментария.
     * @param message       Текст комментария.
     *
     * @exception NoteNotFoundException
     *
     * @return Идентификатор созданного комментария.
     */
    fun createComment(ownerId: Int, noteId: Int, fromId: Int, message: String): Int {
        val note = getById(ownerId, noteId)
            ?: throw NoteNotFoundException("Failed to find a note with ID $noteId and owner ID $ownerId")

        val comment = Comment(++commentId, noteId, fromId, message)

        if (comments[noteId] == null) {
            comments[noteId] = mutableMapOf(Pair(comment.id, comment))
        } else {
            comments[noteId]!![comment.id] = comment
        }

        note.comments++
        mapCommentNote[comment.id] = note.id
        return comment.id
    }

    /**
     * Удаляет комментарий к заметке.
     *
     * @param ownerId       Идентификатор владельца заметки.
     * @param commentId     Идентификатор комментария.
     * @param unrecoverable Способ удаления, @c true - без возможности восстановления.
     *
     * @return Код ошибки.
     */
    fun deleteComment(ownerId: Int, commentId: Int, unrecoverable: Boolean = false): Error {
        val noteId = mapCommentNote[commentId]
            ?: return Error.NOT_FOUND
        val note = getById(ownerId, noteId)
            ?: return Error.NOT_FOUND

        val comment = comments[noteId]?.remove(commentId)
        if (comment != null) {
            note.comments--
            if (!unrecoverable) {
                if (commentsTrash[noteId] == null) {
                    commentsTrash[noteId] = mutableMapOf(Pair(comment.id, comment))
                } else {
                    commentsTrash[noteId]!![comment.id] = comment
                }
            }
            return Error.OK
        }

        return Error.NOT_FOUND
    }

    /**
     * Редактирует указанный комментарий у заметки.
     *
     * @param ownerId       Идентификатор владельца заметки.
     * @param commentId     Идентификатор комментария.
     * @param message       Новый текст комментария.
     *
     * @return Код ошибки.
     */
    fun editComment(ownerId: Int, commentId: Int, message: String): Error {
        if (notes[ownerId] == null || notes[ownerId]!!.isEmpty()) {
            return Error.NOT_FOUND
        }
        val noteId = mapCommentNote[commentId]
        if (noteId != null) {
            // comments[noteId] cannot be null if mapCommentNote is valid
            val comment = comments[noteId]!![commentId]
            if (comment != null) {
                comments[noteId]!![commentId] = comment.copy(text = message)
                return Error.OK
            }
        }
        return Error.NOT_FOUND
    }

    /**
     * Возвращает список комментариев к заметке.
     *
     * @param ownerId       Идентификатор владельца заметки.
     * @param noteId        Идентификатор заметки.
     *
     * @exception NoteNotFoundException
     *
     * @return Массив объектов Comment.
     */
    fun getComments(ownerId: Int, noteId: Int): Array<Comment> {
        if (notes[ownerId] == null) {
            throw NoteNotFoundException("Failed to find any user with ID $ownerId")
        } else if (notes[ownerId]!![noteId] == null) {
            throw NoteNotFoundException("Failed to find a note with ID $noteId for user with ID $ownerId")
        }

        return if (comments[noteId] == null) arrayOf() else comments[noteId]!!.values.toTypedArray()
    }

    /**
     * Восстанавливает удалённый комментарий.
     *
     * @param ownerId       Идентификатор владельца заметки.
     * @param commentId     Идентификатор удаленного комментария.
     *
     * @return Код ошибки.
     */
    fun restoreComment(ownerId: Int, commentId: Int): Error {
        val noteId = mapCommentNote[commentId]
            ?: return Error.NOT_FOUND
        val note = getById(ownerId, noteId)
            ?: return Error.NOT_FOUND

        val comment = commentsTrash[noteId]?.remove(commentId)
        if (comment != null) {
            comments[noteId]!![comment.id] = comment
            note.comments++
            return Error.OK
        }

        return Error.NOT_FOUND
    }
}
