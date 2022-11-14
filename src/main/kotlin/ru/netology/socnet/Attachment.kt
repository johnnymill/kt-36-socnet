package ru.netology.socnet

enum class AttachmentType {
    AUDIO, FILE, GEO, PICTURE, VIDEO
}

interface Attachment {
    val type: AttachmentType
}

data class AudioAttachment(val audio: Audio) : Attachment {
    override val type: AttachmentType = AttachmentType.AUDIO
}

data class Audio(
    val id: Int,
    val ownerId: Int,
    val artist: String,
    val title: String,
    val url: String
)

data class FileAttachment(val file: File) : Attachment {
    override val type: AttachmentType = AttachmentType.FILE
}

data class File(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val url: String
)

data class GeoAttachment(val geo: Geo) : Attachment {
    override val type: AttachmentType = AttachmentType.GEO
}

data class Geo(
    val id: Int,
    val title: String,
    val latitude: Int,
    val longitude: Int
)

data class PictureAttachment(val picture: Picture) : Attachment {
    override val type: AttachmentType = AttachmentType.PICTURE
}

data class Picture(
    val id: Int,
    val ownerId: Int,
    val text: String,
    val url: String
)

data class VideoAttachment(val video: Video) : Attachment {
    override val type: AttachmentType = AttachmentType.VIDEO
}

data class Video(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val url: String
)
