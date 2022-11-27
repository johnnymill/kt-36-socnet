package ru.netology.socnet

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class ChatServiceTest {

    private val SENDER_1 = 1
    private val SENDER_2 = 10
    private val SENDER_UNKNOWN = Int.MAX_VALUE
    private val RECEIVER_1 = 2
    private val RECEIVER_2 = 20
    private val RECEIVER_UNKNOWN = Int.MAX_VALUE

    private val MESSAGE_UNKNOWN = Int.MAX_VALUE

    @Before
    fun clearBeforeTest() {
        ChatService.clear()
    }

    @Test
    fun createMessage() {
        val messageId1 = ChatService.createMessage(SENDER_1, RECEIVER_1, "Sample message from $SENDER_1 to $RECEIVER_1")
        val messageId2 = ChatService.createMessage(RECEIVER_1, SENDER_1, "Sample message from $RECEIVER_1 to $SENDER_1")

        assertEquals(1, messageId1)
        assertEquals(2, messageId2)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteMessage_unknownChat() {
        ChatService.deleteMessage(SENDER_UNKNOWN, RECEIVER_UNKNOWN, MESSAGE_UNKNOWN)
    }

    @Test
    fun deleteMessage_unknownMessage() {
        ChatService.createMessage(SENDER_1, RECEIVER_1, "Sample message from $SENDER_1 to $RECEIVER_1")

        assertFalse(ChatService.deleteMessage(SENDER_1, RECEIVER_1, MESSAGE_UNKNOWN))
    }

    @Test
    fun deleteMessage() {
        val messageId1 = ChatService.createMessage(SENDER_1, RECEIVER_1, "Sample message from $SENDER_1 to $RECEIVER_1")
        val messageId2 = ChatService.createMessage(RECEIVER_1, SENDER_1, "Sample message from $RECEIVER_1 to $SENDER_1")

        assertTrue(ChatService.deleteMessage(SENDER_1, RECEIVER_1, messageId2))
        assertTrue(ChatService.deleteMessage(SENDER_1, RECEIVER_1, messageId1))
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteChat_unknown() {
        ChatService.deleteChat(SENDER_UNKNOWN, RECEIVER_UNKNOWN)
    }

    @Test
    fun deleteChat() {
        ChatService.createMessage(SENDER_1, RECEIVER_1, "Sample message from $SENDER_1 to $RECEIVER_1")

        ChatService.deleteChat(SENDER_1, RECEIVER_1)
        try {
            ChatService.deleteChat(SENDER_1, RECEIVER_1)
        } catch (e: ChatNotFoundException) {
            return
        }

        assertTrue(false)
    }

    @Test
    fun getUnreadChatsCount() {
        assertEquals(0, ChatService.getUnreadChatsCount(SENDER_UNKNOWN))

        ChatService.createMessage(SENDER_1, RECEIVER_1, "Sample message from $SENDER_1 to $RECEIVER_1")
        ChatService.createMessage(RECEIVER_1, SENDER_1, "Sample message from $RECEIVER_1 to $SENDER_1")

        assertEquals(0, ChatService.getUnreadChatsCount(RECEIVER_UNKNOWN))
        assertEquals(1, ChatService.getUnreadChatsCount(SENDER_1))
        assertEquals(1, ChatService.getUnreadChatsCount(RECEIVER_1))

        ChatService.createMessage(SENDER_2, RECEIVER_1, "Sample message from $SENDER_2 to $RECEIVER_1")
        assertEquals(0, ChatService.getUnreadChatsCount(SENDER_2))
        assertEquals(2, ChatService.getUnreadChatsCount(RECEIVER_1))
    }

    @Test
    fun getChats_unknown() {
        assertTrue(ChatService.getChats(SENDER_UNKNOWN).isEmpty())
    }

    @Test
    fun getChats() {
        val noMessage = "Нет сообщений"
        val message11 = "Sample message from $SENDER_1 to $RECEIVER_1"
        val message21 = "Sample message from $SENDER_2 to $RECEIVER_1"

        ChatService.createMessage(SENDER_1, RECEIVER_1, message11)
        assertEquals(1, ChatService.getChats(SENDER_1).size)
        assertEquals(1, ChatService.getChats(SENDER_1).filter {
            it.key == RECEIVER_1 && it.value == noMessage
        }.size)
        assertEquals(1, ChatService.getChats(RECEIVER_1).size)
        assertEquals(1, ChatService.getChats(RECEIVER_1).filter {
            it.key == SENDER_1 && it.value == message11
        }.size)

        ChatService.createMessage(SENDER_2, RECEIVER_1, message21)
        assertEquals(1, ChatService.getChats(SENDER_1).size)
        assertEquals(1, ChatService.getChats(SENDER_1).filter {
            it.key == RECEIVER_1 && it.value == noMessage
        }.size)
        assertEquals(0, ChatService.getChats(SENDER_1).filterKeys {it == SENDER_2 }.size)
        assertEquals(1, ChatService.getChats(SENDER_2).size)
        assertEquals(1, ChatService.getChats(SENDER_2).filter {
            it.key == RECEIVER_1 && it.value == noMessage
        }.size)
        assertEquals(0, ChatService.getChats(SENDER_1).filterKeys {it == SENDER_1 }.size)
        assertEquals(2, ChatService.getChats(RECEIVER_1).size)
        assertEquals(1, ChatService.getChats(RECEIVER_1).filter {
            it.key == SENDER_1 && it.value == message11
        }.size)
        assertEquals(1, ChatService.getChats(RECEIVER_1).filter {
            it.key == SENDER_2 && it.value == message21
        }.size)
    }

    @Test(expected = ChatNotFoundException::class)
    fun getMessages_unknownChat() {
        ChatService.getMessages(SENDER_UNKNOWN, RECEIVER_UNKNOWN, 0, 0)
    }

    @Test(expected = MessageNotFoundException::class)
    fun getMessages_unknownMessage() {
        ChatService.createMessage(SENDER_1, RECEIVER_1, "Sample message from $SENDER_1 to $RECEIVER_1")
        assertTrue(ChatService.getMessages(RECEIVER_1, SENDER_1, MESSAGE_UNKNOWN, 1).isEmpty())
    }

    @Test
    fun getMessages() {
        val messageId1 =
            ChatService.createMessage(SENDER_1, RECEIVER_1, "Sample message from $SENDER_1 to $RECEIVER_1 one")
        val messageId2 =
            ChatService.createMessage(RECEIVER_1, SENDER_1, "Sample message from $RECEIVER_1 to $SENDER_1 one")
        val messageId3 =
            ChatService.createMessage(SENDER_1, RECEIVER_1, "Sample message from $SENDER_1 to $RECEIVER_1 two")
        val messageId4 =
            ChatService.createMessage(SENDER_2, RECEIVER_2, "Sample message from $SENDER_2 to $RECEIVER_2 one")
        val messageId5 =
            ChatService.createMessage(SENDER_2, RECEIVER_2, "Sample message from $SENDER_2 to $RECEIVER_2 two")

        assertEquals(1, ChatService.getMessages(RECEIVER_1, SENDER_1, messageId1, 1).size)
        assertEquals(1, ChatService.getMessages(SENDER_1, RECEIVER_1, messageId2, 1).size)
        assertEquals(1, ChatService.getMessages(RECEIVER_1, SENDER_1, messageId1, 3).size)
        assertTrue(ChatService.getMessages(SENDER_1, RECEIVER_1, messageId2, 1).isEmpty())
        assertTrue(ChatService.getMessages(RECEIVER_1, SENDER_1, messageId3, 2).isEmpty())

        assertTrue(ChatService.getMessages(SENDER_2, RECEIVER_2, messageId4, 2).isEmpty())
        assertEquals(1, ChatService.getMessages(RECEIVER_2, SENDER_2, messageId5, 1).size)
        assertEquals(1, ChatService.getMessages(RECEIVER_2, SENDER_2, messageId4, 2).size)
        assertTrue(ChatService.getMessages(RECEIVER_2, SENDER_2, messageId4, 2).isEmpty())
    }
}
