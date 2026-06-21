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
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(true) }
    var offlineMode by remember { mutableStateOf(false) }

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

        Text(
            text = "Preferences",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsToggle(
                icon = Icons.Rounded.Notifications,
                label = "Notifications",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it },
                color = AIICTheme.colors.primary,
            )
            SettingsToggle(
                icon = Icons.Rounded.DarkMode,
                label = "Dark Mode",
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it },
                color = AIICTheme.colors.secondary,
            )
            SettingsToggle(
                icon = Icons.Rounded.Storage,
                label = "Offline Mode",
                checked = offlineMode,
                onCheckedChange = { offlineMode = it },
                color = AIICTheme.colors.tertiary,
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "General",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsNavItem(Icons.Rounded.Language, "Language", "English", AIICTheme.colors.accent, onClick = {})
            SettingsNavItem(Icons.Rounded.Lock, "Security", null, AIICTheme.colors.error, onClick = {})
            SettingsNavItem(Icons.Rounded.Payment, "Subscription", "Free Plan", AIICTheme.colors.warning, onClick = {})
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Support",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsNavItem(Icons.Rounded.SupportAgent, "Help Center", null, AIICTheme.colors.primary, onClick = {})
            SettingsNavItem(Icons.Rounded.Info, "About AIIC", "v1.0.0", AIICTheme.colors.textTertiary, onClick = {})
        }

        Spacer(Modifier.height(32.dp))

        com.aiic.app.common.components.PremiumButton(
            text = "Log Out",
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
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
