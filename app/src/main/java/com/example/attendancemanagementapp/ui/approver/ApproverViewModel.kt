package com.example.attendancemanagementapp.ui.approver

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.repository.ApproverRepository
import com.example.attendancemanagementapp.ui.approver.request.ApproverRequestEvent
import com.example.attendancemanagementapp.ui.approver.request.ApproverRequestReducer
import com.example.attendancemanagementapp.ui.approver.request.ApproverRequestState
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.project.status.ProjectStatusEvent
import com.example.attendancemanagementapp.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApproverViewModel @Inject constructor(private val approverRepository: ApproverRepository) : ViewModel() {
    companion object {
        private const val TAG = "ApproverViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _approveRequestState = MutableStateFlow(ApproverRequestState())
    val approveRequestState = _approveRequestState.asStateFlow()

    fun onRequestEvent(e: ApproverRequestEvent) {
        _approveRequestState.update { ApproverRequestReducer.reduce(it, e) }

        when (e) {
            ApproverRequestEvent.InitFirst -> getApprovers()
            ApproverRequestEvent.InitLast -> getApprovers()
            ApproverRequestEvent.ClickedInitFilter -> getApprovers()
            is ApproverRequestEvent.ClickedUseFilter -> getApprovers()
            ApproverRequestEvent.LoadNextPage -> getApprovers()
            ApproverRequestEvent.ClickedAddTrip -> _uiEffect.tryEmit(UiEffect.Navigate("tripAdd"))
            ApproverRequestEvent.ClickedAddVacation -> _uiEffect.tryEmit(UiEffect.Navigate("vacationAdd"))
            else -> Unit
        }
    }

    /* 결재 요청 목록 조회 */
    fun getApprovers() {
        val state = approveRequestState.value

        viewModelScope.launch {
            approverRepository.getApprovers(
                query = state.filter,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _approveRequestState.update { it.copy(approverRequest = data) }

                        Log.d(TAG, "[getApprovers] 결재 요청 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${state.filter.year}년/${state.filter.month}월/${state.filter.approvalType.label}/${state.filter.applicationType.label})\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getApprovers")
                    }
            }
        }
    }
}