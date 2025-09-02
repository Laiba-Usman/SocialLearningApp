package com.example.sociallearningapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sociallearningapp.data.repository.AuthRepository
import com.example.sociallearningapp.data.repository.QuizRepository
import com.example.sociallearningapp.data.repository.TaskRepository
import com.example.sociallearningapp.data.repository.UserRepository
import com.example.sociallearningapp.domain.model.QuizResult
import com.example.sociallearningapp.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val quizRepository: QuizRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    val quizHistory = quizRepository.getQuizHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val tasksCount = taskRepository.getTasks()
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            try {
                val user = userRepository.getCurrentUserProfile()
                _profileState.value = ProfileState(user = user)
            } catch (e: Exception) {
                _profileState.value = ProfileState(error = e.message)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}

