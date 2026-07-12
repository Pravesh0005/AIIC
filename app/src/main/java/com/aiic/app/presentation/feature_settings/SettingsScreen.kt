package com.aiic.app.presentation.feature_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.theme.AIICTheme

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),

    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDataStorageDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val isDarkTheme = com.aiic.app.core.theme.LocalIsDarkTheme.current
    val onToggleTheme = com.aiic.app.core.theme.LocalThemeToggle.current

    // ── Language picker dialog ──
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    "Select Language",
                    style = AIICTheme.typography.titleLarge,
                    color = AIICTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("English", "Hindi", "Spanish", "French", "German", "Japanese", "Korean", "Chinese").forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(AIICTheme.shapes.small)
                                .background(
                                    if (state.language == lang) AIICTheme.colors.primary.copy(alpha = 0.1f)
                                    else Color.Transparent
                                )
                                .clickable {
                                    viewModel.onAction(SettingsAction.UpdateLanguage(lang))
                                    showLanguageDialog = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang,
                                style = AIICTheme.typography.bodyLarge,
                                color = if (state.language == lang) AIICTheme.colors.primary else AIICTheme.colors.textPrimary,
                                fontWeight = if (state.language == lang) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Done", color = AIICTheme.colors.primary)
                }
            },
            containerColor = AIICTheme.colors.surfaceElevated,
            titleContentColor = AIICTheme.colors.textPrimary,
        )
    }

    // ── Data & Storage dialog ──
    if (showDataStorageDialog) {
        AlertDialog(
            onDismissRequest = { showDataStorageDialog = false },
            title = {
                Text("Data & Storage", style = AIICTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoRow(label = "Cache Size", value = "12 MB")
                    InfoRow(label = "Interview Data", value = "Stored locally + Firebase")
                    InfoRow(label = "Resume Files", value = "Stored in cloud storage")
                    InfoRow(label = "Offline Mode", value = "Fallback questions available")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Your interview sessions and resume analyses are securely stored in Firebase. Local cache helps improve app performance.",
                        style = AIICTheme.typography.bodySmall,
                        color = AIICTheme.colors.textTertiary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDataStorageDialog = false }) {
                    Text("Close", color = AIICTheme.colors.primary)
                }
            },
            containerColor = AIICTheme.colors.surfaceElevated,
        )
    }

    // ── Privacy & Security dialog ──
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text("Privacy & Security", style = AIICTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoRow(label = "Authentication", value = "Firebase Auth (Google)")
                    InfoRow(label = "Data Encryption", value = "TLS 1.3 in transit")
                    InfoRow(label = "AI Processing", value = "Gemini & Groq APIs")
                    InfoRow(label = "Resume Data", value = "Not shared with third parties")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Your resume content and interview answers are processed by AI for feedback generation only. We do not sell or share your personal data with third parties. AI responses are not stored beyond your session.",
                        style = AIICTheme.typography.bodySmall,
                        color = AIICTheme.colors.textTertiary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", color = AIICTheme.colors.primary)
                }
            },
            containerColor = AIICTheme.colors.surfaceElevated,
        )
    }

    // ── Help & Support dialog ──
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text("Help & Support", style = AIICTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoRow(label = "Email", value = "support@aiic.app")
                    InfoRow(label = "FAQ", value = "In-app guide available")
                    InfoRow(label = "Version", value = "1.0.0")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Common Issues:\n\n• Interview not loading? Check your internet connection and API key configuration.\n\n• Resume analysis stuck? Try re-uploading a smaller PDF file.\n\n• Feedback not generating? The AI providers may be temporarily unavailable. The app will retry automatically.",
                        style = AIICTheme.typography.bodySmall,
                        color = AIICTheme.colors.textTertiary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Close", color = AIICTheme.colors.primary)
                }
            },
            containerColor = AIICTheme.colors.surfaceElevated,
        )
    }

    // ── About AIIC dialog ──
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text("About AIIC", style = AIICTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoRow(label = "App Name", value = "AI Interview Coach")
                    InfoRow(label = "Version", value = "1.0.0")
                    InfoRow(label = "Build", value = "Production")
                    InfoRow(label = "Platform", value = "Android (Jetpack Compose)")
                    InfoRow(label = "AI Engine", value = "Gemini + Groq")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "AIIC is an AI-powered interview preparation platform that helps candidates practice mock interviews, analyze resumes, and track their readiness for real interviews.\n\n© 2026 AIIC. All rights reserved.",
                        style = AIICTheme.typography.bodySmall,
                        color = AIICTheme.colors.textTertiary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = AIICTheme.colors.primary)
                }
            },
            containerColor = AIICTheme.colors.surfaceElevated,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AIICTheme.spacing.screenHorizontal)
            .padding(bottom = 80.dp),
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Settings",
            style = AIICTheme.typography.headlineLarge,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(28.dp))

        // ── Preferences ──
        Text(
            text = "Preferences",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsToggle(
                icon = Icons.Rounded.DarkMode,
                label = "Dark Mode",
                checked = isDarkTheme,
                onCheckedChange = { onToggleTheme(it) },
                color = AIICTheme.colors.secondary,
            )
            SettingsToggle(
                icon = Icons.Rounded.Notifications,
                label = "Notifications",
                checked = state.notificationsEnabled,
                onCheckedChange = { viewModel.onAction(SettingsAction.UpdateNotifications(it)) },
                color = AIICTheme.colors.primary,
            )
            SettingsToggle(
                icon = Icons.Rounded.Vibration,
                label = "Haptic Feedback",
                checked = state.hapticEnabled,
                onCheckedChange = { viewModel.onAction(SettingsAction.UpdateHaptics(it)) },
                color = AIICTheme.colors.accent,
            )
        }

        Spacer(Modifier.height(28.dp))

        // ── General ──
        Text(
            text = "General",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsNavItem(
                icon = Icons.Rounded.Language,
                label = "Language",
                detail = state.language,
                color = AIICTheme.colors.green,
                onClick = { showLanguageDialog = true }
            )
            SettingsNavItem(
                icon = Icons.Rounded.Storage,
                label = "Data & Storage",
                detail = null,
                color = AIICTheme.colors.secondary,
                onClick = { showDataStorageDialog = true }
            )
            SettingsNavItem(
                icon = Icons.Rounded.Lock,
                label = "Privacy & Security",
                detail = null,
                color = AIICTheme.colors.accent,
                onClick = { showPrivacyDialog = true }
            )
        }

        Spacer(Modifier.height(28.dp))

        // ── Support ──
        Text(
            text = "Support",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsNavItem(
                icon = Icons.Rounded.SupportAgent,
                label = "Help & Support",
                detail = null,
                color = AIICTheme.colors.warning,
                onClick = { showHelpDialog = true }
            )
            SettingsNavItem(
                icon = Icons.Rounded.Info,
                label = "About AIIC",
                detail = "v1.0.0",
                color = AIICTheme.colors.primary,
                onClick = { showAboutDialog = true }
            )
        }

        Spacer(Modifier.height(32.dp))

        PremiumButton(
            text = "Log Out",
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            containerColor = AIICTheme.colors.error,
            showArrow = false,
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textSecondary
        )
        Text(
            text = value,
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    color: Color,
) {
    PremiumCard(modifier = Modifier.clickable { onCheckedChange(!checked) }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(AIICTheme.shapes.small).background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(16.dp))
            Text(label, style = AIICTheme.typography.titleSmall, color = AIICTheme.colors.textPrimary, modifier = Modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AIICTheme.colors.primary,
                    uncheckedThumbColor = AIICTheme.colors.textTertiary,
                    uncheckedTrackColor = AIICTheme.colors.surfaceElevated,
                ),
            )
        }
    }
}

@Composable
private fun SettingsNavItem(icon: ImageVector, label: String, detail: String?, color: Color, onClick: () -> Unit) {
    PremiumCard(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(AIICTheme.shapes.small).background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(16.dp))
            Text(label, style = AIICTheme.typography.titleSmall, color = AIICTheme.colors.textPrimary, modifier = Modifier.weight(1f))
            if (detail != null) {
                Text(detail, style = AIICTheme.typography.bodySmall, color = AIICTheme.colors.textSecondary)
                Spacer(Modifier.width(8.dp))
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = AIICTheme.colors.textDisabled, modifier = Modifier.size(20.dp))
        }
    }
}
