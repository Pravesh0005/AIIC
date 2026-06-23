# DAY 5: AIIC AI Feedback Engine Architecture & Implementation Plan

## 1. Day 5 Architecture Summary
The AI Feedback Engine transforms AIIC from a simple simulator into an intelligent career coach. The architecture follows Clean Architecture with a strict separation of concerns:
- **Presentation Layer**: `AnswerFeedbackScreen`, `SessionFeedbackScreen`, beautiful score rings, and actionable cards.
- **Domain Layer**: `FeedbackRepository`, `AnalyzeAnswerUseCase`, `GenerateSessionSummaryUseCase`.
- **Data Layer**: `FirestoreFeedbackRepositoryImpl`, structured `FeedbackDto`, and intelligent caching.
- **AI Layer**: `FeedbackPromptBuilder` to enforce structured JSON output for deterministic UI rendering.

## 2. Feedback Engine Design
The engine works in three layers:
1. **Real-time Deterministic Scoring**: Length, keyword matching, and structure validation.
2. **Deep Semantic AI Evaluation**: Llama 3/Gemini evaluates technical correctness, communication clarity, and role alignment.
3. **Session-Level Synthesis**: Aggregating all answer feedbacks to generate a final session performance and improvement roadmap.

## 3. Scoring System Design
Every answer is scored out of 100, broken down into:
- **Technical/Accuracy Score** (Weight: 40%)
- **Communication/Clarity Score** (Weight: 20%)
- **Relevance/Role Fit Score** (Weight: 20%)
- **Structure Score** (Weight: 10%)
- **Confidence Score** (Weight: 10%)

## 4. Data Models
```kotlin
data class AnswerFeedback(
    val feedbackId: String,
    val sessionId: String,
    val questionId: String,
    val answerText: String,
    val overallScore: Int,
    val technicalScore: Int,
    val communicationScore: Int,
    val relevanceScore: Int,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val improvementSuggestions: List<String>,
    val interviewerPerspective: String,
    val followUpQuestions: List<String>
)

data class SessionSummary(
    val sessionId: String,
    val averageScore: Int,
    val strongAreas: List<String>,
    val weakAreas: List<String>,
    val priorityImprovements: List<String>,
    val roleReadiness: String
)
```

## 5. Firestore Schema
- **`sessions/{sessionId}/feedbacks/{feedbackId}`**: Stores individual `AnswerFeedback` documents.
- **`sessions/{sessionId}/summary`**: Stores the `SessionSummary` document.
- **`users/{userId}/weak_areas`**: Aggregated history of recurrent weak areas across sessions.

## 6. Domain Repositories
```kotlin
interface FeedbackRepository {
    suspend fun saveAnswerFeedback(feedback: AnswerFeedback): NetworkResult<Unit>
    suspend fun getFeedbackForAnswer(answerId: String): NetworkResult<AnswerFeedback>
    suspend fun generateAndSaveSessionSummary(sessionId: String): NetworkResult<SessionSummary>
    suspend fun getSessionSummary(sessionId: String): NetworkResult<SessionSummary>
}
```

## 7. Use Cases
- `AnalyzeAnswerUseCase`: Invokes the AI Engine -> Parses JSON -> Saves to `FeedbackRepository` -> Returns `AnswerFeedback`.
- `GetSessionSummaryUseCase`: Fetches all feedbacks for a session -> Generates summary -> Returns `SessionSummary`.
- `GetWeakAreasUseCase`: Cross-references historical summaries.

## 8. Prompt Engine Design
`FeedbackPromptBuilder` generates highly structured prompts demanding JSON output:
```json
{
  "overallScore": 85,
  "technicalScore": 90,
  "communicationScore": 80,
  "relevanceScore": 85,
  "strengths": ["Clear explanation of ViewModels", "Good mention of StateFlow"],
  "weaknesses": ["Missed explaining Coroutines properly"],
  "improvementSuggestions": ["Elaborate on Dispatchers when talking about background work"],
  "interviewerPerspective": "Strong mid-level answer, but lacked deep multi-threading insight."
}
```

## 9. ViewModels
- `AnswerFeedbackViewModel`: Subscribes to feedback state for a specific answer. Emits `FeedbackUiState`.
- `SessionSummaryViewModel`: Calculates averages, handles the loading of the final summary.

## 10. UI States
```kotlin
sealed class FeedbackUiState {
    object Idle : FeedbackUiState()
    object Analyzing : FeedbackUiState()
    data class Success(val feedback: AnswerFeedback) : FeedbackUiState()
    data class Error(val message: String) : FeedbackUiState()
}
```

## 11. Screen Implementations
- **`AnswerFeedbackScreen`**: Displays the ScoreRing, Strengths, Weaknesses, and Improvements.
- **`SessionSummaryScreen`**: Shows the radar chart of skills, average score, and role readiness.
- **`AnswerReviewScreen`**: Pager interface to swipe through all answers and their respective feedbacks.

## 12. Reusable Components
- `ScoreRing(score, size, color)`
- `FeedbackCard(title, items, icon)`
- `StrengthChip(text)` / `WeaknessChip(text)`
- `InterviewerNoteCard(text)`

## 13. Error Handling Strategy
- **AI Timeout**: Fallback to deterministic scoring and show "Partial Analysis".
- **JSON Parse Error**: Show a safe generic feedback parsing error with a "Retry Analysis" button.
- **Offline**: Cache locally and analyze later.

## 14. Performance Strategy
- Run `AnalyzeAnswerUseCase` asynchronously the moment the user clicks "Submit Answer", even while navigating.
- Cache `AnswerFeedback` in Room/Memory so navigating back is instant.

## 15. Final File Tree (Day 5 Additions)
```text
app/src/main/java/com/aiic/app/
 ├── domain/
 │    ├── model/FeedbackModels.kt
 │    ├── repository/FeedbackRepository.kt
 │    ├── usecase/feedback/AnalyzeAnswerUseCase.kt
 │    └── ai/prompt/FeedbackPromptBuilder.kt
 ├── data/
 │    └── repository/FirestoreFeedbackRepository.kt
 └── presentation/
      └── feature_feedback/
           ├── AnswerFeedbackScreen.kt
           ├── SessionSummaryScreen.kt
           ├── FeedbackViewModel.kt
           └── components/
                ├── ScoreRing.kt
                └── FeedbackCards.kt
```

## 16. Day 6 Roadmap
- **Voice Interviews & Real-time Processing**: Adding Speech-to-Text and Text-to-Speech for live interview simulation.
- **Advanced Analytics Dashboard**: Visualizing growth over time.
- **Export to PDF**: Generate a formal PDF report of the interview.
