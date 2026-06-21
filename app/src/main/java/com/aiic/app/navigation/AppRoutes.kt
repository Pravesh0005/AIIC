package com.aiic.app.navigation

sealed class AppRoutes(val route: String) {
    data object Splash : AppRoutes("splash")
    data object Onboarding : AppRoutes("onboarding")
    data object Login : AppRoutes("login")
    data object Register : AppRoutes("register")
    data object ForgotPassword : AppRoutes("forgot_password")
    data object AccountSetup : AppRoutes("account_setup")
    data object Home : AppRoutes("home")
    data object Profile : AppRoutes("profile")
    data object Settings : AppRoutes("settings")

    // Resume Platform
    data object ResumeDashboard : AppRoutes("resume_dashboard")
    data object ResumeUpload : AppRoutes("resume_upload")
    data object ResumeHistory : AppRoutes("resume_history")
    data object ResumeDetail : AppRoutes("resume_detail/{resumeId}") {
        fun createRoute(resumeId: String) = "resume_detail/$resumeId"
    }

    companion object {
        const val AUTH_GRAPH = "auth_graph"
        const val MAIN_GRAPH = "main_graph"
    }
}
