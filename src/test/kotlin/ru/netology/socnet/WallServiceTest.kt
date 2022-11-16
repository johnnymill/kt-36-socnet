package ru.netology.socnet

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class WallServiceTest {

    @Before
    fun clearBeforeTest() {
        WallService.clear()
    }

    @Test
    fun add() {
        val post1 = WallService.add(Post(0, 0, 0, "Test 'Add a post 1'"))
        val post2 = WallService.add(Post(0, 0, 0, "Test 'Add a post 2'"))

        assertEquals(1, post1.id)
        assertEquals(2, post2.id)
    }

    @Test
    fun updateEmpty() {
        val post = Post(100, 0, 0, "Test trying to update an empty wall")
        val result = WallService.update(post)

        assertFalse(result)
    }

    @Test
    fun updateMissing() {
        WallService.add(Post(0, 0, 0, "The first post"))
        val post = Post(100, 0, 0, "Test trying to update a missing post")
        val result = WallService.update(post)

        assertFalse(result)
    }

    @Test
    fun updateExisting() {
        WallService.add(Post(0, 0, 0, "The first post"))
        val updatedPost = Post(1, 1, 1, "Test trying to update an existing post")
        val result = WallService.update(updatedPost)

        assertTrue(result)
    }

    @Test
    fun addAttachmentEmpty() {
        val video = VideoAttachment(Video(1, 1, "Video sample", "URL sample"))
        val result = WallService.addAttachment(100, video)

        assertFalse(result)
    }

    @Test
    fun addAttachmentMissing() {
        WallService.add(Post(0, 0, 0, "The first post"))
        val video = VideoAttachment(Video(1, 1, "Video sample", "URL sample"))
        val result = WallService.addAttachment(100, video)

        assertFalse(result)
    }

    @Test
    fun addAttachmentExisting() {
        WallService.add(Post(0, 0, 0, "The first post"))
        val video = VideoAttachment(Video(1, 1, "Video sample", "URL sample"))
        val result = WallService.addAttachment(1, video)

        assertTrue(result)
    }

    @Test(expected = PostNotFoundException::class)
    fun createComment_emptyWall() {
        val comment = Comment(0, 0, 1, "Comment sample")
        WallService.createComment(100, comment)
    }

    @Test(expected = PostNotFoundException::class)
    fun createComment_invalidPost() {
        val post = WallService.add(Post(0, 0, 0, "Post sample"))
        val comment = Comment(0, 0, 1, "Comment sample")

        WallService.createComment(post.id + 100, comment)
    }

    @Test
    fun createComment_validPost() {
        val post = WallService.add(Post(0, 0, 0, "Post sample"))
        val commentRef = Comment(0, 0, 1, "Comment sample")

        val comment = try {
            WallService.createComment(post.id, commentRef)
        } catch (e: PostNotFoundException) {
            null
        }

        assert(comment != null
                && comment.id == 1
                && comment.toId == post.id
                && comment.fromId == commentRef.fromId
                && comment.text == commentRef.text
                && comment.date == commentRef.date)
    }
}