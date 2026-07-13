package com.aiic.app.presentation.feature_interview.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.InterviewMode
import com.aiic.app.presentation.feature_interview.components.VoiceWaveform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSessionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSummary: (String) -> Unit,
    viewModel: InterviewSessionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    val requireMic = state.interviewMode == InterviewMode.VOICE || state.interviewMode == InterviewMode.VIDEO
    val requireCamera = state.interviewMode == InterviewMode.VIDEO

    com.aiic.app.common.permissions.InterviewPermissionGate(
        requireMicrophone = requireMic,
        requireCamera = requireCamera,
        onDenied = onNavigateBack,
        onAllGranted = {

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is com.aiic.app.core.base.UiEvent.NavigateBack -> onNavigateBack()
                is com.aiic.app.core.base.UiEvent.Navigate -> {
                    if (event.route.startsWith("interview_summary")) {
                        onNavigateToSummary(event.route)
                    }
                }
                else -> {}
            }
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(AIICTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = AIICTheme.colors.primary)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Preparing your interview...",
                    style = AIICTheme.typography.bodyLarge,
                    color = AIICTheme.colors.textSecondary
                )
            }
        }
    } else {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Question ${state.questionNumber} of ${state.totalQuestions}",
                            style = AIICTheme.typography.titleMedium,
                            color = AIICTheme.colors.textPrimary
                        )
                        LinearProgressIndicator(
                            progress = { state.questionNumber / state.totalQuestions.toFloat() },
                            color = AIICTheme.colors.primary,
                            trackColor = AIICTheme.colors.borderSubtle,
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(top = 4.dp)
                                .clip(CircleShape)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(InterviewSessionAction.QuitSession) }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Quit", tint = AIICTheme.colors.textSecondary)
                    }
                },
                actions = {
                    // Timer pill
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AIICTheme.colors.surfaceElevated)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        val minutes = state.timeRemainingSeconds / 60
                        val seconds = state.timeRemainingSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            style = AIICTheme.typography.labelMedium,
                            color = AIICTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AIICTheme.colors.background)
            )
        },
        containerColor = AIICTheme.colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AIICTheme.spacing.screenHorizontal)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── AI Interviewer Section ──
            InterviewerBubble(
                question = state.currentQuestion?.content ?: "Preparing question...",
                isFollowUp = state.currentQuestion?.isFollowUp == true,
                isThinking = state.isEvaluating
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Camera Preview ──
            if (state.interviewMode == InterviewMode.VIDEO) {
                CameraPreviewSection(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth().height(240.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Voice Mode: Waveform + Transcript ──
            if (state.interviewMode == InterviewMode.VOICE || state.interviewMode == InterviewMode.VIDEO) {
                VoiceInterviewSection(
                    transcript = state.voiceTranscript,
                    rmsLevel = state.voiceRmsLevel,
                    isRecording = state.isVoiceRecording,
                    onToggleRecording = { viewModel.onAction(InterviewSessionAction.ToggleVoiceRecording) },
                    onTranscriptChange = { viewModel.onAction(InterviewSessionAction.UpdateVoiceTranscript(it)) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 250.dp, max = 400.dp)
                )
            } else {
                // ── Text Mode: Answer Input ──
                TextInterviewSection(
                    answer = state.currentAnswerInput,
                    onAnswerChange = { viewModel.onAction(InterviewSessionAction.UpdateAnswerInput(it)) },
                    isEvaluating = state.isEvaluating,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 250.dp, max = 400.dp)
                )
            }

            // ── Camera Warning Bar ──
            if (state.interviewMode == InterviewMode.VIDEO && state.cameraWarning != null) {
                CameraWarningBar(warning = state.cameraWarning!!)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── Error Display ──
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = AIICTheme.colors.error,
                    style = AIICTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Action Buttons ──
            ActionBar(
                interviewMode = state.interviewMode,
                isVoiceRecording = state.isVoiceRecording,
                isEvaluating = state.isEvaluating,
                hasAnswer = state.currentAnswerInput.isNotBlank() || state.voiceTranscript.isNotBlank(),
                isPaused = state.isPaused,
                onToggleVoice = { viewModel.onAction(InterviewSessionAction.ToggleVoiceRecording) },
                onSubmit = { viewModel.onAction(InterviewSessionAction.SubmitAnswer) },
                onTogglePause = { viewModel.onAction(InterviewSessionAction.TogglePause) }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    } // end else
    } // end onAllGranted
    ) // end InterviewPermissionGate
}

@Composable
private fun InterviewerBubble(
    question: String,
    isFollowUp: Boolean,
    isThinking: Boolean
) {
    PremiumCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isFollowUp) 2.dp else 1.dp,
                color = if (isFollowUp) AIICTheme.colors.accent else AIICTheme.colors.borderSubtle,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                // AI Avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    AIICTheme.colors.primary,
                                    AIICTheme.colors.accent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "AI Interviewer",
                        style = AIICTheme.typography.labelMedium,
                        color = AIICTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (isFollowUp) {
                        Text(
                            "Follow-up Question",
                            style = AIICTheme.typography.labelSmall,
                            color = AIICTheme.colors.accent
                        )
                    }
                }
            }

            if (isThinking) {
                ThinkingAnimation()
            } else {
                Text(
                    text = question,
                    style = AIICTheme.typography.bodyLarge,
                    color = AIICTheme.colors.textPrimary,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 26.sp
                )
            }
        }
    }
}

@Composable
private fun ThinkingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "dot"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        AIICTheme.colors.primary.copy(
                            alpha = when (index) {
                                0 -> alpha
                                1 -> alpha * 0.8f
                                else -> alpha * 0.6f
                            }
                        )
                    )
            )
        }
    }
}

@Composable
private fun VoiceInterviewSection(
    transcript: String,
    rmsLevel: Float,
    isRecording: Boolean,
    onToggleRecording: () -> Unit,
    onTranscriptChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var editableText by remember(transcript) { mutableStateOf(transcript) }

    Column(modifier = modifier) {
        // Waveform
        VoiceWaveform(
            rmsLevel = rmsLevel,
            isActive = isRecording,
            barColor = AIICTheme.colors.primary,
            barColorSecondary = AIICTheme.colors.accent
        )

        Spacer(Modifier.height(12.dp))

        // "Your Answer" label + Edit button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Answer",
                style = AIICTheme.typography.titleMedium,
                color = AIICTheme.colors.textSecondary,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = { isEditing = !isEditing }) {
                Text(
                    text = if (isEditing) "Done" else "Edit",
                    style = AIICTheme.typography.labelMedium,
                    color = AIICTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Transcript area - editable or read-only
        PremiumCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "LIVE TRANSCRIPT",
                    style = AIICTheme.typography.labelSmall,
                    color = AIICTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (isEditing) {
                    OutlinedTextField(
                        value = editableText,
                        onValueChange = { 
                            editableText = it 
                            onTranscriptChange(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = AIICTheme.typography.bodyLarge.copy(
                            color = AIICTheme.colors.textPrimary,
                            lineHeight = 24.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AIICTheme.colors.primary,
                            unfocusedBorderColor = AIICTheme.colors.borderSubtle,
                            cursorColor = AIICTheme.colors.primary
                        ),
                        placeholder = { Text("Edit your answer...", color = AIICTheme.colors.textTertiary) },
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Text(
                        text = if (transcript.isBlank()) {
                            if (isRecording) "Listening... Start speaking." else "Tap the mic to start speaking."
                        } else transcript,
                        style = AIICTheme.typography.bodyLarge,
                        color = if (transcript.isBlank()) AIICTheme.colors.textTertiary else AIICTheme.colors.textPrimary,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TextInterviewSection(
    answer: String,
    onAnswerChange: (String) -> Unit,
    isEvaluating: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Your Answer",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp),
            textStyle = AIICTheme.typography.bodyLarge,
            placeholder = { Text("Type your answer here...", color = AIICTheme.colors.textTertiary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AIICTheme.colors.primary,
                unfocusedBorderColor = AIICTheme.colors.borderSubtle,
                focusedTextColor = AIICTheme.colors.textPrimary,
                unfocusedTextColor = AIICTheme.colors.textPrimary,
                cursorColor = AIICTheme.colors.primary
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = !isEvaluating
        )
    }
}

@Composable
private fun CameraWarningBar(warning: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF3CD))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Rounded.Warning, contentDescription = null, tint = Color(0xFF856404), modifier = Modifier.size(20.dp))
        Text(warning, style = AIICTheme.typography.bodySmall, color = Color(0xFF856404), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ActionBar(
    interviewMode: InterviewMode,
    isVoiceRecording: Boolean,
    isEvaluating: Boolean,
    hasAnswer: Boolean,
    isPaused: Boolean,
    onToggleVoice: () -> Unit,
    onSubmit: () -> Unit,
    onTogglePause: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pause button
        IconButton(
            onClick = onTogglePause,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AIICTheme.colors.surfaceElevated)
        ) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(
                    id = if (isPaused) com.aiic.app.R.drawable.ic_exact_play else com.aiic.app.R.drawable.ic_exact_pause
                ),
                contentDescription = if (isPaused) "Resume" else "Pause",
                tint = AIICTheme.colors.textSecondary
            )
        }

        // Voice toggle (for voice/video modes)
        if (interviewMode == InterviewMode.VOICE || interviewMode == InterviewMode.VIDEO) {
            IconButton(
                onClick = onToggleVoice,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isVoiceRecording) AIICTheme.colors.error.copy(alpha = 0.15f)
                        else AIICTheme.colors.surfaceElevated
                    )
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(
                        id = if (isVoiceRecording) com.aiic.app.R.drawable.ic_exact_mic_off else com.aiic.app.R.drawable.ic_exact_mic
                    ),
                    contentDescription = if (isVoiceRecording) "Stop" else "Record",
                    tint = if (isVoiceRecording) AIICTheme.colors.error else AIICTheme.colors.textSecondary
                )
            }
        }

        // Submit button
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AIICTheme.colors.primary,
                disabledContainerColor = AIICTheme.colors.primary.copy(alpha = 0.5f)
            ),
            enabled = !isEvaluating && hasAnswer
        ) {
            if (isEvaluating) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Evaluating...", color = Color.White, fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.aiic.app.R.drawable.ic_exact_send), 
                    contentDescription = null, 
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CameraPreviewSection(
    viewModel: InterviewSessionViewModel,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    PremiumCard(modifier = modifier) {
        androidx.compose.ui.viewinterop.AndroidView(
            factory = { context ->
                androidx.camera.view.PreviewView(context).apply {
                    scaleType = androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
                    viewModel.startCamera(lifecycleOwner, this)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
