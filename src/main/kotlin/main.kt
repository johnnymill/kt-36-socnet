import ru.netology.socnet.*

fun main() {
    WallService.add(Post(1, 1, 1, "I'm 1 and it's my first post"))
    WallService.add(Post(2, 2, 2, "I'm 2 and it's my first post"))

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
}
