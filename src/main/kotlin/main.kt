import ru.netology.socnet.*

fun main() {
    WallService.add(Post(1, 1, 1, "I'm 1 and it's my first post"))
    WallService.add(Post(2, 2, 2, "I'm 2 and it's my first post"))
    WallService.add(Post(3, 3, 3, null))

    println("Original posts")
    WallService.printPosts()

    WallService.viewPostById(1)
    val post = Post(2, 3, 3, "I'm 2 and it's my updated post", date = 1667799999)
    WallService.update(post)

    println("Updated posts")
    WallService.printPosts()

    val audio = AudioAttachment(Audio(1, 1, "Pink Floyd", "The Wall", "URL"))
    val video = VideoAttachment(Video(2, 2, "Twin Peaks", "URL"))
    WallService.addAttachment(2, audio)
    WallService.addAttachment(2, video)

    println("Updated posts with attachments")
    WallService.printPosts()

    println("\nComments adding has started")
    val comment1 = Comment(0, 0, 1, "The first comment")
    val comment2 = Comment(0, 0, 2, "The second comment")
    try {
        var comment = WallService.createComment(2, comment1)
        println("Added comment $comment")

        comment = WallService.createComment(200, comment2)
        println("Added comment $comment")
    } catch (e: PostNotFoundException) {
        println("Failed to add comment $comment2")
    } finally {
        println("Comments adding has finished")
    }
}
