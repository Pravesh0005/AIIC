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

    companion object {
        const val AUTH_GRAPH = "auth_graph"
        const val MAIN_GRAPH = "main_graph"
    }
}
