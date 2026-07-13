package com.aiic.app.presentation.feature_interview.report

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.InterviewReport
import com.aiic.app.presentation.feature_interview.components.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InterviewReportScreen(
    sessionId: String,
    onNavigateBack: () -> Unit,
    viewModel: InterviewReportViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(sessionId) {
        viewModel.loadReport(sessionId)
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(AIICTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = AIICTheme.colors.primary)
                    Spacer(Modifier.height(16.dp))
                    Text("Loading report...", color = AIICTheme.colors.textSecondary)
                }
            }
        }
        state.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize().background(AIICTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Error, contentDescription = null, tint = AIICTheme.colors.error, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(state.error!!, color = AIICTheme.colors.error, textAlign = TextAlign.Center)
                }
            }
        }
        state.report != null -> {
            val report = state.report!!

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Interview Report", color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = AIICTheme.colors.textPrimary)
                            }
                        },
                        actions = {
                            
                            IconButton(onClick = {
                                val pdfFile = generateReportPdf(context, report)
                                if (pdfFile != null) {
                                    val uri = androidx.core.content.FileProvider.getUriForFile(
                                        context, "${context.packageName}.fileprovider", pdfFile
                                    )
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/pdf"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Report PDF"))
                                }
                            }) {
                                Icon(Icons.Rounded.Download, contentDescription = "Download PDF", tint = AIICTheme.colors.accent)
                            }
                            
                            IconButton(onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, buildShareText(report))
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Report"))
                            }) {
                                Icon(Icons.Rounded.Share, contentDescription = "Share", tint = AIICTheme.colors.primary)
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
                        .verticalScroll(scrollState)
                ) {
                    Spacer(Modifier.height(8.dp))

                    HeroScoreCard(report)

                    Spacer(Modifier.height(24.dp))

                    HiringVerdictCard(report)

                    Spacer(Modifier.height(24.dp))

                    Text("Detailed Scores", style = AIICTheme.typography.titleLarge, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        AnimatedScoreRing(report.technicalAccuracyScore, label = "Technical", size = 80, strokeWidth = 8f)
                        AnimatedScoreRing(report.communicationScore, label = "Communication", size = 80, strokeWidth = 8f)
                        AnimatedScoreRing(report.confidenceScore, label = "Confidence", size = 80, strokeWidth = 8f)
                    }
                    Spacer(Modifier.height(16.dp))
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        AnimatedScoreRing(report.problemSolvingScore, label = "Problem Solving", size = 80, strokeWidth = 8f)
                        AnimatedScoreRing(report.depthScore, label = "Depth", size = 80, strokeWidth = 8f)
                        AnimatedScoreRing(report.structureScore, label = "Structure", size = 80, strokeWidth = 8f)
                    }
                    Spacer(Modifier.height(16.dp))
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        AnimatedScoreRing(report.vocabularyScore, label = "Vocabulary", size = 80, strokeWidth = 8f)
                        AnimatedScoreRing(report.professionalismScore, label = "Professionalism", size = 80, strokeWidth = 8f)
                        AnimatedScoreRing(report.examplesScore, label = "Examples", size = 80, strokeWidth = 8f)
                    }

                    Spacer(Modifier.height(24.dp))

                    Text("Performance Radar", style = AIICTheme.typography.titleLarge, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    PremiumCard {
                        RadarChart(
                            scores = listOf(
                                report.technicalAccuracyScore,
                                report.communicationScore,
                                report.confidenceScore,
                                report.problemSolvingScore,
                                report.depthScore,
                                report.structureScore
                            ),
                            labels = listOf("Technical", "Communication", "Confidence", "Problem Solving", "Depth", "Structure")
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    if (report.strengths.isNotEmpty()) {
                        Text("Strengths", style = AIICTheme.typography.titleMedium, color = Color(0xFF00B894), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            report.strengths.forEach { TagPill(it, color = Color(0xFF00B894)) }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    if (report.weaknesses.isNotEmpty()) {
                        Text("Areas to Improve", style = AIICTheme.typography.titleMedium, color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            report.weaknesses.forEach { TagPill(it, color = Color(0xFFFF6B6B)) }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    if (report.improvementPlan.isNotEmpty()) {
                        Text("Improvement Plan", style = AIICTheme.typography.titleLarge, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        PremiumCard {
                            Column(Modifier.padding(16.dp)) {
                                report.improvementPlan.forEachIndexed { index, step ->
                                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text("${index + 1}.", color = AIICTheme.colors.primary, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(8.dp))
                                        Text(step, style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.textPrimary)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    if (report.nextLearningPath.isNotEmpty()) {
                        Text("Learning Path", style = AIICTheme.typography.titleLarge, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        PremiumCard {
                            Column(Modifier.padding(16.dp)) {
                                report.nextLearningPath.forEach { topic ->
                                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.School, contentDescription = null, tint = AIICTheme.colors.accent, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(topic, style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.textPrimary)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    if (report.questionResults.isNotEmpty()) {
                        Text("Question Results", style = AIICTheme.typography.titleLarge, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))

                        report.questionResults.forEachIndexed { index, qr ->
                            PremiumCard(modifier = Modifier.padding(vertical = 4.dp)) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Q${index + 1}", style = AIICTheme.typography.labelMedium, color = AIICTheme.colors.primary, fontWeight = FontWeight.Bold)
                                        Text("${qr.score.toInt()}/100", style = AIICTheme.typography.labelMedium, color = scoreColor(qr.score), fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(qr.question, style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.Medium)
                                    if (qr.feedback.isNotBlank()) {
                                        Spacer(Modifier.height(8.dp))
                                        Text(qr.feedback, style = AIICTheme.typography.bodySmall, color = AIICTheme.colors.textSecondary)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    if (report.voiceAnalysis != null) {
                        val voice = report.voiceAnalysis!!
                        Text("Voice Analysis", style = AIICTheme.typography.titleLarge, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        PremiumCard {
                            Column(Modifier.padding(16.dp)) {
                                MetricRow("Words Per Minute", "${voice.wordsPerMinute.toInt()} WPM")
                                MetricRow("Total Words", "${voice.totalWords}")
                                MetricRow("Filler Words", "${voice.fillerWordCount}")
                                MetricRow("Communication Score", "${voice.communicationScore.toInt()}%", color = scoreColor(voice.communicationScore))
                                if (voice.fillerWords.isNotEmpty()) {
                                    Spacer(Modifier.height(8.dp))
                                    Text("Filler Word Breakdown:", style = AIICTheme.typography.labelMedium, color = AIICTheme.colors.textSecondary)
                                    voice.fillerWords.forEach { (word, count) ->
                                        MetricRow("\"$word\"", "$count times", color = Color(0xFFFF6B6B))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    if (report.bodyLanguageReport != null) {
                        val body = report.bodyLanguageReport!!
                        Text("Body Language", style = AIICTheme.typography.titleLarge, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        PremiumCard {
                            Column(Modifier.padding(16.dp)) {
                                ScoreBar("Eye Contact", body.eyeContactScore, color = Color(0xFF6C5CE7))
                                ScoreBar("Confidence", body.confidenceScore, color = Color(0xFF00B894))
                                ScoreBar("Professionalism", body.professionalismScore, color = Color(0xFF0984E3))
                                ScoreBar("Engagement", body.engagementScore, color = Color(0xFFFDAA5E))
                                ScoreBar("Energy", body.energyScore, color = Color(0xFFE17055))
                                if (body.suggestions.isNotEmpty()) {
                                    Spacer(Modifier.height(12.dp))
                                    body.suggestions.forEach { suggestion ->
                                        Row(Modifier.padding(vertical = 2.dp)) {
                                            Text("•", color = AIICTheme.colors.primary)
                                            Spacer(Modifier.width(8.dp))
                                            Text(suggestion, style = AIICTheme.typography.bodySmall, color = AIICTheme.colors.textSecondary)
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    PremiumCard {
                        Column(Modifier.padding(16.dp)) {
                            MetricRow("Role", report.targetRole)
                            MetricRow("Type", report.interviewType.name)
                            MetricRow("Difficulty", report.difficulty.name)
                            MetricRow("Mode", report.interviewMode.name)
                            MetricRow("Duration", formatDuration(report.totalDurationMs))
                            MetricRow("Questions", "${report.questionResults.size}")
                        }
                    }

                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun HeroScoreCard(report: InterviewReport) {
    PremiumCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AIICTheme.colors.primary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedScoreRing(
                    score = report.overallScore,
                    label = "Overall Score",
                    size = 140,
                    strokeWidth = 14f
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = report.targetRole,
                    style = AIICTheme.typography.titleMedium,
                    color = AIICTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun HiringVerdictCard(report: InterviewReport) {
    val verdictColor = when {
        report.hiringRecommendation.contains("Strong Yes", ignoreCase = true) -> Color(0xFF00B894)
        report.hiringRecommendation.contains("Yes", ignoreCase = true) -> Color(0xFF00CEC9)
        report.hiringRecommendation.contains("Borderline", ignoreCase = true) -> Color(0xFFFDAA5E)
        else -> Color(0xFFFF6B6B)
    }

    PremiumCard {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Hiring Recommendation", style = AIICTheme.typography.labelSmall, color = AIICTheme.colors.textTertiary)
                    Text(report.hiringRecommendation, style = AIICTheme.typography.headlineSmall, color = verdictColor, fontWeight = FontWeight.Bold)
                }
                Icon(
                    when {
                        report.hiringRecommendation.contains("Yes", ignoreCase = true) -> Icons.Rounded.ThumbUp
                        else -> Icons.Rounded.ThumbDown
                    },
                    contentDescription = null,
                    tint = verdictColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = AIICTheme.colors.borderSubtle)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Role Readiness", style = AIICTheme.typography.labelSmall, color = AIICTheme.colors.textTertiary)
                    Text(report.roleReadiness, style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Salary Level", style = AIICTheme.typography.labelSmall, color = AIICTheme.colors.textTertiary)
                    Text(report.salaryReadiness, style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
            if (report.companyFit.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text("Company Fit", style = AIICTheme.typography.labelSmall, color = AIICTheme.colors.textTertiary)
                Text(report.companyFit, style = AIICTheme.typography.bodySmall, color = AIICTheme.colors.textSecondary)
            }
        }
    }
}

private fun scoreColor(score: Float): Color = when {
    score >= 80 -> Color(0xFF00B894)
    score >= 60 -> Color(0xFFFDAA5E)
    score >= 40 -> Color(0xFFE17055)
    else -> Color(0xFFFF6B6B)
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes}m ${seconds}s"
}

private fun buildShareText(report: InterviewReport): String {
    return """
🎯 AIIC Interview Report
━━━━━━━━━━━━━━━━━━━

📋 Role: ${report.targetRole}
📊 Overall Score: ${report.overallScore.toInt()}/100
🎯 Hiring: ${report.hiringRecommendation}
💼 Readiness: ${report.roleReadiness}

💪 Strengths: ${report.strengths.joinToString(", ")}
📈 Improve: ${report.weaknesses.joinToString(", ")}

Generated by AI Interview Coach (AIIC)
    """.trimIndent()
}

private fun generateReportPdf(context: android.content.Context, report: InterviewReport): java.io.File? {
    return try {
        val document = android.graphics.pdf.PdfDocument()
        val pageWidth = 595 
        val pageHeight = 842 
        val margin = 40f
        val maxContentWidth = pageWidth - 2 * margin.toInt()

        val titlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#1A1A2E")
            textSize = 22f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            isAntiAlias = true
        }
        val headingPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#6C5CE7")
            textSize = 16f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            isAntiAlias = true
        }
        val bodyPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#333333")
            textSize = 12f
            isAntiAlias = true
        }
        val labelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#888888")
            textSize = 11f
            isAntiAlias = true
        }

        val lines = mutableListOf<Triple<String, android.graphics.Paint, Float>>() 

        lines.add(Triple("AIIC - AI Interview Coach Report", titlePaint, 0f))
        lines.add(Triple("", bodyPaint, 10f))
        lines.add(Triple("Overall Score: ${report.overallScore.toInt()}/100", headingPaint, 10f))
        lines.add(Triple("Role: ${report.targetRole}", bodyPaint, 6f))
        lines.add(Triple("Hiring Recommendation: ${report.hiringRecommendation}", bodyPaint, 4f))
        lines.add(Triple("Role Readiness: ${report.roleReadiness}   |   Salary Level: ${report.salaryReadiness}", bodyPaint, 4f))
        if (report.companyFit.isNotBlank()) {
            lines.add(Triple("Company Fit: ${report.companyFit}", bodyPaint, 4f))
        }
        lines.add(Triple("", bodyPaint, 10f))

        lines.add(Triple("━━━ Detailed Scores ━━━", headingPaint, 10f))
        lines.add(Triple("Technical: ${report.technicalAccuracyScore.toInt()}%   Communication: ${report.communicationScore.toInt()}%   Confidence: ${report.confidenceScore.toInt()}%", bodyPaint, 6f))
        lines.add(Triple("Problem Solving: ${report.problemSolvingScore.toInt()}%   Depth: ${report.depthScore.toInt()}%   Structure: ${report.structureScore.toInt()}%", bodyPaint, 4f))
        lines.add(Triple("Vocabulary: ${report.vocabularyScore.toInt()}%   Professionalism: ${report.professionalismScore.toInt()}%   Examples: ${report.examplesScore.toInt()}%", bodyPaint, 4f))
        lines.add(Triple("", bodyPaint, 10f))

        if (report.strengths.isNotEmpty()) {
            lines.add(Triple("━━━ Strengths ━━━", headingPaint, 6f))
            report.strengths.forEach { lines.add(Triple("  ✓ $it", bodyPaint, 3f)) }
            lines.add(Triple("", bodyPaint, 6f))
        }
        
        if (report.weaknesses.isNotEmpty()) {
            lines.add(Triple("━━━ Areas to Improve ━━━", headingPaint, 6f))
            report.weaknesses.forEach { lines.add(Triple("  ✗ $it", bodyPaint, 3f)) }
            lines.add(Triple("", bodyPaint, 6f))
        }
        
        if (report.improvementPlan.isNotEmpty()) {
            lines.add(Triple("━━━ Improvement Plan ━━━", headingPaint, 6f))
            report.improvementPlan.forEachIndexed { i, step -> lines.add(Triple("  ${i + 1}. $step", bodyPaint, 3f)) }
            lines.add(Triple("", bodyPaint, 6f))
        }
        
        if (report.nextLearningPath.isNotEmpty()) {
            lines.add(Triple("━━━ Learning Path ━━━", headingPaint, 6f))
            report.nextLearningPath.forEach { lines.add(Triple("  → $it", bodyPaint, 3f)) }
            lines.add(Triple("", bodyPaint, 6f))
        }
        
        if (report.questionResults.isNotEmpty()) {
            lines.add(Triple("━━━ Question Results ━━━", headingPaint, 6f))
            report.questionResults.forEachIndexed { i, qr ->
                lines.add(Triple("Q${i + 1} (${qr.score.toInt()}/100): ${qr.question}", bodyPaint, 6f))
                if (qr.feedback.isNotBlank()) {
                    lines.add(Triple("   Feedback: ${qr.feedback}", labelPaint, 2f))
                }
            }
            lines.add(Triple("", bodyPaint, 6f))
        }
        
        if (report.voiceAnalysis != null) {
            val v = report.voiceAnalysis!!
            lines.add(Triple("━━━ Voice Analysis ━━━", headingPaint, 6f))
            lines.add(Triple("WPM: ${v.wordsPerMinute.toInt()}   Total Words: ${v.totalWords}   Filler Words: ${v.fillerWordCount}   Communication: ${v.communicationScore.toInt()}%", bodyPaint, 4f))
            lines.add(Triple("", bodyPaint, 6f))
        }
        
        if (report.bodyLanguageReport != null) {
            val b = report.bodyLanguageReport!!
            lines.add(Triple("━━━ Body Language ━━━", headingPaint, 6f))
            lines.add(Triple("Eye Contact: ${b.eyeContactScore.toInt()}%   Confidence: ${b.confidenceScore.toInt()}%   Professionalism: ${b.professionalismScore.toInt()}%", bodyPaint, 4f))
            lines.add(Triple("Engagement: ${b.engagementScore.toInt()}%   Energy: ${b.energyScore.toInt()}%", bodyPaint, 4f))
            b.suggestions.forEach { lines.add(Triple("  • $it", labelPaint, 2f)) }
            lines.add(Triple("", bodyPaint, 6f))
        }
        
        lines.add(Triple("━━━ Session Info ━━━", headingPaint, 6f))
        lines.add(Triple("Type: ${report.interviewType.name}   Difficulty: ${report.difficulty.name}   Mode: ${report.interviewMode.name}", bodyPaint, 4f))
        lines.add(Triple("Duration: ${formatDuration(report.totalDurationMs)}   Questions: ${report.questionResults.size}", bodyPaint, 4f))
        lines.add(Triple("", bodyPaint, 16f))
        lines.add(Triple("Generated by AI Interview Coach (AIIC)", labelPaint, 4f))

        var pageNum = 1
        var y = margin + 20f
        var pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        for ((text, paint, spacing) in lines) {
            y += spacing
            val lineHeight = paint.textSize + 6f

            val words = text.split(" ")
            val wrappedLines = mutableListOf<String>()
            var currentLine = ""
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (paint.measureText(testLine) > maxContentWidth) {
                    if (currentLine.isNotEmpty()) wrappedLines.add(currentLine)
                    currentLine = word
                } else {
                    currentLine = testLine
                }
            }
            if (currentLine.isNotEmpty()) wrappedLines.add(currentLine)

            for (line in wrappedLines) {
                if (y + lineHeight > pageHeight - margin) {
                    document.finishPage(page)
                    pageNum++
                    pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    y = margin + 20f
                }
                canvas.drawText(line, margin, y, paint)
                y += lineHeight
            }
        }
        document.finishPage(page)

        val dir = java.io.File(context.cacheDir, "reports")
        dir.mkdirs()
        val file = java.io.File(dir, "AIIC_Report_${report.targetRole.replace(" ", "_")}_${System.currentTimeMillis()}.pdf")
        document.writeTo(java.io.FileOutputStream(file))
        document.close()
        file
    } catch (e: Exception) {
        android.util.Log.e("AIIC_PDF", "PDF generation failed: ${e.message}", e)
        null
    }
}
