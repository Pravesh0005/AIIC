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
    NavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings),
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToResume: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var selectedNav by remember { mutableIntStateOf(0) }

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
                        onNavigateToNotifications = { selectedNav = 3 },
                        onNavigateToResume = onNavigateToResume
                    )
                1 -> com.aiic.app.presentation.feature_analytics.AnalyticsScreen()
                2 -> com.aiic.app.presentation.feature_profile.ProfileScreen(
                    onNavigateToEditProfile = { /* TODO: Hook up nav */ },
                    onNavigateToSettings = { selectedNav = 3 }
                )
                3 -> com.aiic.app.presentation.feature_settings.SettingsScreen(
                    onLogout = { viewModel.onAction(HomeAction.Logout) }
                )
            }
        }
    }
}



@Composable
private fun HomeContent(
    state: HomeState,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToResume: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(AIICTheme.spacing.sectionGap),
    ) {
        item { HeroSection(state, onNavigateToProfile, onNavigateToNotifications, onNavigateToResume) }
        item { StatsRow(state) }
        item { QuickActions(onNavigateToResume) }
        item { RecentActivity(state) }
        item { AnalyticsPreview(state) }
    }
}

                .padding(paddingValues)
        ) {
            when (selectedBottomNav) {
                0 -> { // Home Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 80.dp) // Extra padding for bottom nav
                    ) {
                        HomeHeader(
                            state = state, 
                            onNavigateToProfile = { selectedBottomNav = 2 },
                            onNavigateToDummy = onNavigateToDummy
                        )
                        Spacer(Modifier.height(24.dp))
                        
                        StatsCarousel(state = state)
                        Spacer(Modifier.height(32.dp))
                        
                        QuickActions(onNavigateToResume, onNavigateToDummy)
                        Spacer(Modifier.height(32.dp))
                        
                        HomeTabs(
                            activeTab = activeTab,
                            onTabSelected = { activeTab = it }
                        )
                        
                        when (activeTab) {
                            0 -> RecentActivityList(state.recentActivity, onNavigateToResume)
                            1 -> RecommendedTasks()
                        }
                    }
                }
                1 -> { // Analytics Tab
                    com.aiic.app.presentation.feature_analytics.AnalyticsScreen()
                }
                2 -> { // Profile Tab
                    com.aiic.app.presentation.feature_profile.ProfileScreen(
                        onNavigateToEditProfile = { onNavigateToDummy("Edit Profile") },
                        onNavigateToSettings = { selectedBottomNav = 3 },
                        onNavigateToDummy = onNavigateToDummy,
                        onSignOut = onNavigateToLogin
                    )
                }
                3 -> { // Settings Tab
                    com.aiic.app.presentation.feature_settings.SettingsScreen(
                        onNavigateToDummy = onNavigateToDummy,
                        onLogout = onNavigateToLogin
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    state: HomeUiState,
    onNavigateToProfile: () -> Unit,
    onNavigateToDummy: (String) -> Unit
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
                        .background(AIICTheme.colors.surfaceElevated)
                        .clickable { onNavigateToDummy("Notifications") },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.Notifications,
                        contentDescription = "Notifications",
                        tint = AIICTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }
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
                        onClick = { onNavigateToResume() },
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
                onClick = { onNavigateToResume() }
            )
            FeatureCard(
                icon = { Icon(Icons.Rounded.QuestionAnswer, null, tint = AIICTheme.colors.accent, modifier = Modifier.size(22.dp)) },
                title = "Question Bank",
                description = "Browse 500+ curated questions",
                onClick = { onNavigateToDummy("Question Bank") }
            )
            FeatureCard(
                icon = { Icon(Icons.Rounded.TrendingUp, null, tint = Color(0xFF10B981), modifier = Modifier.size(22.dp)) },
                title = "Skill Assessment",
                description = "Evaluate your strengths & gaps",
                onClick = { onNavigateToDummy("Skill Assessment") }
            )
        }
    }
}

@Composable
private fun RecentActivity(state: HomeState) {
    Column {
        SectionHeader(title = "Recent Activity", action = "See All", onAction = {})
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier.padding(horizontal = AIICTheme.spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActivityItem(
                icon = Icons.Rounded.PlayArrow,
                iconColor = AIICTheme.colors.primary,
                title = "Technical Interview",
                subtitle = "Completed • Score 85%",
                time = "2h ago",
            )
            ActivityItem(
                icon = Icons.Rounded.EmojiEvents,
                iconColor = AIICTheme.colors.warning,
                title = "7-Day Streak Achieved!",
                subtitle = "Keep the momentum going",
                time = "Today",
            )
            ActivityItem(
                icon = Icons.Rounded.LocalFireDepartment,
                iconColor = AIICTheme.colors.accent,
                title = "Behavioral Round",
                subtitle = "Completed • Score 78%",
                time = "Yesterday",
            )
        }
    }
}

@Composable
private fun ActivityItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    time: String,
) {
    PremiumCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(AIICTheme.shapes.medium)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = AIICTheme.typography.titleSmall, color = AIICTheme.colors.textPrimary)
                Text(text = subtitle, style = AIICTheme.typography.bodySmall, color = AIICTheme.colors.textTertiary)
            }
            Text(text = time, style = AIICTheme.typography.caption, color = AIICTheme.colors.textDisabled)
        }
    }
}

@Composable
private fun AnalyticsPreview(state: HomeState) {
    Column {
        SectionHeader(title = "Performance", subtitle = "Your interview analytics")
        Spacer(Modifier.height(12.dp))
        PremiumCard(modifier = Modifier.padding(horizontal = AIICTheme.spacing.screenHorizontal)) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    AnalyticsStat("Avg. Score", "82%", AIICTheme.colors.secondary)
                    AnalyticsStat("Best Area", "DSA", AIICTheme.colors.primary)
                    AnalyticsStat("Improve", "HR", AIICTheme.colors.accent)
                }
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(AIICTheme.colors.border),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(AIICTheme.colors.gradientPrimaryStart, AIICTheme.colors.gradientSecondaryStart)
                                )
                            ),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Top 18% of all users this week",
                    style = AIICTheme.typography.caption,
                    color = AIICTheme.colors.textTertiary,
                )
            }
        }
    }
}

@Composable
private fun AnalyticsStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = AIICTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(text = label, style = AIICTheme.typography.caption, color = AIICTheme.colors.textTertiary)
    }
}
