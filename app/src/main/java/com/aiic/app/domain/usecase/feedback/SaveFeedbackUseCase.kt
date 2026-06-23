package com.aiic.app.domain.usecase.feedback

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.AnswerFeedback
import com.aiic.app.domain.repository.FeedbackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveFeedbackUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository
) {
    suspend operator fun invoke(feedback: AnswerFeedback): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        feedbackRepository.saveAnswerFeedback(feedback)
    }
}
