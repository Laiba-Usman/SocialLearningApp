package com.example.sociallearningapp.presentation.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


// Mock Banner Ad Component
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .background(Color(0xFFE3F2FD)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "📱 Banner Ad Space",
            color = Color(0xFF1976D2),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Mock Interstitial Ad Manager
class InterstitialAdManager {
    fun showInterstitial(onAdDismissed: () -> Unit) {
        // Simulate ad display delay
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            kotlinx.coroutines.delay(1000) // Simulate ad duration
            onAdDismissed()
        }
    }
}

