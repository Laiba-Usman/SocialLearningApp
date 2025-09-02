package com.example.sociallearningapp.domain.model

// User Model
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = ""
)

// Quiz Models
data class Question(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = ""
)

data class QuizResult(
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val timestamp: Long = 0L
)

// Task Models
enum class TaskStatus {
    ALL, PENDING, COMPLETED
}

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = 0L
)

// Chat Models
data class ChatMessage(
    val id: String = "",
    val content: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: Long = 0L
)

