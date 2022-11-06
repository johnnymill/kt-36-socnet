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
}