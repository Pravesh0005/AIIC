package com.aiic.app.domain.usecase.analysis

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.domain.repository.GenerativeAiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnalyzeResumeUseCase @Inject constructor(
    private val extractResumeTextUseCase: ExtractResumeTextUseCase,
    private val generativeAiRepository: GenerativeAiRepository,
    private val saveResumeAnalysisUseCase: SaveResumeAnalysisUseCase
) {
    /**
     * Orchestrates the complete Day 3B AI Pipeline:
     * 1. Extract Text from PDF
     * 2. Parse Text via LLM Intelligence Engine
     * 3. Save resulting analysis to Firestore
     * 4. Return result to UI
     */
    suspend operator fun invoke(userId: String, resumeId: String): NetworkResult<ResumeAnalysis> {
        return withContext(Dispatchers.IO) {
            
            // Stage 1 & 2: Retrieval and Extraction
            when (val textResult = extractResumeTextUseCase(userId, resumeId)) {
                is NetworkResult.Error -> return@withContext NetworkResult.Error("Failed to extract text from resume: ${textResult.message}")
                is NetworkResult.Success -> {
                    val rawText = textResult.data
                    
                    // Stage 3-8: Cleaning, Parsing, Skill Extraction, Keyword Analysis, ATS Evaluation, Recommendations
                    when (val analysisResult = generativeAiRepository.generateResumeAnalysis(userId, resumeId, rawText)) {
                        is NetworkResult.Error -> return@withContext NetworkResult.Error("AI Analysis failed: ${analysisResult.message}")
                        is NetworkResult.Success -> {
                            val finalAnalysis = analysisResult.data
                            
                            // Stage 9: Firestore Persistence
                            val saveResult = saveResumeAnalysisUseCase(finalAnalysis)
                            if (saveResult is NetworkResult.Error) {
                                // We can choose to return success with a warning or fail completely.
                                // For an intelligence layer, returning the analysis even if save fails is usually better UX.
                            }
                            
                            // Stage 10 is UI Presentation (handled by ViewModel receiving this Success)
                            NetworkResult.Success(finalAnalysis)
                        }
                    }
                }
            }
        }
    }
}
