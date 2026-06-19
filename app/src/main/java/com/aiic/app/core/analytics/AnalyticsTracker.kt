package com.aiic.app.core.analytics

/**
 * Analytics abstraction layer.
 * Implement with Firebase Analytics, Mixpanel, or custom backend.
 * All screens should log through this interface — not directly to any SDK.
 */
interface AnalyticsTracker {
    fun logEvent(event: AnalyticsEvent)
    fun setUserId(userId: String)
    fun setUserProperty(key: String, value: String)
    fun logScreen(screenName: String, screenClass: String? = null)
}

data class AnalyticsEvent(
    val name: String,
    val params: Map<String, Any> = emptyMap(),
) {
    companion object {
        // Auth events
        fun loginSuccess(method: String) = AnalyticsEvent("login", mapOf("method" to method))
        fun loginFailed(error: String) = AnalyticsEvent("login_failed", mapOf("error" to error))
        fun registerSuccess() = AnalyticsEvent("sign_up")
        fun logout() = AnalyticsEvent("logout")

        // Interview events
        fun interviewStarted(type: String) = AnalyticsEvent("interview_started", mapOf("type" to type))
        fun interviewCompleted(score: Float) = AnalyticsEvent("interview_completed", mapOf("score" to score))

        // Subscription events
        fun paywallViewed(source: String) = AnalyticsEvent("paywall_viewed", mapOf("source" to source))
        fun subscriptionStarted(plan: String) = AnalyticsEvent("subscription_started", mapOf("plan" to plan))
    }
}

/**
 * No-op implementation for development / when analytics is disabled.
 */
class NoOpAnalyticsTracker : AnalyticsTracker {
    override fun logEvent(event: AnalyticsEvent) {}
    override fun setUserId(userId: String) {}
    override fun setUserProperty(key: String, value: String) {}
    override fun logScreen(screenName: String, screenClass: String?) {}
}
