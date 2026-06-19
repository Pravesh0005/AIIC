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
import com.aiic.app.presentation.feature_splash.SplashScreen

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
                onNavigateToHome = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.Home.route) {
            HomeScreen()
        }
    }
}
