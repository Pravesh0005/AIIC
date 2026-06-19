package com.aiic.app.presentation.feature_profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiic.app.common.components.GlassCard
import com.aiic.app.common.components.GradientText
import com.aiic.app.core.theme.AIICTheme

@Composable
fun ProfileScreen() {
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
            text = "Profile",
            style = AIICTheme.typography.headlineLarge,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(28.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(AIICTheme.colors.primary, AIICTheme.colors.accent)
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "P",
                        style = AIICTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AIICTheme.colors.surfaceElevated),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.CameraAlt,
                        contentDescription = null,
                        tint = AIICTheme.colors.textSecondary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            GradientText(text = "Praveen", style = AIICTheme.typography.headlineLarge)
            Text(
                text = "praveen@example.com",
                style = AIICTheme.typography.bodySmall,
                color = AIICTheme.colors.textTertiary,
            )
        }

        Spacer(Modifier.height(28.dp))

        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Rounded.Diamond,
                    contentDescription = null,
                    tint = AIICTheme.colors.warning,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Upgrade to Pro", style = AIICTheme.typography.titleSmall, color = AIICTheme.colors.textPrimary)
                    Text("Unlock unlimited interviews", style = AIICTheme.typography.caption, color = AIICTheme.colors.textTertiary)
                }
                Icon(Icons.Rounded.ChevronRight, null, tint = AIICTheme.colors.textTertiary, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ProfileMenuItem(Icons.Rounded.Edit, "Edit Profile", AIICTheme.colors.primary)
            ProfileMenuItem(Icons.Rounded.WorkspacePremium, "Achievements", AIICTheme.colors.warning)
            ProfileMenuItem(Icons.Rounded.Shield, "Privacy & Security", AIICTheme.colors.secondary)
            ProfileMenuItem(Icons.Rounded.Logout, "Sign Out", AIICTheme.colors.error)
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, color: Color) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(AIICTheme.shapes.small)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(14.dp))
            Text(label, style = AIICTheme.typography.titleSmall, color = AIICTheme.colors.textPrimary, modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ChevronRight, null, tint = AIICTheme.colors.textDisabled, modifier = Modifier.size(18.dp))
        }
    }
}
