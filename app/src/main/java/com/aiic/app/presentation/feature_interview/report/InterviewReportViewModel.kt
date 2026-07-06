package com.aiic.app.presentation.feature_interview.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.InterviewReport
import com.aiic.app.domain.repository.InterviewReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InterviewReportState(
    val isLoading: Boolean = true,
    val report: InterviewReport? = null,
    val error: String? = null
)

@HiltViewModel
class InterviewReportViewModel @Inject constructor(
    private val reportRepository: InterviewReportRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InterviewReportState())
    val state: StateFlow<InterviewReportState> = _state.asStateFlow()

    fun loadReport(sessionId: String) {
        if (_state.value.report?.sessionId == sessionId) return

        viewModelScope.launch {
            _state.value = InterviewReportState(isLoading = true)

            when (val result = reportRepository.getReport(sessionId)) {
                is NetworkResult.Success -> {
                    _state.value = InterviewReportState(
                        isLoading = false,
                        report = result.data
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = InterviewReportState(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}
