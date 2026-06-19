package com.aiic.app.presentation.feature_settings

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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Storage
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
import com.aiic.app.common.components.GlassCard
import com.aiic.app.core.theme.AIICTheme

@Composable
fun SettingsScreen() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = AIICTheme.spacing.screenHorizontal),
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Settings",
            style = AIICTheme.typography.headlineLarge,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(28.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            SettingsNavItem(Icons.Rounded.Language, "Language", "English", AIICTheme.colors.tertiary)
            SettingsNavItem(Icons.Rounded.Storage, "Data & Storage", null, AIICTheme.colors.warning)
            SettingsNavItem(Icons.Rounded.Info, "About AIIC", "v1.0.0", AIICTheme.colors.textTertiary)
        }
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
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(AIICTheme.shapes.small).background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) { Icon(icon, null, tint = color, modifier = Modifier.size(18.dp)) }
            Spacer(Modifier.width(14.dp))
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
private fun SettingsNavItem(icon: ImageVector, label: String, detail: String?, color: Color) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(AIICTheme.shapes.small).background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) { Icon(icon, null, tint = color, modifier = Modifier.size(18.dp)) }
            Spacer(Modifier.width(14.dp))
            Text(label, style = AIICTheme.typography.titleSmall, color = AIICTheme.colors.textPrimary, modifier = Modifier.weight(1f))
            if (detail != null) {
                Text(detail, style = AIICTheme.typography.caption, color = AIICTheme.colors.textDisabled)
                Spacer(Modifier.width(6.dp))
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = AIICTheme.colors.textDisabled, modifier = Modifier.size(18.dp))
        }
    }
}
