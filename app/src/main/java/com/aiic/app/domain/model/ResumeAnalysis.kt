package com.aiic.app.domain.model

data class ResumeAnalysis(
    val analysisId: String,
    val resumeId: String,
    val userId: String,
    val timestamp: Long,
    val overallScore: Int,
    val atsScoreDetails: AtsScoreDetails,
    val profileSummary: String,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val riskAreas: List<String>,
    val recruiterImpression: String,
    val hirePotential: String,
    val skills: Map<String, List<String>>, 
    val missingKeywords: List<String>,
    val recommendations: List<Recommendation>
) {
    val classification: String
        get() = when (overallScore) {
            in 90..100 -> "Excellent"
            in 75..89 -> "Strong"
            in 50..74 -> "Intermediate"
            else -> "Beginner"
        }
}

data class AtsScoreDetails(
    val skillsScore: Int,
    val projectScore: Int,
    val experienceScore: Int,
    val keywordScore: Int,
    val structureScore: Int,
    val completenessScore: Int
)

data class Recommendation(
    val category: String, 
    val suggestion: String,
    val priority: String 
)
