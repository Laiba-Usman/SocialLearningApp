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
class TaskRepository @Inject constructor() {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getTasksRef(): DatabaseReference? {
        val currentUser = auth.currentUser ?: return null
        return database.getReference("tasks").child(currentUser.uid)
    }

    suspend fun addTask(task: Task) {
        val ref = getTasksRef() ?: return
        ref.child(task.id).setValue(task).await()
    }

    suspend fun toggleTaskComplete(taskId: String) {
        val ref = getTasksRef() ?: return
        val taskRef = ref.child(taskId)
        val snapshot = taskRef.get().await()
        val task = snapshot.getValue(Task::class.java) ?: return

        val newStatus = if (task.status == TaskStatus.COMPLETED)
            TaskStatus.PENDING else TaskStatus.COMPLETED

        taskRef.child("status").setValue(newStatus.name).await()
    }

    suspend fun deleteTask(taskId: String) {
        val ref = getTasksRef() ?: return
        ref.child(taskId).removeValue().await()
    }

    fun getTasks(): Flow<List<Task>> = callbackFlow {
        val ref = getTasksRef()
        if (ref == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = mutableListOf<Task>()
                for (child in snapshot.children) {
                    child.getValue(Task::class.java)?.let { task ->
                        tasks.add(task)
                    }
                }
                tasks.sortByDescending { it.createdAt }
                trySend(tasks)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}


