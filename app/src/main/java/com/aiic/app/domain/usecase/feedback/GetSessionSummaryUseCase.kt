package com.aiic.app.domain.usecase.feedback

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.SessionSummary
import com.aiic.app.domain.repository.FeedbackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetSessionSummaryUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository
) {
    suspend operator fun invoke(sessionId: String): NetworkResult<SessionSummary> = withContext(Dispatchers.IO) {
        val generateResult = feedbackRepository.generateAndSaveSessionSummary(sessionId)
        if (generateResult is NetworkResult.Success) {
            return@withContext generateResult
        }
        // Fallback to getting existing if generation failed
        feedbackRepository.getSessionSummary(sessionId)
    }
}
