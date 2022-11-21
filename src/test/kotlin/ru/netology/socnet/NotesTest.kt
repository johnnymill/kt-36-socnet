package ru.netology.socnet

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class NotesTest {

    private val ownerId = 1
    private val unknownOwnerId = Int.MAX_VALUE
    private val unknownNoteId = Int.MAX_VALUE
    private val unknownCommentId = Int.MAX_VALUE

    @Before
    fun clearBeforeTest() {
        Notes.clear()
    }

    @Test
    fun add() {
        val noteId1 = Notes.add(ownerId, "The first note", "Sample text one")
        val noteId2 = Notes.add(ownerId, "The second note", "Sample text two")

        assert(noteId1 == 1 && noteId2 == 2)
    }

    @Test
    fun delete_unknownOwner() {
        val error = Notes.delete(unknownOwnerId, unknownNoteId)

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun delete_unknownNote() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val error = Notes.delete(ownerId, unknownNoteId)

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun delete_withComments() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId = Notes.createComment(ownerId, noteId, 1, "Sample comment")
        Notes.deleteComment(ownerId, commentId, false)
        val error = Notes.delete(ownerId, noteId)

        assertEquals(Error.OK, error)

        val note = Notes.getById(ownerId, noteId)
        assertNull(note)

        try {
            val comments = Notes.getComments(ownerId, noteId)
            assertEquals(0, comments.size)
        } catch (e: NoteNotFoundException) {
            assertTrue(true)
        }
    }

    @Test
    fun delete() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val error = Notes.delete(ownerId, noteId)

        assertEquals(Error.OK, error)
    }

    @Test
    fun edit_unknownOwner() {
        val error = Notes.edit(unknownOwnerId, unknownNoteId, "", "")

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun edit_unknownNote() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val error = Notes.edit(ownerId, unknownNoteId, "", "")

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun edit() {
        val newTitle = "Edited note"
        val newText = "Edited text"
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val error = Notes.edit(ownerId, noteId, newTitle, newText)

        assertEquals(Error.OK, error)

        val note = Notes.getById(ownerId, noteId)
        assert(note?.id == noteId
                && note?.ownerId == ownerId
                && note?.title == newTitle
                && note?.text == newText)
    }

    @Test(expected = NoteNotFoundException::class)
    fun get_unknownOwner() {
        val error = Notes.get(unknownOwnerId, unknownNoteId)
    }

    @Test(expected = NoteNotFoundException::class)
    fun get_unknownNote() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val notes = Notes.get(ownerId, noteId, unknownNoteId)
    }

    @Test
    fun get_all() {
        val titles = arrayOf("Note 1", "Note 2", "Note 3")
        val texts = arrayOf("Text 1", "Text 2", "Text 3")
        var ids: IntArray = intArrayOf()
        for (i in titles.indices) {
            ids += Notes.add(ownerId, titles[i], texts[i])
        }
        val notes = Notes.get(ownerId)

        assertEquals(ids.size, notes.size)
        for (n in notes) {
            val i = ids.indexOf(n.id)
            assert(i != -1)
            assert(n.title == titles[i] && n.text == texts[i])
        }
    }

    @Test
    fun get() {
        val titles = arrayOf("Note 1", "Note 2", "Note 3")
        val texts = arrayOf("Text 1", "Text 2", "Text 3")
        var ids: IntArray = intArrayOf()
        for (i in titles.indices) {
            ids += Notes.add(ownerId, titles[i], texts[i])
        }
        val notes = Notes.get(ownerId, ids[0], ids[ids.lastIndex])

        assertEquals(ids.size - 1, notes.size)
        for (n in notes) {
            val i = ids.indexOf(n.id)
            assert(i != -1)
            assert(n.title == titles[i] && n.text == texts[i])
        }
    }

    @Test
    fun getById_unknownOwner() {
        val note = Notes.getById(unknownOwnerId, unknownNoteId)

        assertNull(note)
    }

    @Test
    fun getById_unknownNote() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val note = Notes.getById(ownerId, unknownNoteId)

        assertNull(note)
    }

    @Test
    fun getById() {
        val title = "Sample note"
        val text = "Sample text"
        val noteId = Notes.add(ownerId, title, text)
        val note = Notes.getById(ownerId, noteId)

        assertNotNull(note)
        assert(note?.id == noteId
                && note?.ownerId == ownerId
                && note?.title == title
                && note?.text == text)
    }

    @Test(expected = NoteNotFoundException::class)
    fun createComment_unknownOwner() {
        val commentId = Notes.createComment(unknownOwnerId, unknownNoteId, 1, "Sample comment")
    }

    @Test(expected = NoteNotFoundException::class)
    fun createComment_unknownNote() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId = Notes.createComment(ownerId, unknownNoteId, 1, "Sample comment")
    }

    @Test
    fun createComment() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId1 = Notes.createComment(ownerId, noteId, 1, "Sample comment one")

        assertEquals(1, commentId1)

        val note = Notes.getById(ownerId, noteId)
        assertEquals(1, note!!.comments)

        val commentId2 = Notes.createComment(ownerId, noteId, 2, "Sample comment two")
        assertEquals(2, commentId2)
        assertEquals(2, note!!.comments)
    }

    @Test
    fun deleteComment_unknownOwner() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId = Notes.createComment(ownerId, noteId, 1, "Sample comment")
        val error = Notes.deleteComment(unknownOwnerId, commentId)

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun deleteComment_unknownNote() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId = Notes.createComment(ownerId, noteId, 1, "Sample comment")
        Notes.deleteComment(ownerId, commentId, true)
        val error = Notes.deleteComment(ownerId, commentId)

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun deleteComment_unknownComment() {
        val error = Notes.deleteComment(unknownOwnerId, unknownCommentId)

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun deleteComment_toTrash() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId1 = Notes.createComment(ownerId, noteId, 1, "Sample comment one")
        val commentId2 = Notes.createComment(ownerId, noteId, 2, "Sample comment two")
        val note = Notes.getById(ownerId, noteId)

        // Delete the first comment
        var error = Notes.deleteComment(ownerId, commentId1, false)
        assertEquals(Error.OK, error)
        assertEquals(1, note!!.comments)

        var comments = Notes.getComments(ownerId, noteId)
        assertEquals(1, comments.size)
        assertEquals(commentId2, comments[0].id)

        // Delete the second comment
        error = Notes.deleteComment(ownerId, commentId2, false)
        assertEquals(Error.OK, error)
        assertEquals(0, note!!.comments)

        comments = Notes.getComments(ownerId, noteId)
        assertEquals(0, comments.size)
    }

    @Test
    fun deleteComment() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId = Notes.createComment(ownerId, noteId, 1, "Sample comment")
        var error = Notes.deleteComment(ownerId, commentId, true)

        assertEquals(Error.OK, error)

        val note = Notes.getById(ownerId, noteId)
        assertEquals(0, note!!.comments)

        // Ensure the comment is not in a Trash
        error = Notes.restoreComment(ownerId, commentId)
        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun editComment_unknownOwner() {
        val error = Notes.editComment(unknownOwnerId, unknownCommentId, "Edited comment")

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun editComment_unknownNote() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val error = Notes.editComment(ownerId, unknownCommentId, "Edited comment")

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun editComment_unknownComment() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId = Notes.createComment(ownerId, noteId, 1, "Sample comment")
        Notes.deleteComment(ownerId, commentId, true)
        val error = Notes.editComment(ownerId, commentId, "Edited comment")

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun editComment() {
        val message = "Edited comment"
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId = Notes.createComment(ownerId, noteId, 1, "Sample comment")
        val error = Notes.editComment(ownerId, commentId, message)

        assertEquals(Error.OK, error)

        val comments = Notes.getComments(ownerId, noteId)
        assert(comments.size == 1 && comments[0].text == message)
    }

    @Test(expected = NoteNotFoundException::class)
    fun getComments_unknownOwner() {
        val comments = Notes.getComments(unknownOwnerId, unknownNoteId)
    }

    @Test(expected = NoteNotFoundException::class)
    fun getComments_unknownNote() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val comments = Notes.getComments(ownerId, unknownNoteId)
    }

    @Test
    fun getComments() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        var comments = Notes.getComments(ownerId, noteId)

        assertEquals(0, comments.size)

        val commentId1 = Notes.createComment(ownerId, noteId, 1, "Sample comment one")
        comments = Notes.getComments(ownerId, noteId)
        assertEquals(1, comments.size)
        assertEquals(commentId1, comments[0].id)

        val commentId2 = Notes.createComment(ownerId, noteId, 2, "Sample comment two")
        comments = Notes.getComments(ownerId, noteId)
        assertEquals(2, comments.size)
        assertEquals(setOf(commentId1, commentId2), setOf(comments[0].id, comments[1].id))
    }

    @Test
    fun restoreComment_unknownOwner() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId = Notes.createComment(ownerId, noteId, 1, "Sample comment")
        Notes.deleteComment(ownerId, commentId, false)
        val error = Notes.restoreComment(unknownOwnerId, commentId)

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun restoreComment_unknownNote() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId = Notes.createComment(ownerId, noteId, 1, "Sample comment")
        Notes.deleteComment(ownerId, commentId, false)
        Notes.restoreComment(ownerId, commentId)
        val error = Notes.restoreComment(ownerId, commentId)

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun restoreComment_unknownComment() {
        val error = Notes.restoreComment(unknownOwnerId, unknownCommentId)

        assertEquals(Error.NOT_FOUND, error)
    }

    @Test
    fun restoreComments() {
        val noteId = Notes.add(ownerId, "Sample note", "Sample text")
        val commentId1 = Notes.createComment(ownerId, noteId, 1, "Sample comment one")
        val commentId2 = Notes.createComment(ownerId, noteId, 2, "Sample comment two")
        val note = Notes.getById(ownerId, noteId)

        Notes.deleteComment(ownerId, commentId1, false)
        Notes.deleteComment(ownerId, commentId2, false)

        // Restore the first comment
        var error = Notes.restoreComment(ownerId, commentId1)
        assertEquals(Error.OK, error)
        assertEquals(1, note!!.comments)

        var comments = Notes.getComments(ownerId, noteId)
        assertEquals(1, comments.size)
        assertEquals(commentId1, comments[0].id)

        // Restore the second comment
        error = Notes.restoreComment(ownerId, commentId2)
        assertEquals(Error.OK, error)
        assertEquals(2, note!!.comments)

        comments = Notes.getComments(ownerId, noteId)
        assertEquals(2, comments.size)
        assertEquals(setOf(commentId1, commentId2), setOf(comments[0].id, comments[1].id))
    }
}