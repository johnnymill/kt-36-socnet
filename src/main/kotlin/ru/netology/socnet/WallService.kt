package ru.netology.socnet

object WallService {
    private var postId = 0
    private var posts = emptyArray<Post>()

    fun clear() {
        posts = emptyArray()
        postId = 0
    }

    fun add(post: Post): Post {
        posts += post.copy(id = ++postId)
        return posts.last()
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
}
