package com.aiic.app.common.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.theme.AIICTheme

/**
 * Composable that handles runtime permissions for voice and video interview modes.
 * Shows a premium permission request UI if permissions are not granted.
 * Calls [onAllGranted] once all required permissions are available.
 */
@Composable
fun InterviewPermissionGate(
    requireMicrophone: Boolean = false,
    requireCamera: Boolean = false,
    onAllGranted: @Composable () -> Unit,
    onDenied: () -> Unit = {}
) {
    val context = LocalContext.current

    val requiredPermissions = buildList {
        if (requireMicrophone) add(Manifest.permission.RECORD_AUDIO)
        if (requireCamera) add(Manifest.permission.CAMERA)
    }

    if (requiredPermissions.isEmpty()) {
        onAllGranted()
        return
    }

    var allGranted by remember {
        mutableStateOf(requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        })
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        allGranted = permissions.values.all { it }
        if (!allGranted) onDenied()
    }

    if (allGranted) {
        onAllGranted()
    } else {
        PermissionRequestScreen(
            requireMicrophone = requireMicrophone,
            requireCamera = requireCamera,
            onRequestPermissions = {
                launcher.launch(requiredPermissions.toTypedArray())
            }
        )
    }
}

@Composable
private fun PermissionRequestScreen(
    requireMicrophone: Boolean,
    requireCamera: Boolean,
    onRequestPermissions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Shield icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(AIICTheme.colors.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Security,
                contentDescription = null,
                tint = AIICTheme.colors.primary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Permissions Required",
            style = AIICTheme.typography.headlineSmall,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "To provide the best interview experience, we need access to:",
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        if (requireMicrophone) {
            PermissionItem(
                icon = Icons.Rounded.Mic,
                title = "Microphone",
                description = "For voice-based interview answers and speech analysis",
                color = Color(0xFF6C5CE7)
            )
            Spacer(Modifier.height(12.dp))
        }

        if (requireCamera) {
            PermissionItem(
                icon = Icons.Rounded.CameraAlt,
                title = "Camera",
                description = "For body language and facial expression analysis",
                color = Color(0xFF00B894)
            )
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(24.dp))

        PremiumButton(
            text = "Grant Permissions",
            onClick = onRequestPermissions,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Your data stays on-device. No audio or video is recorded or uploaded.",
            style = AIICTheme.typography.bodySmall,
            color = AIICTheme.colors.textTertiary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PermissionItem(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, style = AIICTheme.typography.titleSmall, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.SemiBold)
            Text(description, style = AIICTheme.typography.bodySmall, color = AIICTheme.colors.textSecondary)
        }
    }
}

/**
 * Utility to check if a specific permission is granted.
 */
fun hasPermission(context: Context, permission: String): Boolean =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
