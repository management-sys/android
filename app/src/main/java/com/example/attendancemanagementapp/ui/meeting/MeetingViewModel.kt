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
import com.example.attendancemanagementapp.ui.meeting.detail.MeetingDetailState
import com.example.attendancemanagementapp.ui.meeting.edit.MeetingEditEvent
import com.example.attendancemanagementapp.ui.meeting.edit.MeetingEditReducer
import com.example.attendancemanagementapp.ui.meeting.edit.MeetingEditState
import com.example.attendancemanagementapp.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MeetingTarget { ADD, EDIT }

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
    private val _meetingEditState = MutableStateFlow(MeetingEditState())
    val meetingEditState = _meetingEditState.asStateFlow()

    fun onAddEvent(e: MeetingAddEvent) {
        _meetingAddState.update { MeetingAddReducer.reduce(it, e) }

        when (e) {
            is MeetingAddEvent.InitWith -> getEmployees(MeetingTarget.ADD)
            MeetingAddEvent.ClickedAdd -> addMeeting()
            MeetingAddEvent.LoadNextPage -> getEmployees(MeetingTarget.ADD)
            MeetingAddEvent.ClickedSearch -> getEmployees(MeetingTarget.ADD)
            MeetingAddEvent.ClickedInitSearch -> getEmployees(MeetingTarget.ADD)
            else -> Unit
        }
    }

    fun onDetailEvent(e: MeetingDetailEvent) {
        when (e) {
            MeetingDetailEvent.ClickedUpdate -> {
                _meetingEditState.update { it.copy() }
                _uiEffect.tryEmit(UiEffect.Navigate("meetingEdit"))
            }
            MeetingDetailEvent.ClickedDelete -> deleteMeeting()
            else -> Unit
        }
    }

    fun onEditEvent(e: MeetingEditEvent) {
        _meetingEditState.update { MeetingEditReducer.reduce(it, e) }

        when (e) {
            is MeetingEditEvent.InitWith -> getEmployees(MeetingTarget.EDIT)
            MeetingEditEvent.ClickedUpdate -> updateMeeting()
            MeetingEditEvent.LoadNextPage -> getEmployees(MeetingTarget.EDIT)
            MeetingEditEvent.ClickedSearch -> getEmployees(MeetingTarget.EDIT)
            MeetingEditEvent.ClickedInitSearch -> getEmployees(MeetingTarget.EDIT)
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
                        _meetingDetailState.update { it.copy(info = data) }

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
                        _meetingDetailState.update { it.copy(info = data) }

                        Log.d(TAG, "[getMeeting] 회의록 상세 조회 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getMeeting")
                    }
            }
        }
    }

    /* 회의록 수정 */
    fun updateMeeting() {
        val state = meetingEditState.value

        viewModelScope.launch {
            meetingRepository.updateMeeting(
                meetingId = state.meetingId,
                request = state.inputData
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _meetingDetailState.update { it.copy(info = data) }
                        _uiEffect.emit(UiEffect.ShowToast("수정이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)
                        Log.d(TAG, "[updateMeeting] 회의록 수정 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "updateMeeting")
                    }
            }
        }
    }

    /* 회의록 삭제 */
    fun deleteMeeting() {
        viewModelScope.launch {
            meetingRepository.deleteMeeting(meetingId = meetingDetailState.value.info.id).collect { result ->
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
    fun getEmployees(target: MeetingTarget) {
        val state = when (target) {
            MeetingTarget.ADD -> meetingAddState.value.employeeState
            MeetingTarget.EDIT -> meetingEditState.value.employeeState
        }

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
                            when (target) {
                                MeetingTarget.ADD -> {
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
                                MeetingTarget.EDIT -> {
                                    _meetingEditState.update {
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
                            }
                        }
                        else {
                            when (target) {
                                MeetingTarget.ADD -> {
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
                                MeetingTarget.EDIT -> {
                                    _meetingEditState.update {
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