package com.aiic.app.domain.usecase.analysis

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.AtsScoreDetails
import com.aiic.app.domain.model.Recommendation
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.domain.repository.GenerativeAiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalculateATSScoreUseCase @Inject constructor() {
    
    operator fun invoke(analysis: ResumeAnalysis): Int {
        val details = analysis.atsScoreDetails
        return (details.skillsScore * 0.3 + 
                details.experienceScore * 0.3 + 
                details.projectScore * 0.15 + 
                details.keywordScore * 0.1 + 
                details.structureScore * 0.1 + 
                details.completenessScore * 0.05).toInt()
    }
}

class GenerateRecommendationsUseCase @Inject constructor() {
    
    operator fun invoke(missingKeywords: List<String>): List<Recommendation> {
        return missingKeywords.map {
            Recommendation(
                category = "Keyword Optimization",
                suggestion = "Consider incorporating the term '$it' naturally into your experience or skills section.",
                priority = "High"
            )
        }
    }
}
