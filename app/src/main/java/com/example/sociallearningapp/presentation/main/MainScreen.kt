package com.example.sociallearningapp.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sociallearningapp.presentation.chat.ChatScreen
import com.example.sociallearningapp.presentation.profile.ProfileScreen
import com.example.sociallearningapp.presentation.quiz.QuizScreen
import com.example.sociallearningapp.presentation.tasks.TasksScreen

sealed class BottomNavItem(var route: String, var icon: ImageVector, var title: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Quiz : BottomNavItem("quiz", Icons.Default.List, "Quiz")
    object Tasks : BottomNavItem("tasks", Icons.Default.List, "Tasks")
    object Chat : BottomNavItem("chat", Icons.Default.Home, "Chat") // reuse Home icon for simplicity
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Quiz,
        BottomNavItem.Tasks,
        BottomNavItem.Chat,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Quiz.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Quiz.route) { QuizScreen() }
            composable(BottomNavItem.Tasks.route) { TasksScreen() }
            composable(BottomNavItem.Chat.route) { ChatScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
        }
    }
}
