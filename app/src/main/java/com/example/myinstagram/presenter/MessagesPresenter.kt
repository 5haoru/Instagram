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
        conversations = DataRepository.getConversations()
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
        conversations = DataRepository.getConversations()
    }
}
