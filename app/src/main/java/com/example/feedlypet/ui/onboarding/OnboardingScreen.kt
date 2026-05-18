package com.example.feedlypet.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.feedlypet.R
import com.example.feedlypet.ui.auth.components.PawLogo
import com.example.feedlypet.ui.theme.BrandChocoBg
import com.example.feedlypet.ui.theme.BrandChocoGradientEnd
import com.example.feedlypet.ui.theme.BrandChocoGradientStart
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val emoji: String,
    val titleRes: Int,
    val subtitleRes: Int,
    val showLogo: Boolean = false
)

private val pages = listOf(
    OnboardingPage("🐾", R.string.onboarding_title_1, R.string.onboarding_subtitle_1, showLogo = true),
    OnboardingPage("📅", R.string.onboarding_title_2, R.string.onboarding_subtitle_2),
    OnboardingPage("📡", R.string.onboarding_title_3, R.string.onboarding_subtitle_3),
    OnboardingPage("🔔", R.string.onboarding_title_4, R.string.onboarding_subtitle_4)
)

private val chocoGradient = Brush.linearGradient(
    colors = listOf(BrandChocoGradientStart, BrandChocoGradientEnd)
)

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandChocoBg)
    ) {
        // Pager content — on dark background
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val p = pages[page]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 32.dp, end = 32.dp, top = 80.dp, bottom = 280.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (p.showLogo) {
                    PawLogo()
                } else {
                    Text(p.emoji, style = MaterialTheme.typography.displayLarge)
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    stringResource(p.titleRes),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(p.subtitleRes),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }
        }

        // Skip button
        if (!isLastPage) {
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(end = 8.dp)
            ) { Text(stringResource(R.string.onboarding_skip), color = Color.White.copy(alpha = 0.7f)) }
        }

        // Bottom panel with dots + buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(BrandChocoBg)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(pages.size) { i ->
                    val selected = i == pagerState.currentPage
                    Surface(
                        modifier = Modifier.size(if (selected) 10.dp else 8.dp),
                        shape = CircleShape,
                        color = if (selected) Color.White else Color.White.copy(alpha = 0.35f)
                    ) {}
                }
            }

            if (!isLastPage) {
                Button(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = BrandChocoGradientEnd
                    )
                ) { Text(stringResource(R.string.onboarding_next)) }
            } else {
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = BrandChocoGradientEnd
                    )
                ) { Text(stringResource(R.string.onboarding_login)) }
                OutlinedButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.onboarding_register), color = Color.White) }
            }
        }
    }
}
