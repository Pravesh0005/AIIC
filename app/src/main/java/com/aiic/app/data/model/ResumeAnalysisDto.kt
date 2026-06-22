package com.aiic.app.data.model

import com.aiic.app.domain.model.AtsScoreDetails
import com.aiic.app.domain.model.Recommendation
import com.aiic.app.domain.model.ResumeAnalysis

data class ResumeAnalysisDto(
    val analysisId: String = "",
    val resumeId: String = "",
    val userId: String = "",
    val timestamp: Long = 0L,
    val overallScore: Int = 0,
    val atsScoreDetails: AtsScoreDetailsDto = AtsScoreDetailsDto(),
    val profileSummary: String = "",
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val riskAreas: List<String> = emptyList(),
    val recruiterImpression: String = "",
    val hirePotential: String = "",
    val skills: Map<String, List<String>> = emptyMap(),
    val missingKeywords: List<String> = emptyList(),
    val recommendations: List<RecommendationDto> = emptyList()
)

data class AtsScoreDetailsDto(
    val skillsScore: Int = 0,
    val projectScore: Int = 0,
    val experienceScore: Int = 0,
    val keywordScore: Int = 0,
    val structureScore: Int = 0,
    val completenessScore: Int = 0
)

data class RecommendationDto(
    val category: String = "",
    val suggestion: String = "",
    val priority: String = ""
)

fun ResumeAnalysisDto.toDomain(): ResumeAnalysis {
    return ResumeAnalysis(
        analysisId = analysisId,
        resumeId = resumeId,
        userId = userId,
        timestamp = timestamp,
        overallScore = overallScore,
        atsScoreDetails = atsScoreDetails.toDomain(),
        profileSummary = profileSummary,
        strengths = strengths,
        weaknesses = weaknesses,
        riskAreas = riskAreas,
        recruiterImpression = recruiterImpression,
        hirePotential = hirePotential,
        skills = skills,
        missingKeywords = missingKeywords,
        recommendations = recommendations.map { it.toDomain() }
    )
}

fun AtsScoreDetailsDto.toDomain(): AtsScoreDetails {
    return AtsScoreDetails(
        skillsScore = skillsScore,
        projectScore = projectScore,
        experienceScore = experienceScore,
        keywordScore = keywordScore,
        structureScore = structureScore,
        completenessScore = completenessScore
    )
}

fun RecommendationDto.toDomain(): Recommendation {
    return Recommendation(
        category = category,
        suggestion = suggestion,
        priority = priority
    )
}

fun ResumeAnalysis.toDto(): ResumeAnalysisDto {
    return ResumeAnalysisDto(
        analysisId = analysisId,
        resumeId = resumeId,
        userId = userId,
        timestamp = timestamp,
        overallScore = overallScore,
        atsScoreDetails = AtsScoreDetailsDto(
            skillsScore = atsScoreDetails.skillsScore,
            projectScore = atsScoreDetails.projectScore,
            experienceScore = atsScoreDetails.experienceScore,
            keywordScore = atsScoreDetails.keywordScore,
            structureScore = atsScoreDetails.structureScore,
            completenessScore = atsScoreDetails.completenessScore
        ),
        profileSummary = profileSummary,
        strengths = strengths,
        weaknesses = weaknesses,
        riskAreas = riskAreas,
        recruiterImpression = recruiterImpression,
        hirePotential = hirePotential,
        skills = skills,
        missingKeywords = missingKeywords,
        recommendations = recommendations.map { RecommendationDto(it.category, it.suggestion, it.priority) }
    )
}
