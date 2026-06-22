package com.aiic.app.domain.usecase

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.getOrNull
import com.aiic.app.domain.model.InterviewConfig
import com.aiic.app.domain.model.InterviewSession
import com.aiic.app.domain.model.InterviewQuestion
import com.aiic.app.domain.repository.InterviewSessionRepository
import com.aiic.app.domain.repository.InterviewQuestionRepository
import com.aiic.app.domain.repository.ResumeRepository
import javax.inject.Inject

class SetupInterviewSessionUseCase @Inject constructor(
    private val sessionRepository: InterviewSessionRepository,
    private val questionRepository: InterviewQuestionRepository,
    private val resumeRepository: ResumeRepository
) {
    suspend operator fun invoke(config: InterviewConfig, userId: String): NetworkResult<Pair<InterviewSession, List<InterviewQuestion>>> {
        // 1. Fetch Resume Context if provided
        var resumeContext = ""
        if (config.resumeId != null) {
            val resumeResult = resumeRepository.getResumeById(config.resumeId)
            val resume = resumeResult.getOrNull()
            if (resume != null) {
                resumeContext = resume.content ?: ""
            }
        }

        // 2. Create the Session in DB
        val sessionResult = sessionRepository.createSession(config, userId)
        val session = sessionResult.getOrNull()
        if (session == null) {
            val errorMsg = (sessionResult as? NetworkResult.Error)?.message ?: "Failed to create session"
            return NetworkResult.Error(message = errorMsg)
        }

        // 3. Generate Initial Questions via AI
        val questionsResult = questionRepository.generateInitialQuestions(config, resumeContext)
        val questions = questionsResult.getOrNull()
        if (questions == null) {
            val errorMsg = (questionsResult as? NetworkResult.Error)?.message ?: "Failed to generate questions"
            return NetworkResult.Error(message = errorMsg)
        }
        
        // 4. Assign SessionId to questions and save them
        val finalQuestions = questions.mapIndexed { index, q ->
            q.copy(sessionId = session.sessionId, order = index + 1)
        }
        
        val saveResult = questionRepository.saveQuestions(finalQuestions)
        if (saveResult.getOrNull() == null) {
            return NetworkResult.Error(message = "Failed to save questions to database")
        }

        return NetworkResult.Success(Pair(session, finalQuestions))
    }
}
