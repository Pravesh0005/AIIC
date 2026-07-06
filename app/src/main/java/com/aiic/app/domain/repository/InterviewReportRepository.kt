package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.InterviewReport

interface InterviewReportRepository {
    suspend fun saveReport(report: InterviewReport): NetworkResult<Unit>
    suspend fun getReport(sessionId: String): NetworkResult<InterviewReport>
    suspend fun getReportsForUser(userId: String): NetworkResult<List<InterviewReport>>
}
