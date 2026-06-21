package com.aiic.app.presentation.feature_resume.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pending
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.AnalysisStatus
import com.aiic.app.domain.model.Resume
import com.aiic.app.domain.model.UploadProgress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResumeCard(
    resume: Resume,
    onClick: () -> Unit,
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    PremiumCard(
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(AIICTheme.shapes.small)
                    .background(AIICTheme.colors.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Description,
                    contentDescription = null,
                    tint = AIICTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resume.fileName,
                    style = AIICTheme.typography.titleMedium,
                    color = AIICTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "v${resume.resumeVersion} • ${formatDate(resume.uploadDate)}",
                        style = AIICTheme.typography.bodySmall,
                        color = AIICTheme.colors.textSecondary
                    )
                    if (resume.activeResume) {
                        Spacer(modifier = Modifier.width(8.dp))
                        ActiveBadge()
                    }
                }
            }
            
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "Options",
                    tint = AIICTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
fun ActiveBadge() {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(AIICTheme.colors.success.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Active",
            style = AIICTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
            color = AIICTheme.colors.success
        )
    }
}

@Composable
fun ResumeStatusChip(status: AnalysisStatus) {
    val (icon, color, text) = when (status) {
        AnalysisStatus.PENDING -> Triple(Icons.Rounded.Pending, AIICTheme.colors.warning, "Pending Analysis")
        AnalysisStatus.IN_PROGRESS -> Triple(Icons.Rounded.Pending, AIICTheme.colors.primary, "Analyzing...")
        AnalysisStatus.COMPLETED -> Triple(Icons.Rounded.CheckCircle, AIICTheme.colors.success, "Analysis Complete")
        AnalysisStatus.FAILED -> Triple(Icons.Rounded.Error, AIICTheme.colors.error, "Analysis Failed")
        AnalysisStatus.SKIPPED -> Triple(Icons.Rounded.Description, AIICTheme.colors.textTertiary, "Not Analyzed")
    }

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Text(
            text = text,
            style = AIICTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
            color = color
        )
    }
}

@Composable
fun UploadProgressCard(progress: UploadProgress) {
    PremiumCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Uploading Resume...",
                    style = AIICTheme.typography.titleMedium,
                    color = AIICTheme.colors.textPrimary
                )
                Text(
                    text = "${progress.percentage}%",
                    style = AIICTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AIICTheme.colors.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress.percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = AIICTheme.colors.primary,
                trackColor = AIICTheme.colors.surfaceBright,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${formatBytes(progress.bytesTransferred)} of ${formatBytes(progress.totalBytes)}",
                style = AIICTheme.typography.caption,
                color = AIICTheme.colors.textSecondary
            )
        }
    }
}

@Composable
fun EmptyResumeState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AIICTheme.colors.surfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Description,
                contentDescription = null,
                tint = AIICTheme.colors.textTertiary,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = AIICTheme.typography.headlineMedium,
            color = AIICTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "Unknown date"
    val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return format.format(Date(timestamp))
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format(Locale.getDefault(), "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
