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
            HomeScreen()
        }

        composable(AppRoutes.Profile.route) {
            ProfileScreen()
        }

        composable(AppRoutes.Settings.route) {
            SettingsScreen()
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
        ) {
            ResumeDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
