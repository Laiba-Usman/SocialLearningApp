package com.example.sociallearningapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sociallearningapp.data.repository.AuthRepository
import com.example.sociallearningapp.data.repository.UserRepository
import com.example.sociallearningapp.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val user: User? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isFirstLaunch = MutableStateFlow(true)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                val userProfile = userRepository.getUserProfile(currentUser.uid)
                _authState.value = AuthState(
                    isAuthenticated = true,
                    user = userProfile
                )
                _isFirstLaunch.value = userRepository.isFirstLaunch()
            }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            try {
                val firebaseUser = authRepository.signUp(email, password)
                val user = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email,
                    avatarUrl = ""
                )
                userRepository.createUserProfile(user)
                _authState.value = AuthState(
                    isAuthenticated = true,
                    user = user
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            try {
                val firebaseUser = authRepository.signIn(email, password)
                val userProfile = userRepository.getUserProfile(firebaseUser.uid)
                _authState.value = AuthState(
                    isAuthenticated = true,
                    user = userProfile
                )
                _isFirstLaunch.value = userRepository.isFirstLaunch()
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userRepository.completeOnboarding()
            _isFirstLaunch.value = false
        }
    }
}