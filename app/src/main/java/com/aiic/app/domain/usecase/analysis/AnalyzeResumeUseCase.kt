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
    
    suspend operator fun invoke(userId: String, resumeId: String): NetworkResult<ResumeAnalysis> {
        return withContext(Dispatchers.IO) {
            
            when (val textResult = extractResumeTextUseCase(userId, resumeId)) {
                is NetworkResult.Error -> return@withContext NetworkResult.Error(message = "Failed to extract text from resume: ${textResult.message}")
                is NetworkResult.Success -> {
                    val rawText = textResult.data
                    
                    when (val analysisResult = generativeAiRepository.generateResumeAnalysis(userId, resumeId, rawText)) {
                        is NetworkResult.Error -> return@withContext NetworkResult.Error(message = "AI Analysis failed: ${analysisResult.message}")
                        is NetworkResult.Success -> {
                            val finalAnalysis = analysisResult.data
                            
                            val saveResult = saveResumeAnalysisUseCase(finalAnalysis)
                            if (saveResult is NetworkResult.Error) {
                                
                            }
                            
                            NetworkResult.Success(finalAnalysis)
                        }
                    }
                }
            }
        }
    }
}
