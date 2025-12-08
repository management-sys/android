package com.example.attendancemanagementapp.ui.meeting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.data.repository.MeetingRepository
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddEvent
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddReducer
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddState
import com.example.attendancemanagementapp.ui.meeting.detail.MeetingDetailEvent
import com.example.attendancemanagementapp.ui.meeting.detail.MeetingDetailReducer
import com.example.attendancemanagementapp.ui.meeting.detail.MeetingDetailState
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
class MeetingViewModel @Inject constructor(private val meetingRepository: MeetingRepository, private val employeeRepository: EmployeeRepository) : ViewModel() {
    companion object {
        private const val TAG = "MeetingViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _meetingAddState = MutableStateFlow(MeetingAddState())
    val meetingAddState = _meetingAddState.asStateFlow()
    private val _meetingDetailState = MutableStateFlow(MeetingDetailState())
    val meetingDetailState = _meetingDetailState.asStateFlow()

    fun onAddEvent(e: MeetingAddEvent) {
        _meetingAddState.update { MeetingAddReducer.reduce(it, e) }

        when (e) {
            is MeetingAddEvent.InitWith -> getEmployees()
            MeetingAddEvent.ClickedAdd -> addMeeting()
            MeetingAddEvent.LoadNextPage -> getEmployees()
            MeetingAddEvent.ClickedSearch -> getEmployees()
            else -> Unit
        }
    }

    fun onDetailEvent(e: MeetingDetailEvent) {
        _meetingDetailState.update { MeetingDetailReducer.reduce(it, e) }

        when (e) {
            MeetingDetailEvent.ClickedDelete -> deleteMeeting()
            else -> Unit
        }
    }

    /* 회의록 등록 */
    fun addMeeting() {
        val inputData = meetingAddState.value.inputData

        Log.d(TAG, inputData.toString())
        val request = inputData.copy(
            expenses = inputData.expenses.filter { it.type != "" && it.amount != 0 }
        )

        viewModelScope.launch {
            meetingRepository.addMeeting(request = request).collect { result ->
                result
                    .onSuccess { data ->
                        _meetingDetailState.update { it.copy(meetingInfo = data) }

                        _uiEffect.emit(UiEffect.ShowToast("등록이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[addMeeting] 회의록 등록 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "addMeeting")
                    }
            }
        }
    }

    /* 회의록 상세 조회 */
    fun getMeeting(meetingId: Long) {
        viewModelScope.launch {
            meetingRepository.getMeeting(meetingId).collect { result ->
                result
                    .onSuccess { data ->
                        _meetingDetailState.update { it.copy(meetingInfo = data) }

                        Log.d(TAG, "[getMeeting] 회의록 상세 조회 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getMeeting")
                    }
            }
        }
    }

    /* 회의록 삭제 */
    fun deleteMeeting() {
        viewModelScope.launch {
            meetingRepository.deleteMeeting(meetingId = meetingDetailState.value.meetingInfo.id).collect { result ->
                result
                    .onSuccess {
                        _uiEffect.emit(UiEffect.NavigateBack)
                        _uiEffect.emit(UiEffect.ShowToast("회의록이 성공적으로 삭제되었습니다"))

                        Log.d(TAG, "[deleteMeeting] 회의록 삭제 성공")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "deleteMeeting")
                    }
            }
        }
    }

    /* 직원 목록 조회 */
    fun getEmployees() {
        val state = meetingAddState.value.employeeState

        viewModelScope.launch {
            _meetingAddState.update { it.copy(employeeState = it.employeeState.copy(paginationState = it.employeeState.paginationState.copy(isLoading = true))) }

            employeeRepository.getManageEmployees(
                department = "",
                grade = "",
                title = "",
                name = state.searchText,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        if (state.paginationState.currentPage == 0) {
                            _meetingAddState.update {
                                it.copy(
                                    employeeState = it.employeeState.copy(
                                        employees = data.content,
                                        paginationState = it.employeeState.paginationState.copy(
                                            currentPage = it.employeeState.paginationState.currentPage + 1,
                                            totalPage = data.totalPages,
                                            isLoading = false
                                        )
                                    )
                                )
                            }
                        }
                        else {
                            _meetingAddState.update {
                                it.copy(
                                    employeeState = it.employeeState.copy(
                                        employees = it.employeeState.employees + data.content,
                                        paginationState = it.employeeState.paginationState.copy(
                                            currentPage = it.employeeState.paginationState.currentPage + 1,
                                            isLoading = false
                                        )
                                    )
                                )
                            }
                        }

                        Log.d(TAG, "[getEmployees] 직원 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${meetingAddState.value.employeeState.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getEmployees")
                    }
            }
        }
    }
}