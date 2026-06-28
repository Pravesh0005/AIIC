package com.aiic.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aiic.app.presentation.feature_auth.forgot_password.ForgotPasswordScreen
import com.aiic.app.presentation.feature_auth.login.LoginScreen
import com.aiic.app.presentation.feature_auth.register.RegisterScreen
import com.aiic.app.presentation.feature_home.HomeScreen
import com.aiic.app.presentation.feature_onboarding.OnboardingScreen
import com.aiic.app.presentation.feature_profile.AccountSetupScreen
import com.aiic.app.presentation.feature_profile.ProfileScreen
import com.aiic.app.presentation.feature_settings.SettingsScreen
import com.aiic.app.presentation.feature_splash.SplashScreen
import com.aiic.app.presentation.feature_resume.dashboard.ResumeDashboardScreen
import com.aiic.app.presentation.feature_resume.upload.ResumeUploadScreen
import com.aiic.app.presentation.feature_resume.history.ResumeHistoryScreen
import com.aiic.app.presentation.feature_resume.detail.ResumeDetailScreen
import com.aiic.app.presentation.feature_resume.analysis.ResumeAnalysisScreen
import com.aiic.app.presentation.feature_resume.analysis.ATSScoreScreen
import com.aiic.app.presentation.feature_resume.analysis.SkillBreakdownScreen
import com.aiic.app.presentation.feature_resume.analysis.RecommendationsScreen
import com.aiic.app.presentation.feature_resume.analysis.ResumeInsightsScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

private const val ANIM_DURATION = 400

@Composable
fun AIICNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Splash.route,
        enterTransition = {
            fadeIn(tween(ANIM_DURATION)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(ANIM_DURATION)
            )
        },
        exitTransition = {
            fadeOut(tween(ANIM_DURATION)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(ANIM_DURATION)
            )
        },
        popEnterTransition = {
            fadeIn(tween(ANIM_DURATION)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(ANIM_DURATION)
            )
        },
        popExitTransition = {
            fadeOut(tween(ANIM_DURATION)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(ANIM_DURATION)
            )
        }
    ) {
        composable(AppRoutes.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(AppRoutes.Onboarding.route) {
                        popUpTo(AppRoutes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(AppRoutes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(AppRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(AppRoutes.ForgotPassword.route)
                },
                onNavigateToHome = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToAccountSetup = {
                    navController.navigate(AppRoutes.AccountSetup.route) {
                        popUpTo(AppRoutes.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.AccountSetup.route) {
            AccountSetupScreen(
                onNavigateToHome = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.AccountSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.Home.route) {
            HomeScreen(
                onNavigateToResume = { navController.navigate(AppRoutes.ResumeDashboard.route) },
                onNavigateToInterviewSetup = { navController.navigate(AppRoutes.InterviewSetup.route) },
                onNavigateToEditProfile = { navController.navigate(AppRoutes.EditProfile.route) },
                onNavigateToLogin = { 
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(AppRoutes.Home.route) { inclusive = true }
                    }
                },
                onNavigateToAnalytics = { /* Handled in-tab via HomeScreen bottom nav */ },
                onNavigateToSettings = { navController.navigate(AppRoutes.Settings.route) },
                onNavigateToProfile = { /* Handled in-tab via HomeScreen bottom nav */ }
            )
        }

        composable(AppRoutes.Profile.route) {
            ProfileScreen(
                onNavigateToEditProfile = { navController.navigate(AppRoutes.EditProfile.route) },
                onNavigateToSettings = { navController.navigate(AppRoutes.Settings.route) }
            )
        }
        
        composable(AppRoutes.EditProfile.route) {
            com.aiic.app.presentation.feature_profile.EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(AppRoutes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Resume Platform
        composable(AppRoutes.ResumeDashboard.route) {
            ResumeDashboardScreen(
                onNavigateToUpload = { navController.navigate(AppRoutes.ResumeUpload.route) },
                onNavigateToHistory = { navController.navigate(AppRoutes.ResumeHistory.route) },
                onNavigateToDetail = { resumeId -> navController.navigate(AppRoutes.ResumeDetail.createRoute(resumeId)) }
            )
        }

        composable(AppRoutes.ResumeUpload.route) {
            ResumeUploadScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.ResumeHistory.route) {
            ResumeHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { resumeId -> navController.navigate(AppRoutes.ResumeDetail.createRoute(resumeId)) }
            )
        }

        composable(
            route = AppRoutes.ResumeDetail.route,
            arguments = listOf(androidx.navigation.navArgument("resumeId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val resumeId = backStackEntry.arguments?.getString("resumeId")
            ResumeDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAnalysis = { id -> navController.navigate(AppRoutes.ResumeAnalysis.createRoute(id)) }
            )
        }

        composable(
            route = AppRoutes.ResumeAnalysis.route,
            arguments = listOf(androidx.navigation.navArgument("resumeId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val resumeId = backStackEntry.arguments?.getString("resumeId")
            com.aiic.app.presentation.feature_resume.analysis.ResumeAnalysisScreen(
                resumeId = resumeId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToATS = { id -> navController.navigate(AppRoutes.ATSScore.createRoute(id)) },
                onNavigateToSkills = { id -> navController.navigate(AppRoutes.SkillBreakdown.createRoute(id)) },
                onNavigateToRecommendations = { id -> navController.navigate(AppRoutes.Recommendations.createRoute(id)) }
            )
        }
        
        composable(
            route = AppRoutes.ATSScore.route,
            arguments = listOf(androidx.navigation.navArgument("resumeId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val resumeId = backStackEntry.arguments?.getString("resumeId")
            com.aiic.app.presentation.feature_resume.analysis.ATSScoreScreen(
                resumeId = resumeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppRoutes.SkillBreakdown.route,
            arguments = listOf(androidx.navigation.navArgument("resumeId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val resumeId = backStackEntry.arguments?.getString("resumeId")
            com.aiic.app.presentation.feature_resume.analysis.SkillBreakdownScreen(
                resumeId = resumeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppRoutes.Recommendations.route,
            arguments = listOf(androidx.navigation.navArgument("resumeId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val resumeId = backStackEntry.arguments?.getString("resumeId")
            com.aiic.app.presentation.feature_resume.analysis.RecommendationsScreen(
                resumeId = resumeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.ResumeInsights.route) {
            ResumeInsightsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAnalysis = { resumeId -> navController.navigate(AppRoutes.ResumeAnalysis.createRoute(resumeId)) }
            )
        }

        // --- Day 4: AI Mock Interview Engine ---
        composable(AppRoutes.InterviewSetup.route) {
            com.aiic.app.presentation.feature_interview.setup.InterviewSetupScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSession = { sessionId -> 
                    navController.navigate(AppRoutes.InterviewSession.createRoute(sessionId)) {
                        popUpTo(AppRoutes.InterviewSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = AppRoutes.InterviewSession.route,
            arguments = listOf(androidx.navigation.navArgument("sessionId") { type = androidx.navigation.NavType.StringType })
        ) { 
            com.aiic.app.presentation.feature_interview.session.InterviewSessionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSummary = { route -> navController.navigate(route) { popUpTo(AppRoutes.InterviewSetup.route) { inclusive = true } } }
            )
        }

        composable(
            route = AppRoutes.InterviewSummary.route,
            arguments = listOf(androidx.navigation.navArgument("sessionId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            com.aiic.app.presentation.feature_feedback.SessionSummaryScreen(
                sessionId = sessionId,
                onNavigateHome = { 
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Home.route) { inclusive = true }
                    } 
                }
            )
        }

        composable(
            route = AppRoutes.AnswerFeedback.route,
            arguments = listOf(androidx.navigation.navArgument("answerId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val answerId = backStackEntry.arguments?.getString("answerId") ?: ""
            com.aiic.app.presentation.feature_feedback.AnswerFeedbackScreen(
                answerId = answerId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

    }
}
