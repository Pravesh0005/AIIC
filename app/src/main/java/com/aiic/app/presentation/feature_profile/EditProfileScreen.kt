package com.aiic.app.presentation.feature_profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.AIICTextField
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.base.UiEvent
import com.aiic.app.core.theme.AIICTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var visibleItems by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(4) {
            delay(120)
            visibleItems++
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigateBack()
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", style = AIICTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(EditProfileAction.Cancel) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AIICTheme.colors.background,
                    titleContentColor = AIICTheme.colors.textPrimary,
                    navigationIconContentColor = AIICTheme.colors.textPrimary
                )
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    androidx.compose.material3.Snackbar(
                        snackbarData = data,
                        containerColor = AIICTheme.colors.surfaceElevated,
                        contentColor = AIICTheme.colors.textPrimary,
                        actionColor = AIICTheme.colors.primary,
                        shape = AIICTheme.shapes.medium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        },
        containerColor = AIICTheme.colors.background
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AIICTheme.colors.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AIICTheme.spacing.screenHorizontal)
                    .padding(top = 16.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedVisibility(visibleItems > 0, enter = fadeIn() + slideInVertically { 30 }) {
                    PremiumCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Career Goals",
                                style = AIICTheme.typography.titleMedium,
                                color = AIICTheme.colors.textPrimary,
                                fontWeight = FontWeight.SemiBold,
                            )

                            AIICTextField(
                                value = state.targetRole,
                                onValueChange = { viewModel.onAction(EditProfileAction.UpdateRole(it)) },
                                label = "Target Role",
                                placeholder = "e.g. Software Engineer, Product Manager",
                                leadingIcon = Icons.Rounded.Work,
                            )

                            AIICTextField(
                                value = state.targetCompany,
                                onValueChange = { viewModel.onAction(EditProfileAction.UpdateCompany(it)) },
                                label = "Dream Company",
                                placeholder = "e.g. Google, Amazon, Meta",
                                leadingIcon = Icons.Rounded.Business,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                AnimatedVisibility(visibleItems > 1, enter = fadeIn() + slideInVertically { 30 }) {
                    PremiumCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Background",
                                style = AIICTheme.typography.titleMedium,
                                color = AIICTheme.colors.textPrimary,
                                fontWeight = FontWeight.SemiBold,
                            )

                            AIICTextField(
                                value = state.education,
                                onValueChange = { viewModel.onAction(EditProfileAction.UpdateEducation(it)) },
                                label = "Education",
                                placeholder = "e.g. B.Tech CS, MBA, Self-taught",
                                leadingIcon = Icons.Rounded.School,
                            )

                            AIICTextField(
                                value = state.skills,
                                onValueChange = { viewModel.onAction(EditProfileAction.UpdateSkills(it)) },
                                label = "Key Skills",
                                placeholder = "Kotlin, System Design, DSA",
                                leadingIcon = Icons.Rounded.Stars,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                AnimatedVisibility(visibleItems > 2, enter = fadeIn() + slideInVertically { 30 }) {
                    PremiumButton(
                        text = if (state.isSaving) "Saving..." else "Save Changes",
                        onClick = { viewModel.onAction(EditProfileAction.SaveProfile) },
                        enabled = !state.isSaving,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
