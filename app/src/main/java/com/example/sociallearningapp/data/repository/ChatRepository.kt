package com.example.sociallearningapp.data.repository

import android.content.Context
import com.example.sociallearningapp.domain.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor() {
    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.getReference("chat_messages")

    suspend fun sendMessage(message: ChatMessage) {
        messagesRef.child(message.id).setValue(message).await()
    }

    fun getMessages(): Flow<List<ChatMessage>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                for (child in snapshot.children) {
                    child.getValue(ChatMessage::class.java)?.let { message ->
                        messages.add(message)
                    }
                }
                messages.sortBy { it.timestamp }
                trySend(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        messagesRef.addValueEventListener(listener)
        awaitClose { messagesRef.removeEventListener(listener) }
    }
}
