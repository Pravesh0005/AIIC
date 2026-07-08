package com.aiic.app.presentation.feature_resume.detail

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.core.theme.AIICTheme
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * World-class PDF Preview Engine.
 * Features: Native PdfRenderer, pinch-to-zoom, page-by-page scroll,
 * floating page indicator, shimmer loading state, error retry.
 */
@Composable
fun PdfViewerScreen(
    storageUrl: String,
    fileName: String = "Resume.pdf",
    onNavigateBack: () -> Unit,
    viewModel: PdfViewerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(storageUrl) {
        if (storageUrl.isNotBlank()) {
            viewModel.loadPdf(storageUrl)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top bar
        PdfTopBar(fileName = fileName, onNavigateBack = onNavigateBack, state = state)

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val s = state) {
                is PdfViewerState.Idle, is PdfViewerState.Downloading -> {
                    PdfDownloadingState()
                }
                is PdfViewerState.Rendering -> {
                    PdfRenderingState(currentPage = s.currentPage, totalPages = s.totalPages, progress = s.progress)
                }
                is PdfViewerState.Success -> {
                    PdfPagesViewer(pages = s.pages, totalPages = s.totalPages)
                }
                is PdfViewerState.Error -> {
                    PdfErrorState(message = s.message, onRetry = { viewModel.loadPdf(storageUrl) })
                }
            }
        }
    }
}

@Composable
private fun PdfTopBar(fileName: String, onNavigateBack: () -> Unit, state: PdfViewerState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF16213E))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fileName,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            if (state is PdfViewerState.Success) {
                Text(
                    text = "${state.totalPages} page${if (state.totalPages != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
        // PDF badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE94560))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("PDF", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun PdfDownloadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated PDF icon
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.4f, targetValue = 1f, label = "alpha",
            animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse)
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE94560).copy(alpha = alpha)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("Fetching PDF...", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text("Downloading from secure storage", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
        Spacer(Modifier.height(32.dp))
        LinearProgressIndicator(
            modifier = Modifier.width(200.dp).clip(CircleShape),
            color = Color(0xFFE94560),
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun PdfRenderingState(currentPage: Int, totalPages: Int, progress: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Circular progress
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(100.dp),
                color = Color(0xFFE94560),
                trackColor = Color.White.copy(alpha = 0.1f),
                strokeWidth = 6.dp
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(24.dp))
        Text("Rendering PDF", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text("Page $currentPage of $totalPages", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
    }
}

@Composable
private fun PdfPagesViewer(pages: List<Bitmap>, totalPages: Int) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Derive current visible page
    val currentPage by remember {
        derivedStateOf { listState.firstVisibleItemIndex + 1 }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A2E)),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 12.dp)
        ) {
            itemsIndexed(pages) { index, bitmap ->
                ZoomablePdfPage(
                    bitmap = bitmap,
                    pageNumber = index + 1,
                    totalPages = totalPages
                )
            }
        }

        // Floating page indicator
        AnimatedVisibility(
            visible = totalPages > 1,
            enter = fadeIn() + slideInVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.7f),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Prev page
                    if (currentPage > 1) {
                        IconButton(
                            onClick = { coroutineScope.launch { listState.animateScrollToItem(currentPage - 2) } },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Rounded.ChevronLeft, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                    Text(
                        text = "$currentPage / $totalPages",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Next page
                    if (currentPage < totalPages) {
                        IconButton(
                            onClick = { coroutineScope.launch { listState.animateScrollToItem(currentPage) } },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Rounded.ChevronRight, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ZoomablePdfPage(bitmap: Bitmap, pageNumber: Int, totalPages: Int) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(0.8f, 4f)
        scale = newScale
        // Constrain pan to page bounds
        val maxOffset = if (scale > 1f) 400f * (scale - 1f) else 0f
        offset = Offset(
            x = (offset.x + panChange.x).coerceIn(-maxOffset, maxOffset),
            y = (offset.y + panChange.y).coerceIn(-maxOffset, maxOffset)
        )
    }

    // Double tap to zoom
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "PDF Page $pageNumber",
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .transformable(state = transformableState)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onDoubleClick = {
                        if (scale > 1.2f) {
                            scale = 1f; offset = Offset.Zero
                        } else {
                            scale = 2.5f
                        }
                    },
                    onClick = {}
                ),
            contentScale = ContentScale.FillWidth
        )

        // Page number chip (top-right corner)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$pageNumber",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PdfErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFE94560).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = Color(0xFFE94560), modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("Preview Unavailable", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f), textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560))
        ) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Retry", fontWeight = FontWeight.SemiBold)
        }
    }
}
