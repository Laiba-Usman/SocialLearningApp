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
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")
    private val auth = FirebaseAuth.getInstance()

    suspend fun createUserProfile(user: User) {
        val currentUser = auth.currentUser ?: throw Exception("No authenticated user")
        usersRef.child(currentUser.uid).setValue(user).await()
    }

    suspend fun getUserProfile(userId: String): User? {
        val snapshot = usersRef.child(userId).get().await()
        return snapshot.getValue(User::class.java)
    }

    suspend fun getCurrentUserProfile(): User? {
        val currentUser = auth.currentUser ?: return null
        return getUserProfile(currentUser.uid)
    }

    fun isFirstLaunch(): Boolean {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("first_launch", true)
    }

    fun completeOnboarding() {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("first_launch", false).apply()
    }
}
