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
class QuizRepository @Inject constructor() {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getQuizResultsRef(): DatabaseReference? {
        val currentUser = auth.currentUser ?: return null
        return database.getReference("quiz_results").child(currentUser.uid)
    }

    suspend fun saveQuizResult(result: QuizResult) {
        val ref = getQuizResultsRef() ?: return
        ref.push().setValue(result).await()
    }

    fun getQuizHistory(): Flow<List<QuizResult>> = callbackFlow {
        val ref = getQuizResultsRef()
        if (ref == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val results = mutableListOf<QuizResult>()
                for (child in snapshot.children) {
                    child.getValue(QuizResult::class.java)?.let { result ->
                        results.add(result)
                    }
                }
                results.sortByDescending { it.timestamp }
                trySend(results)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}

