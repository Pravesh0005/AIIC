package com.aiic.app.presentation.feature_analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.InterviewSession
import com.aiic.app.domain.repository.AuthRepository
import com.aiic.app.domain.repository.InterviewSessionRepository
import com.aiic.app.domain.repository.InterviewReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyticsDashboardState(
    val isLoading: Boolean = true,
    val sessions: List<InterviewSession> = emptyList(),
    val scoreHistory: List<Float> = emptyList(),
    val averageScore: Float = 0f,
    val bestScore: Float = 0f,
    val totalSessions: Int = 0,
    val improvementRate: Float = 0f,
    val practiceStreak: Int = 0,
    val totalHoursPracticed: Int = 0,
    val performanceLabel: String = "Beginner",
    val typeBreakdown: Map<String, Float> = emptyMap(),
    // Average dimension scores
    val avgTechnical: Float = 0f,
    val avgCommunication: Float = 0f,
    val avgConfidence: Float = 0f,
    val avgProblemSolving: Float = 0f,
    val avgStructure: Float = 0f,
    val avgProfessionalism: Float = 0f,
    val error: String? = null
)

@HiltViewModel
class AnalyticsDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: InterviewSessionRepository,
    private val reportRepository: InterviewReportRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsDashboardState())
    val state: StateFlow<AnalyticsDashboardState> = _state.asStateFlow()

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        val userId = authRepository.getCurrentSession()?.uid ?: return

        viewModelScope.launch {
            _state.value = AnalyticsDashboardState(isLoading = true)

            // Collect all completed sessions
            sessionRepository.getSessionHistory(userId).collect { sessions ->
                if (sessions.isEmpty()) {
                    _state.value = AnalyticsDashboardState(isLoading = false)
                    return@collect
                }

                val scores = sessions.mapNotNull { it.score }.filter { it > 0 }
                val avgScore = if (scores.isNotEmpty()) scores.average().toFloat() else 0f
                val bestScore = scores.maxOrNull() ?: 0f

                // Improvement rate = last 3 sessions avg vs first 3 sessions avg
                val improvementRate = if (scores.size >= 6) {
                    val recent = scores.takeLast(3).average().toFloat()
                    val early = scores.take(3).average().toFloat()
                    recent - early
                } else 0f

                // Total hours: sum of durations
                val totalMs = sessions.sumOf {
                    val end = it.endedAt ?: it.startedAt
                    (end - it.startedAt).coerceAtLeast(0L)
                }
                val totalHours = (totalMs / 3_600_000L).toInt().coerceAtLeast(0)

                // Type breakdown
                val typeBreakdown = sessions
                    .groupBy { it.interviewType.name.replace("_", " ") }
                    .mapValues { (_, list) ->
                        list.mapNotNull { it.score }.average().toFloat()
                    }

                // Performance label
                val label = when {
                    avgScore >= 90 -> "Expert 🏆"
                    avgScore >= 75 -> "Advanced ⭐"
                    avgScore >= 60 -> "Intermediate 📈"
                    avgScore >= 40 -> "Learning 🌱"
                    else -> "Beginner 🚀"
                }

                // Fetch reports for detailed dimension metrics
                var avgTech = 0f
                var avgComm = 0f
                var avgConf = 0f
                var avgProblem = 0f
                var avgStruct = 0f
                var avgProf = 0f

                val reports = sessions.mapNotNull { session ->
                    when (val result = reportRepository.getReport(session.sessionId)) {
                        is NetworkResult.Success -> result.data
                        else -> null
                    }
                }

                if (reports.isNotEmpty()) {
                    avgTech = reports.map { it.technicalAccuracyScore }.average().toFloat()
                    avgComm = reports.map { it.communicationScore }.average().toFloat()
                    avgConf = reports.map { it.confidenceScore }.average().toFloat()
                    avgProblem = reports.map { it.problemSolvingScore }.average().toFloat()
                    avgStruct = reports.map { it.structureScore }.average().toFloat()
                    avgProf = reports.map { it.professionalismScore }.average().toFloat()
                }

                _state.value = AnalyticsDashboardState(
                    isLoading = false,
                    sessions = sessions.sortedByDescending { it.endedAt },
                    scoreHistory = scores.takeLast(10),
                    averageScore = avgScore,
                    bestScore = bestScore,
                    totalSessions = sessions.size,
                    improvementRate = improvementRate,
                    practiceStreak = calculateStreak(sessions),
                    totalHoursPracticed = totalHours,
                    performanceLabel = label,
                    typeBreakdown = typeBreakdown,
                    avgTechnical = avgTech,
                    avgCommunication = avgComm,
                    avgConfidence = avgConf,
                    avgProblemSolving = avgProblem,
                    avgStructure = avgStruct,
                    avgProfessionalism = avgProf
                )
            }
        }
    }

    private fun calculateStreak(sessions: List<InterviewSession>): Int {
        if (sessions.isEmpty()) return 0
        val daysSorted = sessions
            .mapNotNull { it.endedAt }
            .map { it / 86_400_000L } // Convert to day number
            .distinct()
            .sortedDescending()

        var streak = 0
        var expectedDay = daysSorted.firstOrNull() ?: return 0

        for (day in daysSorted) {
            if (day == expectedDay) {
                streak++
                expectedDay--
            } else break
        }
        return streak
    }
}
