package com.aiic.app.presentation.feature_resume.detail

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import android.content.Context

sealed interface PdfViewerState {
    data object Idle : PdfViewerState
    data object Downloading : PdfViewerState
    data class Rendering(val currentPage: Int, val totalPages: Int, val progress: Float) : PdfViewerState
    data class Success(val pages: List<Bitmap>, val totalPages: Int) : PdfViewerState
    data class Error(val message: String) : PdfViewerState
}

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _state = MutableStateFlow<PdfViewerState>(PdfViewerState.Idle)
    val state: StateFlow<PdfViewerState> = _state.asStateFlow()

    private var pdfRenderer: PdfRenderer? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null

    companion object {
        private const val TAG = "AIIC_PDF"
        
        private const val RENDER_WIDTH = 1080
    }

    fun loadPdf(storageUrl: String) {
        if (_state.value is PdfViewerState.Success) return 
        viewModelScope.launch {
            try {
                _state.value = PdfViewerState.Downloading
                Log.d(TAG, "loadPdf: Downloading from $storageUrl")

                val localFile = downloadToCache(storageUrl)
                if (localFile == null) {
                    _state.value = PdfViewerState.Error("Failed to download PDF. Check connection.")
                    return@launch
                }

                renderAllPages(localFile)
            } catch (e: Exception) {
                Log.e(TAG, "loadPdf: Error", e)
                _state.value = PdfViewerState.Error("Could not open PDF: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun downloadToCache(storageUrl: String): File? = withContext(Dispatchers.IO) {
        try {
            val cacheFile = File(context.cacheDir, "resume_preview_${storageUrl.hashCode()}.pdf")
            if (cacheFile.exists() && cacheFile.length() > 0) {
                Log.d(TAG, "downloadToCache: Cache hit at ${cacheFile.path}")
                return@withContext cacheFile
            }
            val storageRef = storage.getReferenceFromUrl(storageUrl)
            storageRef.getFile(cacheFile).await()
            Log.d(TAG, "downloadToCache: Downloaded ${cacheFile.length()} bytes")
            cacheFile
        } catch (e: Exception) {
            Log.e(TAG, "downloadToCache: Failed - ${e.message}")
            null
        }
    }

    private suspend fun renderAllPages(file: File) = withContext(Dispatchers.IO) {
        try {
            closeRenderer()
            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
            val totalPages = pdfRenderer!!.pageCount

            Log.d(TAG, "renderAllPages: Total pages = $totalPages")
            if (totalPages == 0) {
                _state.value = PdfViewerState.Error("PDF has no pages.")
                return@withContext
            }

            val bitmaps = mutableListOf<Bitmap>()
            for (i in 0 until totalPages) {
                _state.value = PdfViewerState.Rendering(i + 1, totalPages, (i + 1f) / totalPages)

                val page = pdfRenderer!!.openPage(i)
                
                val aspectRatio = page.height.toFloat() / page.width.toFloat()
                val renderHeight = (RENDER_WIDTH * aspectRatio).toInt()

                val bitmap = Bitmap.createBitmap(RENDER_WIDTH, renderHeight, Bitmap.Config.ARGB_8888)
                
                bitmap.eraseColor(android.graphics.Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()

                bitmaps.add(bitmap)
                Log.d(TAG, "renderAllPages: Rendered page ${i + 1}/$totalPages")
            }

            _state.value = PdfViewerState.Success(bitmaps.toList(), totalPages)
            Log.d(TAG, "renderAllPages: All pages rendered successfully")
        } catch (e: Exception) {
            Log.e(TAG, "renderAllPages: Error", e)
            _state.value = PdfViewerState.Error("Failed to render PDF: ${e.localizedMessage}")
        }
    }

    private fun closeRenderer() {
        try { pdfRenderer?.close() } catch (_: Exception) {}
        try { parcelFileDescriptor?.close() } catch (_: Exception) {}
        pdfRenderer = null
        parcelFileDescriptor = null
    }

    override fun onCleared() {
        super.onCleared()
        closeRenderer()
    }
}
