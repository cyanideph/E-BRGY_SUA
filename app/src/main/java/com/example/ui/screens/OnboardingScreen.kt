package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.DeepOceanBlue
import kotlinx.coroutines.launch

private val OnboardingImageShape = RoundedCornerShape(28.dp)

private data class OnboardingPage(
    val imageName: String,
    val fallbackIcon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val context = LocalContext.current

    // Keep the product order explicit. Alphabetical asset ordering would put
    // Emergency first, which does not match the intended onboarding sequence.
    val pages = listOf(
        OnboardingPage("onboarding-services.png", Icons.Default.Description),
        OnboardingPage("onboarding-emergency.png", Icons.Default.Emergency),
        OnboardingPage("onboarding-governance.png", Icons.Default.Groups)
    )

    val availableAssets = remember {
        context.assets.list("onboarding")
            ?.toSet()
            .orEmpty()
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "e-Barangay Sua",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepOceanBlue
                )
                TextButton(onClick = onFinishOnboarding) {
                    Text(
                        text = "Skip",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val page = pages[pageIndex]
                val imageName = page.imageName.takeIf { it in availableAssets }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(OnboardingImageShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageName != null) {
                        AsyncImage(
                            model = "file:///android_asset/onboarding/$imageName",
                            contentDescription = "Barangay Sua onboarding illustration",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(OnboardingImageShape),
                            // Fit is intentional: the artwork is portrait and contains
                            // its own text. Never crop the image or its embedded copy.
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = page.fallbackIcon,
                            contentDescription = null,
                            tint = DeepOceanBlue,
                            modifier = Modifier.size(72.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 22.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) DeepOceanBlue
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinishOnboarding()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepOceanBlue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (pagerState.currentPage == pages.size - 1) {
                        "Enter Barangay Portal"
                    } else {
                        "Next"
                    },
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}
