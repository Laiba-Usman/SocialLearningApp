package com.example.sociallearningapp.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sociallearningapp.data.repository.ChatRepository
import com.example.sociallearningapp.data.repository.UserRepository
import com.example.sociallearningapp.domain.model.ChatMessage
import com.example.sociallearningapp.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val messages = chatRepository.getMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUserProfile()
            _currentUser.value = user
        }
    }

    fun sendMessage(content: String) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val message = ChatMessage(
                id = generateMessageId(),
                content = content,
                senderId = user.id,
                senderName = user.name,
                timestamp = System.currentTimeMillis()
            )
            chatRepository.sendMessage(message)
        }
    }

    private fun generateMessageId(): String {
        return "msg_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}

