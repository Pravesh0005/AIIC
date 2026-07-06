package com.aiic.app.presentation.feature_tips

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewTipsScreen(
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedCategory by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Interview Mastery",
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = AIICTheme.colors.textPrimary)
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

            // Hero Banner
            TipHeroBanner()

            Spacer(Modifier.height(24.dp))

            // Category tabs
            Text(
                "Topics",
                style = AIICTheme.typography.titleLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            val categories = listOf("All", "Communication", "Technical", "Body Language", "Mindset")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEachIndexed { i, cat ->
                    FilterChip(
                        selected = selectedCategory == i,
                        onClick = { selectedCategory = i },
                        label = { Text(cat, style = AIICTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AIICTheme.colors.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tips list
            val tips = getTips().filter { tip ->
                selectedCategory == 0 || tip.category == categories[selectedCategory]
            }

            tips.forEach { tip ->
                TipCard(tip = tip)
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(24.dp))

            // Daily Challenge
            DailyChallengeCard()

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun TipHeroBanner() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF6C5CE7), Color(0xFF0984E3))
                )
            )
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "💡 Pro Tips",
                    style = AIICTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Level Up Your\nInterview Game",
                    style = AIICTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Expert strategies from top hiring managers",
                    style = AIICTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Icon(
                Icons.Rounded.EmojiEvents,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier
                    .size(64.dp)
                    .scale(scale)
            )
        }
    }
}

data class InterviewTip(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val category: String,
    val importance: Int = 1 // 1-3
)

@Composable
private fun TipCard(tip: InterviewTip) {
    var expanded by remember { mutableStateOf(false) }

    PremiumCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tip.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(tip.icon, contentDescription = null, tint = tip.color, modifier = Modifier.size(24.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        tip.title,
                        style = AIICTheme.typography.titleSmall,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = AIICTheme.colors.textTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Importance dots
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.padding(top = 4.dp)) {
                    repeat(3) { i ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (i < tip.importance) tip.color else AIICTheme.colors.borderSubtle)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        tip.category,
                        style = AIICTheme.typography.labelSmall,
                        color = tip.color
                    )
                }

                if (expanded) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        tip.description,
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.textSecondary,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyChallengeCard() {
    PremiumCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFE17055).copy(alpha = 0.1f), Color(0xFFFDAA5E).copy(alpha = 0.05f))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = Color(0xFFE17055), modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Daily Challenge",
                        style = AIICTheme.typography.titleMedium,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "\"Tell me about a time you failed at something and what you learned from it.\"",
                    style = AIICTheme.typography.bodyMedium,
                    color = AIICTheme.colors.textPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Practice answering using the STAR method: Situation, Task, Action, Result. Aim for a 2-minute structured response with a clear lesson learned.",
                    style = AIICTheme.typography.bodySmall,
                    color = AIICTheme.colors.textSecondary
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text("STAR Method", style = AIICTheme.typography.labelSmall) },
                        leadingIcon = { Icon(Icons.Rounded.Star, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text("Behavioral", style = AIICTheme.typography.labelSmall) },
                        leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                }
            }
        }
    }
}

private fun getTips(): List<InterviewTip> = listOf(
    InterviewTip(
        title = "Use the STAR Framework",
        description = "Structure every behavioral answer with Situation, Task, Action, and Result. This shows interviewers you can communicate experiences logically. Spend 20% on Situation, 10% on Task, 60% on Action (your specific contribution), and 10% on quantifiable Result.",
        icon = Icons.Rounded.Star,
        color = Color(0xFF6C5CE7),
        category = "Communication",
        importance = 3
    ),
    InterviewTip(
        title = "Maintain Eye Contact",
        description = "For video interviews, look directly at the camera lens — not the person on screen. This creates the illusion of direct eye contact. Practice by placing a small sticker near your webcam as a visual cue. For in-person: split eye contact 60-40 between interviewer's eyes and notes.",
        icon = Icons.Rounded.RemoveRedEye,
        color = Color(0xFF00B894),
        category = "Body Language",
        importance = 3
    ),
    InterviewTip(
        title = "Research Before You Enter",
        description = "Deep-research the company's: recent product launches, tech stack, team culture (Glassdoor, LinkedIn), recent funding rounds, and the interviewer's background. Reference specific company achievements during answers to show genuine interest.",
        icon = Icons.Rounded.Search,
        color = Color(0xFF0984E3),
        category = "Mindset",
        importance = 3
    ),
    InterviewTip(
        title = "Speak at 130-160 WPM",
        description = "The ideal speaking pace for interviews is 130-160 words per minute. Too slow (< 100 WPM) sounds hesitant; too fast (> 180 WPM) sounds anxious. Record yourself and count. AIIC voice mode measures your WPM in real-time during practice sessions.",
        icon = Icons.Rounded.Speed,
        color = Color(0xFFFDAA5E),
        category = "Communication",
        importance = 2
    ),
    InterviewTip(
        title = "Eliminate Filler Words",
        description = "\"Um\", \"uh\", \"like\", \"you know\", \"basically\" — these erode credibility. Replace pauses with silence. A 2-second pause while thinking looks confident, not confused. Practice by recording answers and counting filler words per answer. Target < 2%.",
        icon = Icons.Rounded.RecordVoiceOver,
        color = Color(0xFFE17055),
        category = "Communication",
        importance = 3
    ),
    InterviewTip(
        title = "The 30-60-90 Day Plan",
        description = "When asked \"Where do you see yourself in this role?\", present a 30-60-90 day plan. First 30 days: learn and listen. Days 31-60: contribute and collaborate. Days 61-90: lead an initiative. This shows strategic thinking and ambition.",
        icon = Icons.Rounded.Timeline,
        color = Color(0xFF6C5CE7),
        category = "Mindset",
        importance = 2
    ),
    InterviewTip(
        title = "Mirror the Interviewer's Energy",
        description = "Match the interviewer's communication style and energy level. If they're formal — be formal. If casual — relax slightly. Mirroring builds unconscious rapport. Also subtly mirror their body posture (not immediately, but gradually).",
        icon = Icons.Rounded.People,
        color = Color(0xFF00CEC9),
        category = "Body Language",
        importance = 2
    ),
    InterviewTip(
        title = "Deep-Dive Technical Answers",
        description = "For technical questions, explain your thought process out loud before answering. Say \"Let me think through this systematically\" then break the problem into components. Interviewers care more about HOW you think than WHAT you know.",
        icon = Icons.Rounded.Code,
        color = Color(0xFF0984E3),
        category = "Technical",
        importance = 3
    ),
    InterviewTip(
        title = "Ask Brilliant Questions",
        description = "Prepare 3-5 unique, high-signal questions that show strategic thinking: \"What does success look like in this role at 6 months?\", \"What's the biggest technical challenge the team is solving?\", \"How do you measure growth for someone in this position?\" Avoid salary and PTO questions early.",
        icon = Icons.Rounded.QuestionAnswer,
        color = Color(0xFF6C5CE7),
        category = "Mindset",
        importance = 3
    ),
    InterviewTip(
        title = "Power Posture Before Interview",
        description = "2 minutes before the interview, stand in a power pose (hands on hips, feet shoulder-width apart, chin up). Research shows this reduces cortisol and increases testosterone, reducing anxiety and boosting confidence. Do it in private before the call.",
        icon = Icons.Rounded.FitnessCenter,
        color = Color(0xFF00B894),
        category = "Body Language",
        importance = 1
    ),
    InterviewTip(
        title = "Handle \"Tell Me About Yourself\" Perfectly",
        description = "Use the Present-Past-Future framework: Current role and biggest achievement → Relevant past experience that led here → Why this specific role excites you. Keep it to 90 seconds. Treat it as a career highlight reel, not a CV recitation.",
        icon = Icons.Rounded.Person,
        color = Color(0xFFFDAA5E),
        category = "Communication",
        importance = 3
    ),
    InterviewTip(
        title = "System Design: Think Scalability First",
        description = "For system design questions: clarify requirements → estimate scale (DAU, QPS, storage) → design high-level architecture → deep-dive components → discuss trade-offs. Always mention: load balancing, caching strategy, database choice, and failure modes.",
        icon = Icons.Rounded.Hub,
        color = Color(0xFFE17055),
        category = "Technical",
        importance = 3
    )
)
