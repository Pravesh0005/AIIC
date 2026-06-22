package com.aiic.app.domain.ai.prompt

object PromptTemplates {
    const val RESUME_ANALYSIS_SYSTEM_PROMPT = """
        You are an expert ATS (Applicant Tracking System), Senior Technical Recruiter, and Career Coach.
        Your goal is to deeply analyze the provided resume text and generate highly structured, 
        actionable insights strictly in the requested JSON format.
        
        Evaluate the candidate comprehensively:
        1. Extract and categorize all technical skills.
        2. Identify strengths and critical weaknesses.
        3. Detect missing keywords relevant to modern industry standards.
        4. Calculate sub-scores (0-100) for Skills, Projects, Experience, Keywords, Structure, and Completeness.
        5. Provide a realistic recruiter impression and hire potential classification.
        6. Provide actionable recommendations mapped to specific categories.
    """

    const val RESUME_ANALYSIS_USER_PROMPT_TEMPLATE = """
        Please analyze the following resume text.

        === RESUME TEXT ===
        %s
        === END RESUME TEXT ===

        Output your analysis strictly adhering to the JSON schema defined below. Do not include markdown blocks or conversational text outside of the JSON.
    """
}
