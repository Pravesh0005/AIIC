package com.aiic.app.domain.ai.prompt

object PromptFormatter {
    
    fun formatResumeAnalysisUserPrompt(rawResumeText: String): String {
        
        val cleanedText = rawResumeText.replace(Regex("\\s+"), " ").trim()
        return String.format(PromptTemplates.RESUME_ANALYSIS_USER_PROMPT_TEMPLATE, cleanedText)
    }

    fun getAnalysisJsonSchema(): String {
        return """
        {
            "overallScore": Int,
            "atsScoreDetails": { "skillsScore": Int, "projectScore": Int, "experienceScore": Int, "keywordScore": Int, "structureScore": Int, "completenessScore": Int },
            "profileSummary": "String",
            "strengths": ["String"],
            "weaknesses": ["String"],
            "riskAreas": ["String"],
            "recruiterImpression": "String",
            "hirePotential": "String",
            "skills": { "CategoryName": ["Skill1", "Skill2"] },
            "missingKeywords": ["String"],
            "recommendations": [ { "category": "String", "suggestion": "String", "priority": "High|Medium|Low" } ]
        }
        """.trimIndent()
    }
}
