package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.repository.PdfExtractionRepository
import com.google.firebase.storage.FirebaseStorage
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealPdfExtractionRepository @Inject constructor(
    private val storage: FirebaseStorage
) : PdfExtractionRepository {

    override suspend fun extractTextFromPdf(userId: String, resumeId: String): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val storageRef = storage.reference.child("resumes/$userId/$resumeId.pdf")
                
                // Download file into memory (10MB limit is enough for Resumes)
                val bytes = storageRef.getBytes(10 * 1024 * 1024).await()
                
                var extractedText = ""
                PDDocument.load(bytes).use { document ->
                    val stripper = PDFTextStripper()
                    // Extract text from the whole document
                    extractedText = stripper.getText(document)
                }

                if (extractedText.isBlank()) {
                    NetworkResult.Error(code = 400, message = "Could not extract text from PDF. Ensure it is a valid text-based PDF.")
                } else {
                    NetworkResult.Success(extractedText.trim())
                }
            } catch (e: Exception) {
                NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to extract text from PDF.", throwable = e)
            }
        }
    }
}
