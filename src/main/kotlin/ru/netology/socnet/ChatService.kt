package ru.netology.socnet

object ChatService {
    private var messageId = 0
    private var chats: MutableMap<Pair<Int, Int>, MutableList<Message>> = mutableMapOf()

    private fun <V> MutableMap<Pair<Int, Int>, V>.getRelatedKey(a: Int, b: Int): Pair<Int, Int>? {
        return if (this[a to b] != null)
            a to b
        else if (this[b to a] != null)
            b to a
        else
            null
    }

    private fun <V> MutableMap<Pair<Int, Int>, V>.getRelated(ownerId: Int) =
        filterKeys { it.first == ownerId || it.second == ownerId }

    fun clear() {
        messageId = 0
        chats = mutableMapOf()
    }

    /**
     * Создаёт сообщение (и чат при необходимости)
     *
     * @param fromId        Идентификатор отправителя
     * @param toId          Идентификатор получателя
     * @param text          Текст сообщения
     *
     * @return Идентификатор сообщения
     */
    fun createMessage(fromId: Int, toId: Int, text: String): Int {
        val chatKey = chats.getRelatedKey(fromId, toId)
        if (chatKey == null)
            chats[fromId to toId] = mutableListOf(Message(++messageId, fromId, text))
        else
            chats[chatKey]!!.add(Message(++messageId, fromId, text))

        return messageId
    }

    /**
     * Удаляет сообщение (и пустой чат при необходимости)
     *
     * @param fromId        Идентификатор отправителя
     * @param toId          Идентификатор получателя
     * @param messageId     Идентификатор сообщения
     *
     * @exception ChatNotFoundException
     *
     * @return @c true при успешном удалении; @c false, если сообщение не найдено
     */
    fun deleteMessage(fromId: Int, toId: Int, messageId: Int): Boolean {
        val chatKey = chats.getRelatedKey(fromId, toId) ?:
            throw ChatNotFoundException("Neither [$fromId to $toId] nor [$toId to $fromId] chat is found")
        val messages = chats[chatKey] ?:
            throw ChatNotFoundException("Unexpectedly empty chat")

        val message = try {
            messages.first { m: Message -> m.id == messageId }
        } catch (e: NoSuchElementException) {
            null
        }
        val result = messages.remove(message)

        if (messages.isEmpty())
            chats.remove(chatKey)

        return result
    }

    /**
     * Удаляет чат вместе со всей перепиской (для обоих собеседников)
     *
     * @param fromId        Идентификатор отправителя
     * @param toId          Идентификатор получателя
     *
     * @exception ChatNotFoundException
     */
    fun deleteChat(fromId: Int, toId: Int) {
        val chatKey = chats.getRelatedKey(fromId, toId) ?:
            throw ChatNotFoundException("Neither [$fromId to $toId] nor [$toId to $fromId] chat is found")

        chats.remove(chatKey)
    }

    /**
     * Возвращает количество непрочитанных чатов пользователя
     *
     * @param ownerId       Идентификатор пользователя
     *
     * @return Количество непрочитанных чатов
     */
    fun getUnreadChatsCount(ownerId: Int): Int {
        return chats.getRelated(ownerId).count { chat ->
            chat.value.count { !it.isRead && it.senderId != ownerId } > 0
        }
    }

    /**
     * Возвращает последние сообщения во всех чатах пользователя.
     * Если в чате нет новых сообщений, возвращает "Нет сообщений"
     *
     * @param ownerId       Идентификатор пользователя
     *
     * @return Список идентификаторов собеседников пользователя с их
     * последними сообщениями
     */
    fun getChats(ownerId: Int): Map<Int, String> =
        chats.getRelated(ownerId).asIterable()
            .associateBy( keySelector = { if (it.key.first != ownerId) it.key.first else it.key.second },
                          valueTransform = {
                              it.value.lastOrNull {
                                      msg: Message -> !msg.isRead && (msg.senderId != ownerId)
                              }?.text ?: "Нет сообщений"})

    /**
     * Возвращает список непрочитанных сообщений чата и помечает их прочитанными
     *
     * @param ownerId               Идентификатор пользователя
     * @param interlocutorId        Идентификатор собеседника
     * @param messageIdOffset       Идентификатор сообщения, начиная с которого необходимо
     *                              формировать итоговый список
     * @param messageCount          Максимальное количество непрочитанных сообщений
     *
     * @exception ChatNotFoundException
     *
     * @return Список непрочитанных сообщений
     */
    fun getMessages(ownerId: Int, interlocutorId: Int, messageIdOffset: Int, messageCount: Int): List<Message> {
        val chatKey = chats.getRelatedKey(ownerId, interlocutorId) ?:
            throw ChatNotFoundException("Neither [$ownerId to $interlocutorId] nor [$interlocutorId to $ownerId] chat is found")
        return chats[chatKey]!!.asSequence()
            .dropWhile { it.id < messageIdOffset }
            .filter {
                val res = !it.isRead && it.senderId == interlocutorId
                if (res) it.isRead = true
                res
            }
            .take(messageCount)
            .toList()
    }
}
