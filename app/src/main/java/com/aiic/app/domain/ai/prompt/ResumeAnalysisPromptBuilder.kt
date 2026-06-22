package com.aiic.app.domain.ai.prompt

class ResumeAnalysisPromptBuilder {

    private var rawText: String = ""
    private var includeSchema: Boolean = true

    fun setRawResumeText(text: String) = apply { this.rawText = text }
    
    fun includeJsonSchema(include: Boolean) = apply { this.includeSchema = include }

    fun buildSystemPrompt(): String {
        return PromptTemplates.RESUME_ANALYSIS_SYSTEM_PROMPT
    }

    fun buildUserPrompt(): String {
        require(rawText.isNotBlank()) { "Raw resume text must be provided to build the prompt." }
        var prompt = PromptFormatter.formatResumeAnalysisUserPrompt(rawText)
        if (includeSchema) {
            prompt += "\n\nEXPECTED JSON SCHEMA:\n" + PromptFormatter.getAnalysisJsonSchema()
        }
        return prompt
    }
}
