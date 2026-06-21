package com.aiic.app.presentation.feature_resume.upload

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.presentation.feature_resume.components.UploadProgressCard

@Composable
fun ResumeUploadScreen(
    onNavigateBack: () -> Unit,
    viewModel: ResumeUploadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            var fileName = "resume.pdf"
            var fileSize = 0L
            val mimeType = context.contentResolver.getType(it)

            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex)
                    if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                }
            }
            viewModel.uploadFile(it, fileName, fileSize, mimeType)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = AIICTheme.colors.textPrimary)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AIICTheme.spacing.screenHorizontal)
        ) {
            AnimatedContent(
                targetState = uiState,
                label = "upload_state_transition"
            ) { state ->
                when (state) {
                    is UploadUiState.Idle, is UploadUiState.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(Modifier.height(48.dp))
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(AIICTheme.colors.surfaceElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.UploadFile,
                                    contentDescription = null,
                                    tint = AIICTheme.colors.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "Upload Your Resume",
                                style = AIICTheme.typography.headlineMedium,
                                color = AIICTheme.colors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "PDF format only, up to 10 MB.",
                                style = AIICTheme.typography.bodyMedium,
                                color = AIICTheme.colors.textSecondary
                            )
                            
                            if (state is UploadUiState.Error) {
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = state.message,
                                    style = AIICTheme.typography.bodyMedium,
                                    color = AIICTheme.colors.error
                                )
                            }
                            
                            Spacer(Modifier.height(32.dp))
                            PremiumButton(
                                text = "Select PDF",
                                onClick = { pdfPickerLauncher.launch("application/pdf") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    is UploadUiState.Validating -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Spacer(Modifier.height(100.dp))
                            CircularProgressIndicator(color = AIICTheme.colors.primary)
                            Spacer(Modifier.height(24.dp))
                            Text("Validating file...", style = AIICTheme.typography.titleMedium, color = AIICTheme.colors.textPrimary)
                        }
                    }
                    is UploadUiState.Uploading -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Spacer(Modifier.height(60.dp))
                            UploadProgressCard(progress = state.progress)
                            Spacer(Modifier.height(32.dp))
                            PremiumButton(
                                text = "Cancel Upload",
                                onClick = { viewModel.cancelUpload() },
                                containerColor = AIICTheme.colors.surfaceBright,
                                contentColor = AIICTheme.colors.textPrimary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    is UploadUiState.Syncing -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Spacer(Modifier.height(100.dp))
                            CircularProgressIndicator(color = AIICTheme.colors.primary)
                            Spacer(Modifier.height(24.dp))
                            Text("Syncing metadata...", style = AIICTheme.typography.titleMedium, color = AIICTheme.colors.textPrimary)
                        }
                    }
                    is UploadUiState.Success -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Spacer(Modifier.height(100.dp))
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(AIICTheme.colors.success.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = AIICTheme.colors.success,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "Upload Successful!",
                                style = AIICTheme.typography.headlineMedium,
                                color = AIICTheme.colors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Your resume has been added to the platform.",
                                style = AIICTheme.typography.bodyMedium,
                                color = AIICTheme.colors.textSecondary
                            )
                            Spacer(Modifier.height(32.dp))
                            PremiumButton(
                                text = "Back to Dashboard",
                                onClick = onNavigateBack,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
