package ru.netology.socnet

class PostNotFoundException(message: String) : RuntimeException(message)

object WallService {
    private var postId = 0
    private var posts = emptyArray<Post>()
    private var commentId = 0
    private var comments = emptyArray<Comment>()

    fun clear() {
        posts = emptyArray()
        postId = 0
    }

    fun add(post: Post): Post {
        posts += post.copy(id = ++postId)
        return posts.last()
    }

    fun addAttachment(id: Int, attachment: Attachment): Boolean {
        for ((index, post) in posts.withIndex()) {
            if (post.id == id) {
                posts[index] = post.copy(attachments = post.attachments.plusElement(attachment))
                return true
            }
        }
        return false
    }

    fun update(post: Post): Boolean {
        for ((index, currPost) in posts.withIndex()) {
            if (currPost.id == post.id) {
                posts[index] = post.copy(ownerId = currPost.ownerId, date = currPost.date)
                return true
            }
        }
        return false
    }

    fun viewPostById(id: Int) {
        for ((index, post) in posts.withIndex()) {
            if (post.id == id) {
                posts[index] = post.copy(views = Views(post.views.count + 1))
            }
        }
    }

    fun printPosts() {
        for (post in posts) {
            println(post)
        }
    }

    fun createComment(postId: Int, comment: Comment): Comment {
        for (post in posts) {
            if (post.id == postId) {
                comments += comment.copy(id = ++commentId, toId = postId)
                return comments.last()
            }
        }
        throw PostNotFoundException("Failed to add a comment to post $postId: post not found")
    }
}
