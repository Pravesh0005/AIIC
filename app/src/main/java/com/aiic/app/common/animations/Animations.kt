package com.aiic.app.common.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun rememberFadeInAlpha(
    durationMillis: Int = 500,
    delayMillis: Int = 0,
): Float {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMillis.toLong())
        alpha.animateTo(1f, tween(durationMillis, easing = FastOutSlowInEasing))
    }
    return alpha.value
}

@Composable
fun rememberSlideUpAlpha(
    durationMillis: Int = 500,
    delayMillis: Int = 0,
): Pair<Float, Float> {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(30f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMillis.toLong())
        alpha.animateTo(1f, tween(durationMillis, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMillis.toLong())
        offsetY.animateTo(0f, tween(durationMillis, easing = FastOutSlowInEasing))
    }
    return Pair(alpha.value, offsetY.value)
}

fun Modifier.fadeSlideIn(alpha: Float, offsetY: Float): Modifier =
    this
        .alpha(alpha)
        .graphicsLayer { translationY = offsetY }
