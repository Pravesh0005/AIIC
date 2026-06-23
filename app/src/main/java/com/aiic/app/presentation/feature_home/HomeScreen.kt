package com.aiic.app.presentation.feature_home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.FeatureCard
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.common.components.GradientText
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.common.components.ScoreCard
import com.aiic.app.common.components.SectionHeader
import com.aiic.app.core.theme.AIICTheme

private data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val navItems = listOf(
    NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
    NavItem("Analytics", Icons.Filled.Analytics, Icons.Outlined.Analytics),
    NavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person),
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToResume: () -> Unit = {},
    onNavigateToInterviewSetup: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToDummy: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var selectedNav by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is com.aiic.app.core.base.UiEvent.Navigate && event.route == "login") {
                onNavigateToLogin()
            }
        }
    }

    Scaffold(
        containerColor = AIICTheme.colors.background,
        bottomBar = {
            NavigationBar(
                containerColor = AIICTheme.colors.navBarBackground,
                tonalElevation = 0.dp,
                modifier = Modifier.navigationBarsPadding(),
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedNav == index,
                        onClick = { selectedNav = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedNav == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        label = {
                            Text(text = item.label, style = AIICTheme.typography.labelSmall)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AIICTheme.colors.navBarSelected,
                            selectedTextColor = AIICTheme.colors.navBarSelected,
                            unselectedIconColor = AIICTheme.colors.navBarUnselected,
                            unselectedTextColor = AIICTheme.colors.navBarUnselected,
                            indicatorColor = AIICTheme.colors.primaryContainer,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedNav) {
                0 -> HomeContent(
                        state = state, 
                        onNavigateToProfile = { selectedNav = 2 },
                        onNavigateToResume = onNavigateToResume,
                        onNavigateToInterviewSetup = onNavigateToInterviewSetup,
                        onNavigateToDummy = onNavigateToDummy
                    )
                1 -> com.aiic.app.presentation.feature_analytics.AnalyticsScreen(
                    interviewsCompleted = state.interviewsCompleted,
                    readinessScore = state.readinessScore
                )
                2 -> com.aiic.app.presentation.feature_profile.ProfileScreen(
                    userName = state.userName,
                    userEmail = state.userEmail,
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToSettings = { selectedNav = 3 },
                    onNavigateToDummy = onNavigateToDummy,
                    onSignOut = onNavigateToLogin
                )
                3 -> com.aiic.app.presentation.feature_settings.SettingsScreen(
                    onNavigateToDummy = onNavigateToDummy,
                    onLogout = { viewModel.onAction(HomeAction.Logout) },
                    onNavigateBack = {}
                )
            }
        }
    }
}



@Composable
private fun HomeContent(
    state: HomeState,
    onNavigateToProfile: () -> Unit,
    onNavigateToResume: () -> Unit,
    onNavigateToInterviewSetup: () -> Unit,
    onNavigateToDummy: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(AIICTheme.spacing.sectionGap),
    ) {
        item { HeroSection(state, onNavigateToProfile, onNavigateToResume, onNavigateToInterviewSetup) }
        item { StatsRow(state) }
        item { QuickActions(onNavigateToResume, onNavigateToInterviewSetup, onNavigateToDummy) }
    }
}



@Composable
private fun HeroSection(
    state: HomeState,
    onNavigateToProfile: () -> Unit,
    onNavigateToResume: () -> Unit,
    onNavigateToInterviewSetup: () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) { alpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AIICTheme.spacing.screenHorizontal)
            .padding(top = AIICTheme.spacing.base)
            .alpha(alpha.value),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Good evening,",
                    style = AIICTheme.typography.bodyMedium,
                    color = AIICTheme.colors.textTertiary,
                )
                Text(
                    text = state.userName,
                    style = AIICTheme.typography.headlineLarge,
                    color = AIICTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(AIICTheme.colors.primary, AIICTheme.colors.accent)
                            )
                        )
                        .clickable { onNavigateToProfile() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.userName.take(1).uppercase(),
                        style = AIICTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AIICTheme.shapes.card)
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            AIICTheme.colors.gradientPrimaryStart.copy(alpha = 0.15f),
                            AIICTheme.colors.gradientPrimaryEnd.copy(alpha = 0.08f),
                        )
                    )
                )
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = AIICTheme.colors.primary,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "INTERVIEW READINESS",
                            style = AIICTheme.typography.overline,
                            color = AIICTheme.colors.primary,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "You're making great progress.",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.textSecondary,
                    )
                    Spacer(Modifier.height(12.dp))
                    PremiumButton(
                        text = "Start Practice",
                        onClick = { onNavigateToInterviewSetup() },
                        modifier = Modifier.width(160.dp),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GradientText(
                        text = "${(state.readinessScore * 100).toInt()}%",
                        style = AIICTheme.typography.displayLarge,
                    )
                    Text(
                        text = "Ready",
                        style = AIICTheme.typography.labelSmall,
                        color = AIICTheme.colors.textTertiary,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsRow(state: HomeState) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = AIICTheme.spacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ScoreCard(
                title = "Interviews",
                score = "${state.interviewsCompleted}",
                subtitle = "Completed",
                modifier = Modifier.width(150.dp),
                accentColor = AIICTheme.colors.secondary,
            )
        }
        item {
            ScoreCard(
                title = "Streak",
                score = "${state.streakDays}",
                subtitle = "Days active",
                modifier = Modifier.width(150.dp),
                accentColor = AIICTheme.colors.warning,
            )
        }
        item {
            ScoreCard(
                title = "Practice",
                score = "${state.hoursOfPractice}h",
                subtitle = "Total hours",
                modifier = Modifier.width(150.dp),
                accentColor = AIICTheme.colors.tertiary,
            )
        }
    }
}

@Composable
private fun QuickActions(
    onNavigateToResume: () -> Unit,
    onNavigateToInterviewSetup: () -> Unit,
    onNavigateToDummy: (String) -> Unit
) {
    Column {
        SectionHeader(title = "Quick Actions", subtitle = "Jump right in")
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier.padding(horizontal = AIICTheme.spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FeatureCard(
                icon = { Icon(Icons.Rounded.Psychology, null, tint = AIICTheme.colors.primary, modifier = Modifier.size(22.dp)) },
                title = "AI Mock Interview",
                description = "Practice with adaptive AI interviewer",
                onClick = { onNavigateToInterviewSetup() }
            )
            FeatureCard(
                icon = { Icon(Icons.Rounded.QuestionAnswer, null, tint = AIICTheme.colors.accent, modifier = Modifier.size(22.dp)) },
                title = "Resume ATS Scanner",
                description = "Scan your resume for ATS score",
                onClick = { onNavigateToResume() }
            )
            FeatureCard(
                icon = { Icon(Icons.Rounded.TrendingUp, null, tint = AIICTheme.colors.secondary, modifier = Modifier.size(22.dp)) },
                title = "Career Roadmap",
                description = "Generate your personalized AI career path",
                onClick = { onNavigateToDummy("Career Roadmap") }
            )
            FeatureCard(
                icon = { Icon(Icons.Rounded.LocalFireDepartment, null, tint = AIICTheme.colors.warning, modifier = Modifier.size(22.dp)) },
                title = "Daily Challenges",
                description = "Solve quick algorithmic puzzles",
                onClick = { onNavigateToDummy("Daily Challenges") }
            )
            FeatureCard(
                icon = { Icon(Icons.Rounded.PlayArrow, null, tint = AIICTheme.colors.tertiary, modifier = Modifier.size(22.dp)) },
                title = "Voice Interview Mode",
                description = "Real-time spoken technical interview",
                onClick = { onNavigateToDummy("Voice Interview Mode") }
            )
        }
    }
}


