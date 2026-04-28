package com.example.myinstagram.presenter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myinstagram.data.DataRepository
import com.example.myinstagram.model.Conversation
import com.example.myinstagram.model.Message
import com.example.myinstagram.model.User

class MessagesPresenter {

    var conversations by mutableStateOf<List<Conversation>>(emptyList())
        private set
    var currentUser by mutableStateOf<User?>(null)
        private set

    fun loadData() {
        currentUser = DataRepository.getCurrentUser()
        conversations = sortConversationsByRecentMessage(DataRepository.getConversations())
    }

    fun getUserById(userId: String): User? = DataRepository.getUserById(userId)

    fun getOtherParticipant(conversation: Conversation): User? {
        val otherId = conversation.participantIds.find { it != currentUser?.userId }
        return otherId?.let { DataRepository.getUserById(it) }
    }

    fun getLastMessage(conversation: Conversation): Message? {
        return conversation.messages.lastOrNull()
    }

    fun sendMessage(conversationId: String, text: String) {
        val userId = currentUser?.userId ?: return
        val msg = Message(
            messageId = "msg_${System.currentTimeMillis()}",
            conversationId = conversationId,
            senderId = userId,
            text = text,
            timestamp = "Just now",
            isRead = true
        )
        DataRepository.addMessage(conversationId, msg)
        conversations = sortConversationsByRecentMessage(DataRepository.getConversations())
    }

    private fun sortConversationsByRecentMessage(items: List<Conversation>): List<Conversation> {
        return items.sortedBy { conversation ->
            timestampRank(conversation.messages.lastOrNull()?.timestamp)
        }
    }

    private fun timestampRank(timestamp: String?): Int {
        val value = timestamp?.trim()?.lowercase() ?: return Int.MAX_VALUE
        if (value == "just now" || value == "now") return 0

        val amount = value.substringBefore(" ").toIntOrNull() ?: return Int.MAX_VALUE
        return when {
            "minute" in value -> amount
            "hour" in value -> amount * 60
            "day" in value -> amount * 60 * 24
            "week" in value -> amount * 60 * 24 * 7
            "month" in value -> amount * 60 * 24 * 30
            "year" in value -> amount * 60 * 24 * 365
            else -> Int.MAX_VALUE
        }
    }
}
