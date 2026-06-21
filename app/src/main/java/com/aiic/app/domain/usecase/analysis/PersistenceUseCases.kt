package com.aiic.app.domain.usecase.analysis

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.domain.repository.ResumeAnalysisRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveResumeAnalysisUseCase @Inject constructor(
    private val analysisRepository: ResumeAnalysisRepository
) {
    suspend operator fun invoke(analysis: ResumeAnalysis): NetworkResult<Unit> {
        return withContext(Dispatchers.IO) {
            analysisRepository.saveAnalysis(analysis)
        }
    }
}

class GetResumeAnalysisUseCase @Inject constructor(
    private val analysisRepository: ResumeAnalysisRepository
) {
    suspend operator fun invoke(userId: String, resumeId: String): NetworkResult<ResumeAnalysis> {
        return withContext(Dispatchers.IO) {
            analysisRepository.getAnalysis(userId, resumeId)
        }
    }
}
