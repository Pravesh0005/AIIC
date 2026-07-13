package com.aiic.app.presentation.feature_home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.common.components.SectionHeader
import com.aiic.app.core.theme.AIICTheme

private data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val navItems = listOf(
    NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
    NavItem("Interviews", Icons.Rounded.QuestionAnswer, Icons.Outlined.QuestionAnswer),
    NavItem("Resume", Icons.Rounded.Description, Icons.Outlined.Description),
    NavItem("Analytics", Icons.Rounded.Analytics, Icons.Outlined.Analytics),
    NavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person),
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToResume: () -> Unit = {},
    onNavigateToInterviewSetup: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToTips: () -> Unit = {}
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
                    onNavigateToProfile = { selectedNav = 4 },
                    onNavigateToResume = onNavigateToResume,
                    onNavigateToInterviewSetup = onNavigateToInterviewSetup,
                    onNavigateToAnalytics = { selectedNav = 3 },
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToTips = onNavigateToTips
                )
                1 -> com.aiic.app.presentation.feature_analytics.AnalyticsScreen(
                    interviewsCompleted = state.interviewsCompleted,
                    readinessScore = state.readinessScore,
                    hoursOfPractice = state.hoursOfPractice,
                    streakDays = state.streakDays
                )
                2 -> {
                    
                    LaunchedEffect(Unit) { onNavigateToResume() }
                }
                3 -> com.aiic.app.presentation.feature_analytics.AnalyticsScreen(
                    interviewsCompleted = state.interviewsCompleted,
                    readinessScore = state.readinessScore,
                    hoursOfPractice = state.hoursOfPractice,
                    streakDays = state.streakDays
                )
                4 -> com.aiic.app.presentation.feature_profile.ProfileScreen(
                    userName = state.userName,
                    userEmail = state.userEmail,
                    profilePhotoUrl = state.profilePhotoUrl,
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToSettings = onNavigateToSettings,
                    onSignOut = onNavigateToLogin
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
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTips: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    
                    Column {
                        Text(
                            text = "AIIC",
                            style = AIICTheme.typography.titleLarge,
                            color = AIICTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "AI Interview Coach",
                            style = AIICTheme.typography.labelSmall,
                            color = AIICTheme.colors.textTertiary,
                        )
                    }
                }
                IconButton(onClick = {  }) {
                    Icon(
                        Icons.Rounded.Notifications,
                        contentDescription = "Notifications",
                        tint = AIICTheme.colors.textSecondary,
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Text(
                    text = "Good Morning, ${state.userName} 👋",
                    style = AIICTheme.typography.headlineMedium,
                    color = AIICTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Ready to level up your interview skills today?",
                    style = AIICTheme.typography.bodyMedium,
                    color = AIICTheme.colors.textSecondary,
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AIICTheme.colors.surface)
                    .border(1.dp, AIICTheme.colors.border, RoundedCornerShape(16.dp))
                    .padding(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Overall Progress",
                            style = AIICTheme.typography.titleMedium,
                            color = AIICTheme.colors.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "You're doing great! Keep it up.",
                            style = AIICTheme.typography.bodySmall,
                            color = AIICTheme.colors.textSecondary,
                        )
                        Spacer(Modifier.height(12.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(AIICTheme.colors.surfaceBright)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(state.readinessScore.coerceIn(0f, 1f))
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(AIICTheme.colors.primary, AIICTheme.colors.secondary)
                                        )
                                    )
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Row {
                            Text(
                                text = "${(state.readinessScore * 100).toInt()}% Complete",
                                style = AIICTheme.typography.labelSmall,
                                color = AIICTheme.colors.textTertiary,
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = "View Analytics >",
                                style = AIICTheme.typography.labelSmall,
                                color = AIICTheme.colors.secondary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { onNavigateToAnalytics() }
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(AIICTheme.colors.primaryContainer)
                            .border(3.dp, AIICTheme.colors.primary, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${(state.readinessScore * 100).toInt()}%",
                            style = AIICTheme.typography.titleSmall,
                            color = AIICTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        item {
            Column {
                SectionHeader(title = "Quick Actions", action = "See All >", onAction = {})
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    QuickActionItem(
                        icon = Icons.Rounded.Psychology,
                        label = "Start\nInterview",
                        subtitle = "AI Mock Interview",
                        color = AIICTheme.colors.primary,
                        onClick = onNavigateToInterviewSetup,
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionItem(
                        icon = Icons.Rounded.Description,
                        label = "Resume\nReview",
                        subtitle = "ATS & AI Score",
                        color = AIICTheme.colors.green,
                        onClick = onNavigateToResume,
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionItem(
                        icon = Icons.Rounded.Analytics,
                        label = "Analytics",
                        subtitle = "Performance",
                        color = AIICTheme.colors.purple,
                        onClick = onNavigateToAnalytics,
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionItem(
                        icon = Icons.Rounded.School,
                        label = "Study\nCenter",
                        subtitle = "Resources",
                        color = AIICTheme.colors.orange,
                        onClick = onNavigateToTips,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AIICTheme.colors.surface)
                    .border(1.dp, AIICTheme.colors.border, RoundedCornerShape(16.dp))
                    .padding(16.dp),
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = AIICTheme.colors.secondary,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "AI Insight",
                            style = AIICTheme.typography.titleSmall,
                            color = AIICTheme.colors.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Your communication skills improved by 18%.\nFocus on System Design to boost your score.",
                        style = AIICTheme.typography.bodySmall,
                        color = AIICTheme.colors.textSecondary,
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BottomStat(
                    icon = Icons.Rounded.LocalFireDepartment,
                    value = "${state.streakDays}",
                    label = "Day Streak",
                    color = AIICTheme.colors.orange,
                    modifier = Modifier.weight(1f),
                )
                BottomStat(
                    icon = Icons.Rounded.AccessTime,
                    value = formatPracticeTime(state.hoursOfPractice),
                    label = "Practice Time",
                    color = AIICTheme.colors.primary,
                    modifier = Modifier.weight(1f),
                )
                BottomStat(
                    icon = Icons.Rounded.TrendingUp,
                    value = "${(state.readinessScore * 100).toInt()}%",
                    label = "Avg. Score",
                    color = AIICTheme.colors.green,
                    modifier = Modifier.weight(1f),
                )
                BottomStat(
                    icon = Icons.Rounded.EmojiEvents,
                    value = "${state.interviewsCompleted}",
                    label = "Interviews",
                    color = AIICTheme.colors.purple,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AIICTheme.colors.surface)
            .border(1.dp, AIICTheme.colors.border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            style = AIICTheme.typography.labelSmall,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 14.sp,
        )
        Text(
            text = subtitle,
            style = AIICTheme.typography.caption,
            color = AIICTheme.colors.textTertiary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun BottomStat(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AIICTheme.colors.surface)
            .border(1.dp, AIICTheme.colors.border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            style = AIICTheme.typography.titleSmall,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = AIICTheme.typography.caption,
            color = AIICTheme.colors.textTertiary,
        )
    }
}

private fun formatPracticeTime(hours: Float): String {
    if (hours <= 0f) return "0 min"
    val totalMinutes = (hours * 60).toInt()
    return when {
        totalMinutes < 60 -> "${totalMinutes}m"
        totalMinutes % 60 == 0 -> "${totalMinutes / 60}h"
        else -> "${totalMinutes / 60}h ${totalMinutes % 60}m"
    }
}
