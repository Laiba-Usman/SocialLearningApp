package com.example.sociallearningapp.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sociallearningapp.presentation.ads.BannerAdView
import com.example.sociallearningapp.presentation.ads.InterstitialAdManager
import com.example.sociallearningapp.presentation.auth.AuthViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToMain: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val interstitialAdManager = remember { InterstitialAdManager() }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            OnboardingPage(
                page = page,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Banner Ad
        BannerAdView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage > 0) {
                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                ) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(64.dp))
            }

            // Page Indicator
            Row {
                repeat(3) { index ->
                    val color = if (index == pagerState.currentPage)
                        MaterialTheme.colorScheme.primary else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .padding(horizontal = 2.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            colors = CardDefaults.cardColors(containerColor = color)
                        ) {}
                    }
                }
            }

            if (pagerState.currentPage < 2) {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = {
                        // Show interstitial ad before finishing
                        interstitialAdManager.showInterstitial {
                            authViewModel.completeOnboarding()
                            onNavigateToMain()
                        }
                    }
                ) {
                    Text("Finish")
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(
    page: Int,
    modifier: Modifier = Modifier
) {
    val (title, description) = when (page) {
        0 -> "Welcome!" to "Welcome to Social Learning App where you can learn, grow and connect with others."
        1 -> "Features" to "Take quizzes, manage tasks, chat with study groups, and track your progress."
        2 -> "Privacy & Terms" to "We value your privacy and ensure your data is secure. By continuing, you agree to our terms of service."
        else -> "Welcome!" to "Welcome to Social Learning App"
    }

    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = description,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

