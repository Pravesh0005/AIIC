package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.getOrNull
import com.aiic.app.domain.model.InterviewConfig
import com.aiic.app.domain.model.InterviewQuestion
import com.aiic.app.domain.repository.GenerativeAiRepository
import com.aiic.app.domain.repository.InterviewQuestionRepository
import java.util.UUID
import javax.inject.Inject

class FirestoreInterviewQuestionRepository @Inject constructor(
    private val generativeAiRepository: GenerativeAiRepository
) : InterviewQuestionRepository {

    private val questionsCache = mutableListOf<InterviewQuestion>()

    override suspend fun generateInitialQuestions(
        config: InterviewConfig,
        resumeContext: String
    ): NetworkResult<List<InterviewQuestion>> {
        val prompt = """
            You are an expert technical recruiter and hiring manager.
            Generate ${config.questionCount} interview questions for a ${config.role} role.
            Interview Type: ${config.interviewType.name}
            Difficulty: ${config.difficulty.name}
            Resume Context: ${resumeContext.ifBlank { "None provided." }}
            
            IMPORTANT RULES:
            - If Interview Type is MIXED, you MUST include a balance of highly TECHNICAL coding/architecture questions and behavioral questions. Do not only ask behavioral questions.
            - If Interview Type is TECHNICAL, ask only deep technical questions.
            
            Return the output as a list of questions separated by newlines. No formatting or numbers.
        """.trimIndent()

        val aiResult = generativeAiRepository.generateText(prompt)
        
        val aiResponse = aiResult.getOrNull()
        return if (aiResponse != null) {
            val generatedLines = aiResponse.split("\n").filter { it.isNotBlank() }
            val questions = generatedLines.take(config.questionCount).mapIndexed { index, content ->
                InterviewQuestion(
                    questionId = UUID.randomUUID().toString(),
                    content = content.replace(Regex("^\\d+\\.\\s*"), "").trim(), // Strip numbers
                    order = index + 1
                )
            }
            NetworkResult.Success(questions)
        } else {
            // Fallback generic questions
            val fallbackBase = listOf(
                "Tell me about a time you solved a difficult problem regarding ${config.role}.",
                "What is your biggest strength related to ${config.role}?",
                "How do you handle tight deadlines and pressure?",
                "Describe a situation where you had a conflict with a team member.",
                "What are your core technical skills for a ${config.role} position?",
                "Where do you see yourself in 5 years?",
                "How do you stay updated with the latest trends in ${config.role}?"
            )
            val fallback = fallbackBase.shuffled().take(config.questionCount).mapIndexed { i, content ->
                InterviewQuestion(
                    questionId = UUID.randomUUID().toString(),
                    content = content,
                    order = i + 1
                )
            }
            NetworkResult.Success(fallback)
        }
    }

    override suspend fun generateFollowUpQuestion(
        previousQuestion: String,
        answer: String
    ): NetworkResult<InterviewQuestion?> {
        val prompt = """
            You are an expert interviewer.
            Previous Question: $previousQuestion
            Candidate Answer: $answer
            
            Based on the candidate's answer, generate ONE specific follow-up question that digs deeper into their response. 
            If the answer is comprehensive and perfect, reply exactly with "NO_FOLLOW_UP".
            Do not use numbers or bullet points.
        """.trimIndent()

        val aiResult = generativeAiRepository.generateText(prompt)
        val aiResponse = aiResult.getOrNull()
        if (aiResponse != null) {
            val followUp = aiResponse.trim()
            if (followUp == "NO_FOLLOW_UP" || followUp.isBlank()) {
                return NetworkResult.Success(null)
            }
            
            return NetworkResult.Success(
                InterviewQuestion(
                    questionId = UUID.randomUUID().toString(),
                    content = followUp,
                    isFollowUp = true
                )
            )
        }
        return NetworkResult.Success(null)
    }

    override suspend fun saveQuestions(questions: List<InterviewQuestion>): NetworkResult<Unit> {
        questionsCache.addAll(questions)
        return NetworkResult.Success(Unit)
    }

    override suspend fun getQuestionsForSession(sessionId: String): NetworkResult<List<InterviewQuestion>> {
        val sessionQuestions = questionsCache.filter { it.sessionId == sessionId }
        return NetworkResult.Success(sessionQuestions)
    }
}
