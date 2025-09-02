package com.example.sociallearningapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sociallearningapp.presentation.auth.AuthScreen
import com.example.sociallearningapp.presentation.auth.AuthViewModel
import com.example.sociallearningapp.presentation.main.MainScreen
import com.example.sociallearningapp.presentation.onboarding.OnboardingScreen
import com.example.sociallearningapp.presentation.auth.LoginScreen
import com.example.sociallearningapp.presentation.auth.SignUpScreen


@Composable
fun SocialLearningApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val isFirstLaunch by authViewModel.isFirstLaunch.collectAsState()

    NavHost(
        navController = navController,
        startDestination = when {
            authState.isAuthenticated && !isFirstLaunch -> "main"
            authState.isAuthenticated && isFirstLaunch -> "onboarding"
            else -> "login"
        }
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate("signup")
                },
                onLoginSuccess = {
                    navController.navigate("onboarding") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate("onboarding") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen()
        }
    }
}