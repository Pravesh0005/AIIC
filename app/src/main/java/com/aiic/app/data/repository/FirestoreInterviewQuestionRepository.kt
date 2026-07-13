package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.getOrNull
import com.aiic.app.domain.model.InterviewConfig
import com.aiic.app.domain.model.InterviewQuestion
import com.aiic.app.domain.model.InterviewType
import com.aiic.app.domain.model.InterviewDifficulty
import com.aiic.app.domain.model.QuestionCategory
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
        
        val pastQuestions = questionsCache.map { it.content }.takeLast(30).joinToString("\n- ")

        val roleSpecificGuidance = getRoleSpecificGuidance(config.role, config.interviewType)
        val difficultyGuidance = getDifficultyGuidance(config.difficulty)

        val prompt = buildInterviewPrompt(config, resumeContext, pastQuestions, roleSpecificGuidance, difficultyGuidance)

        val aiResult = generativeAiRepository.generateText(prompt)

        val aiResponse = aiResult.getOrNull()
        return if (aiResponse != null) {
            val generatedLines = aiResponse.split("\n")
                .map { it.replace(Regex("^\\d+\\.\\s*"), "").trim() }
                .filter { it.length > 10 }
            val questions = generatedLines.take(config.questionCount).mapIndexed { index, content ->
                val category = when {
                    content.startsWith("[TECHNICAL]", ignoreCase = true) -> QuestionCategory.TECHNICAL
                    content.startsWith("[BEHAVIORAL]", ignoreCase = true) -> QuestionCategory.BEHAVIORAL
                    content.startsWith("[PROJECT]", ignoreCase = true) -> QuestionCategory.PROJECT_BASED
                    content.startsWith("[HR]", ignoreCase = true) -> QuestionCategory.HR_GENERAL
                    else -> QuestionCategory.TECHNICAL
                }
                val cleanContent = content
                    .replace(Regex("^\\[(TECHNICAL|BEHAVIORAL|PROJECT|HR)\\]\\s*", RegexOption.IGNORE_CASE), "")
                    .replace(Regex("^[-*]\\s*"), "").trim()
                InterviewQuestion(
                    questionId = UUID.randomUUID().toString(),
                    content = cleanContent,
                    category = category,
                    order = index + 1
                )
            }
            if (questions.size >= config.questionCount) {
                NetworkResult.Success(questions)
            } else {
                
                val supplemented = questions.toMutableList()
                val fallback = getRoleFallbackQuestions(config.role, config.interviewType)
                    .shuffled()
                    .filter { fb -> supplemented.none { it.content == fb } }
                var order = supplemented.size + 1
                for (fb in fallback) {
                    if (supplemented.size >= config.questionCount) break
                    supplemented.add(InterviewQuestion(
                        questionId = UUID.randomUUID().toString(),
                        content = fb,
                        order = order++
                    ))
                }
                NetworkResult.Success(supplemented)
            }
        } else {
            
            val fallback = getRoleFallbackQuestions(config.role, config.interviewType)
                .shuffled()
                .take(config.questionCount)
                .mapIndexed { i, content ->
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
You are an expert interviewer conducting a technical interview.
Previous Question: $previousQuestion
Candidate Answer: $answer

Based on the candidate's answer, generate ONE specific follow-up question that digs deeper.
If the answer is comprehensive, reply exactly with "NO_FOLLOW_UP".
No numbers or bullet points.
""".trimIndent()

        val aiResult = generativeAiRepository.generateText(prompt)
        val aiResponse = aiResult.getOrNull()
        if (aiResponse != null) {
            val followUp = aiResponse.trim()
            if (followUp.contains("NO_FOLLOW_UP") || followUp.isBlank()) {
                return NetworkResult.Success(null)
            }

            return NetworkResult.Success(
                InterviewQuestion(
                    questionId = UUID.randomUUID().toString(),
                    content = followUp.replace(Regex("^\\d+\\.\\s*"), "").trim(),
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

    private fun getRoleSpecificGuidance(role: String, type: InterviewType): String {
        val roleLower = role.lowercase()
        return when {
            roleLower.contains("android") -> """
ROLE-SPECIFIC GUIDANCE (Android Developer):
Ask about: Jetpack Compose recomposition, StateFlow vs LiveData, Hilt dependency injection,
Room database vs Firestore, ViewModel lifecycle, configuration changes, coroutines vs RxJava,
WorkManager vs AlarmManager, ProGuard/R8, Retrofit interceptors, Navigation component,
Content Providers, Broadcast Receivers, Activity/Fragment lifecycle, Material Design 3.
"""
            roleLower.contains("backend") || roleLower.contains("server") -> """
ROLE-SPECIFIC GUIDANCE (Backend Developer):
Ask about: REST vs GraphQL, WebSocket vs HTTP long-polling, JWT authentication flow,
Redis caching strategies, database indexing and query optimization, message queues (Kafka/RabbitMQ),
microservices vs monolith, API rate limiting, CORS, load balancing strategies,
what happens when a request hits your server, connection pooling, N+1 query problem,
CAP theorem, database sharding, CI/CD pipelines, containerization (Docker/K8s).
"""
            roleLower.contains("frontend") || roleLower.contains("react") || roleLower.contains("web") -> """
ROLE-SPECIFIC GUIDANCE (Frontend Developer):
Ask about: Virtual DOM reconciliation, React hooks lifecycle, state management (Redux/Zustand),
CSS specificity, Flexbox vs Grid, Web Vitals optimization, lazy loading strategies,
SSR vs CSR vs ISR, hydration, accessibility (WCAG), event delegation, debouncing/throttling,
webpack/vite bundling, TypeScript generics, service workers, Web Workers.
"""
            roleLower.contains("full stack") || roleLower.contains("fullstack") -> """
ROLE-SPECIFIC GUIDANCE (Full Stack Developer):
Mix frontend AND backend questions equally.
Frontend: React/Next.js patterns, state management, CSS architecture, performance optimization.
Backend: API design, database modeling, authentication, caching, deployment.
System Design: Design a URL shortener, design a chat system, design a notification service.
"""
            roleLower.contains("ai") || roleLower.contains("ml") || roleLower.contains("machine learning") || roleLower.contains("data science") -> """
ROLE-SPECIFIC GUIDANCE (AI/ML Engineer):
Ask about: Gradient descent variants, transformer architecture, attention mechanism,
overfitting vs underfitting, regularization techniques, CNN vs RNN vs Transformer,
BERT vs GPT architecture differences, fine-tuning vs transfer learning, RAG pipelines,
vector databases, embedding models, precision/recall/F1, confusion matrix,
model deployment (ONNX, TensorRT), MLOps, A/B testing ML models, feature engineering.
"""
            roleLower.contains("devops") || roleLower.contains("sre") || roleLower.contains("cloud") -> """
ROLE-SPECIFIC GUIDANCE (DevOps/Cloud Engineer):
Ask about: Docker multi-stage builds, Kubernetes pod lifecycle, Helm charts,
CI/CD pipeline design, infrastructure as code (Terraform), monitoring (Prometheus/Grafana),
log aggregation (ELK stack), blue-green vs canary deployment, service mesh (Istio),
cloud networking (VPC, subnets), IAM policies, auto-scaling strategies, disaster recovery.
"""
            roleLower.contains("product manager") || roleLower.contains("pm") -> """
ROLE-SPECIFIC GUIDANCE (Product Manager):
Ask about: Prioritization frameworks (RICE, MoSCoW), user story writing, sprint planning,
A/B testing design, product metrics (DAU, retention, NPS), competitive analysis,
stakeholder management, roadmap planning, feature trade-off analysis,
go-to-market strategy, user research methods, data-driven decision making.
"""
            else -> """
ROLE-SPECIFIC GUIDANCE (${role}):
Ask technical questions specific to this role's domain.
Include architecture, tools, frameworks, and problem-solving scenarios relevant to $role.
Avoid generic behavioral questions. Be specific and practical.
"""
        }
    }

    private fun getDifficultyGuidance(difficulty: InterviewDifficulty): String {
        return when (difficulty) {
            InterviewDifficulty.EASY -> "DIFFICULTY: Ask foundational questions. Test basic understanding of concepts."
            InterviewDifficulty.MEDIUM -> "DIFFICULTY: Ask intermediate questions. Expect practical experience and trade-off analysis."
            InterviewDifficulty.HARD -> "DIFFICULTY: Ask senior-level questions. Expect deep system design, edge cases, and architecture decisions."
        }
    }

    private fun getRoleFallbackQuestions(role: String, type: InterviewType): List<String> {
        val roleLower = role.lowercase()

        val technicalPool = when {
            roleLower.contains("android") -> listOf(
                "What is the difference between StateFlow and LiveData, and when would you choose one over the other?",
                "Explain how Jetpack Compose handles recomposition. What triggers it and how do you optimize it?",
                "How does Hilt dependency injection work in Android? Walk me through the annotation hierarchy.",
                "What happens during a configuration change in Android, and how do you preserve state?",
                "Compare Room database with Firestore for offline-first Android apps.",
                "Explain the difference between viewModelScope and lifecycleScope.",
                "How would you implement a paginated list with Paging 3 library?",
                "What is the purpose of remember and rememberSaveable in Compose?",
                "How do you handle deep linking in a Jetpack Navigation app?",
                "Explain how ProGuard/R8 works and what problems it can cause.",
                "What are Content Providers and when would you use them vs direct database access?",
                "How does WorkManager differ from AlarmManager for background tasks?"
            )
            roleLower.contains("backend") || roleLower.contains("server") -> listOf(
                "What happens step by step when an HTTP request reaches your backend server?",
                "Explain the difference between REST and GraphQL. When would you choose each?",
                "What is JWT authentication? Walk me through the complete flow.",
                "How does Redis caching work? What eviction strategies do you know?",
                "Explain database indexing. When can indexes hurt performance?",
                "What is the N+1 query problem and how do you solve it?",
                "Compare WebSocket with HTTP long-polling for real-time communication.",
                "How would you design an API rate limiter?",
                "What is the CAP theorem? Give a real-world example.",
                "Explain microservices vs monolith architecture trade-offs.",
                "How does connection pooling work in a database driver?",
                "What is database sharding and when would you implement it?"
            )
            roleLower.contains("frontend") || roleLower.contains("react") || roleLower.contains("web") -> listOf(
                "Explain the Virtual DOM and how React's reconciliation algorithm works.",
                "What is the difference between useEffect, useMemo, and useCallback?",
                "How does CSS specificity work? Give examples of specificity conflicts.",
                "Compare SSR, CSR, and ISR. When would you use each?",
                "What are Web Vitals and how do you optimize LCP, FID, and CLS?",
                "Explain event delegation in JavaScript and why it's useful.",
                "What is hydration in the context of server-side rendered applications?",
                "How would you implement lazy loading for images and components?",
                "Compare Redux, Zustand, and React Context for state management.",
                "What are Service Workers and how do they enable offline functionality?",
                "Explain debouncing vs throttling with real-world examples.",
                "How does TypeScript's type system improve code quality? Give generic examples."
            )
            roleLower.contains("full stack") || roleLower.contains("fullstack") -> listOf(
                "Design a URL shortener service. Walk me through frontend and backend.",
                "How would you implement real-time notifications across a full-stack app?",
                "Explain how authentication flows differ between SPAs and server-rendered apps.",
                "What is the difference between SQL and NoSQL? When would you choose each?",
                "How do you handle file uploads from frontend to backend to cloud storage?",
                "Design a basic chat application. What technologies would you use and why?",
                "Explain CORS. Why does it exist and how do you configure it?",
                "How would you optimize a slow-loading dashboard with both frontend and backend changes?",
                "What is the role of middleware in backend frameworks like Express or Django?",
                "Compare monorepo vs multi-repo for full-stack projects.",
                "How do you handle environment variables across development, staging, and production?",
                "Explain database migrations and why they matter in team development."
            )
            roleLower.contains("ai") || roleLower.contains("ml") || roleLower.contains("machine learning") -> listOf(
                "Explain the transformer architecture. What makes self-attention powerful?",
                "What is the difference between BERT and GPT architectures?",
                "How does gradient descent work? Compare SGD, Adam, and RMSProp.",
                "What is overfitting and how do you prevent it? Name at least 3 techniques.",
                "Explain the difference between fine-tuning and transfer learning.",
                "What is a RAG (Retrieval Augmented Generation) pipeline?",
                "How do vector databases work and why are they important for AI applications?",
                "Explain precision, recall, and F1 score. When does each matter most?",
                "What is the attention mechanism and why was it a breakthrough?",
                "How would you deploy a machine learning model to production?",
                "Compare CNNs, RNNs, and Transformers for sequence data.",
                "What is feature engineering and why is it important?"
            )
            else -> listOf(
                "What are the most important design patterns in your domain?",
                "How do you approach debugging a production issue you've never seen before?",
                "Explain a complex technical system you've built or contributed to.",
                "How would you design a scalable notification system?",
                "What is your approach to code reviews? What do you look for?",
                "Explain how you handle technical debt in a growing codebase.",
                "What testing strategies do you use and why?",
                "How do you decide between building vs buying a solution?",
                "Describe your approach to performance optimization.",
                "What security considerations do you keep in mind during development?",
                "How do you stay updated with new technologies in your field?",
                "Explain a time you had to make a significant technical trade-off."
            )
        }

        val behavioralPool = listOf(
            "Tell me about a time you disagreed with a technical decision on your team. How did you handle it?",
            "Describe a project where requirements changed significantly mid-development. What did you do?",
            "How do you prioritize tasks when you have multiple deadlines competing for your attention?",
            "Tell me about a time you mentored someone or helped a teammate grow technically.",
            "Describe a situation where you had to learn a new technology quickly to meet a deadline.",
            "How do you handle receiving critical feedback on your code or design decisions?"
        )

        return when (type) {
            InterviewType.TECHNICAL, InterviewType.CODING, InterviewType.DATABASE,
            InterviewType.ANDROID, InterviewType.BACKEND, InterviewType.FRONTEND,
            InterviewType.AI, InterviewType.MACHINE_LEARNING, InterviewType.CLOUD,
            InterviewType.DEVOPS, InterviewType.SYSTEM_DESIGN -> technicalPool
            InterviewType.BEHAVIORAL, InterviewType.HR, InterviewType.LEADERSHIP -> behavioralPool + technicalPool.take(3)
            InterviewType.MIXED -> technicalPool + behavioralPool
        }
    }

    private fun buildInterviewPrompt(
        config: InterviewConfig,
        resumeContext: String,
        pastQuestions: String,
        roleSpecificGuidance: String,
        difficultyGuidance: String
    ): String {
        val categoryInstruction = when (config.interviewType) {
            InterviewType.MIXED -> {
                val totalQ = config.questionCount
                val techCount = (totalQ * 0.4).toInt().coerceAtLeast(1)
                val behavioralCount = (totalQ * 0.3).toInt().coerceAtLeast(1)
                val projectCount = (totalQ * 0.2).toInt().coerceAtLeast(1)
                val hrCount = (totalQ - techCount - behavioralCount - projectCount).coerceAtLeast(0)
                """
CATEGORY DISTRIBUTION (MANDATORY):
- Exactly $techCount questions prefixed with [TECHNICAL] — deep technical/architecture questions
- Exactly $behavioralCount questions prefixed with [BEHAVIORAL] — situation-specific behavioral questions
- Exactly $projectCount questions prefixed with [PROJECT] — project/hands-on experience questions
- ${if (hrCount > 0) "Exactly $hrCount questions prefixed with [HR] — culture/HR questions" else "No HR questions needed"}
"""
            }
            InterviewType.TECHNICAL, InterviewType.CODING, InterviewType.DATABASE,
            InterviewType.ANDROID, InterviewType.BACKEND, InterviewType.FRONTEND,
            InterviewType.AI, InterviewType.MACHINE_LEARNING, InterviewType.CLOUD,
            InterviewType.DEVOPS, InterviewType.SYSTEM_DESIGN -> "\nAll questions must be prefixed with [TECHNICAL]. Every question must be deeply technical and specific to ${config.interviewType.name}.\n"
            InterviewType.BEHAVIORAL, InterviewType.LEADERSHIP -> "\nAll questions must be prefixed with [BEHAVIORAL]. Ask situation-specific behavioral and leadership questions.\n"
            InterviewType.HR -> "\nAll questions must be prefixed with [HR]. Ask HR and culture-fit questions.\n"
        }

        return """
You are an expert technical interviewer at a top-tier company.
Generate exactly ${config.questionCount} interview questions for the role: "${config.role}".

INTERVIEW TYPE: ${config.interviewType.name}
DIFFICULTY: ${config.difficulty.name}

CANDIDATE RESUME/CONTEXT:
${resumeContext.ifBlank { "No resume provided. Ask general role-specific questions." }}

$roleSpecificGuidance
$difficultyGuidance
$categoryInstruction

CRITICAL RULES:
1. Each question MUST start with its category tag: [TECHNICAL], [BEHAVIORAL], [PROJECT], or [HR].
2. Questions must be specific and role-relevant, not generic.
3. For MIXED interviews: strictly follow the category distribution above.
4. DO NOT ask generic HR questions like "tell me about yourself" in MIXED mode.
5. Each question must be unique.
6. DO NOT repeat these previously asked questions:
${if (pastQuestions.isNotBlank()) "- $pastQuestions" else "(none)"}

OUTPUT FORMAT:
Return ONLY the questions, one per line, each starting with its category tag.
Example: [TECHNICAL] How does Hilt dependency injection work in Android?
""".trimIndent()
    }
}
