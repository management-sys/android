package com.example.attendancemanagementapp.ui.meeting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.param.ProjectStatusQuery
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.data.repository.MeetingRepository
import com.example.attendancemanagementapp.data.repository.ProjectRepository
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddEvent
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddReducer
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddState
import com.example.attendancemanagementapp.ui.meeting.add.ProjectEmployeeSearchState
import com.example.attendancemanagementapp.ui.meeting.add.ProjectSearchState
import com.example.attendancemanagementapp.ui.meeting.detail.MeetingDetailEvent
import com.example.attendancemanagementapp.ui.meeting.detail.MeetingDetailState
import com.example.attendancemanagementapp.ui.meeting.edit.MeetingEditEvent
import com.example.attendancemanagementapp.ui.meeting.edit.MeetingEditReducer
import com.example.attendancemanagementapp.ui.meeting.edit.MeetingEditState
import com.example.attendancemanagementapp.ui.meeting.status.MeetingStatusEvent
import com.example.attendancemanagementapp.ui.meeting.status.MeetingStatusReducer
import com.example.attendancemanagementapp.ui.meeting.status.MeetingStatusState
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState
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
class MeetingViewModel @Inject constructor(private val meetingRepository: MeetingRepository, private val employeeRepository: EmployeeRepository, private val projectRepository: ProjectRepository) : ViewModel() {
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
    private val _meetingStatusState = MutableStateFlow(MeetingStatusState())
    val meetingStatusState = _meetingStatusState.asStateFlow()

    fun onAddEvent(e: MeetingAddEvent) {
        _meetingAddState.update { MeetingAddReducer.reduce(it, e) }

        when (e) {
            MeetingAddEvent.Init -> getProjects()
            MeetingAddEvent.ClickedAdd -> addMeeting()
            MeetingAddEvent.LoadNextEmployeePage -> getPersonnel(MeetingTarget.ADD)
            MeetingAddEvent.ClickedEmployeeSearch -> getPersonnel(MeetingTarget.ADD)
            MeetingAddEvent.ClickedEmployeeInitSearch -> getPersonnel(MeetingTarget.ADD)
            MeetingAddEvent.LoadNextProjectPage -> getProjects()
            MeetingAddEvent.ClickedProjectSearch -> getProjects()
            MeetingAddEvent.ClickedProjectInitSearch -> getProjects()
            MeetingAddEvent.ClickedAddInnerAttendee -> getPersonnel(MeetingTarget.ADD)
            else -> Unit
        }
    }

    fun onDetailEvent(e: MeetingDetailEvent) {
        when (e) {
            MeetingDetailEvent.ClickedUpdate -> _uiEffect.tryEmit(UiEffect.Navigate("meetingEdit"))
            MeetingDetailEvent.ClickedDelete -> deleteMeeting()
            else -> Unit
        }
    }

    fun onEditEvent(e: MeetingEditEvent) {
        _meetingEditState.update { MeetingEditReducer.reduce(it, e) }

        when (e) {
            MeetingEditEvent.ClickedUpdate -> updateMeeting()
            MeetingEditEvent.LoadNextPage -> getPersonnel(MeetingTarget.EDIT)
            MeetingEditEvent.ClickedSearch -> getPersonnel(MeetingTarget.EDIT)
            MeetingEditEvent.ClickedInitSearch -> getPersonnel(MeetingTarget.EDIT)
            MeetingEditEvent.ClickedAddInnerAttendee -> getPersonnel(MeetingTarget.EDIT)
            else -> Unit
        }
    }

    fun onStatusEvent(e: MeetingStatusEvent) {
        _meetingStatusState.update { MeetingStatusReducer.reduce(it, e) }

        when (e) {
            MeetingStatusEvent.Init -> getMeetings()
            MeetingStatusEvent.LoadNextPage -> getMeetings()
            MeetingStatusEvent.ClickedSearch -> getMeetings()
            MeetingStatusEvent.ClickedInitSearch -> getMeetings()
            is MeetingStatusEvent.ClickedMeeting -> {
                getMeeting(e.id)
                _uiEffect.tryEmit(UiEffect.Navigate("meetingDetail"))
            }
            MeetingStatusEvent.ClickedInitFilter -> getMeetings()
            is MeetingStatusEvent.ClickedUseFilter -> getMeetings()
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

    /* 회의록 목록 조회 및 검색 */
    fun getMeetings() {
        val state = meetingStatusState.value

        _meetingStatusState.update { it.copy(paginationState = it.paginationState.copy(isLoading = true)) }

        viewModelScope.launch {
            meetingRepository.getMeetings(
                startDate = state.startDate,
                endDate = state.endDate,
                searchText = state.searchText,
                type = state.type,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedMeetings = if (isFirstPage) data.content else state.meetings + data.content

                        _meetingStatusState.update { it.copy(
                            meetings = updatedMeetings,
                            paginationState = it.paginationState.copy(
                                currentPage = it.paginationState.currentPage + 1,
                                totalPage = data.totalPages,
                                isLoading = false
                            )
                        ) }

                        Log.d(TAG, "[getMeetings] 회의록 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${state.startDate}~${state.endDate}/${state.type})\n${data.content}")
                    }
                    .onFailure { e ->
                        _meetingStatusState.update { it.copy(paginationState = it.paginationState.copy(isLoading = false)) }

                        ErrorHandler.handle(e, TAG, "getMeetings")
                    }
            }
        }
    }

    /* 프로젝트 목록 조회 */
    fun getProjects() {
        val state = meetingAddState.value.projectState

        val updateState: (ProjectSearchState) -> Unit = { newState ->
            _meetingAddState.update { it.copy(projectState = newState) }
        }

        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = true)))

        viewModelScope.launch {
            projectRepository.getProjectStatus(
                query = ProjectStatusQuery(searchText = state.searchText),
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedProjects = if (isFirstPage) data.projects.content else state.projects + data.projects.content

                        updateState(state.copy(
                            projects = updatedProjects,
                            paginationState = state.paginationState.copy(
                                currentPage = state.paginationState.currentPage + 1,
                                totalPage = data.projects.totalpages,
                                isLoading = false
                            )
                        ))

                        Log.d(TAG, "[getProjects] 프로젝트 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.projects.totalpages}, 검색(${state.searchText})\n${data.projects.content}")
                    }
                    .onFailure { e ->
                        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = false)))

                        ErrorHandler.handle(e, TAG, "getProjects")
                    }
            }
        }
    }

    /* 프로젝트 투입 인력 목록 조회 */
    fun getPersonnel(target: MeetingTarget) {
        val projectId = when (target) {
            MeetingTarget.ADD -> meetingAddState.value.inputData.projectId
            MeetingTarget.EDIT -> meetingEditState.value.projectId
        }

        if (target == MeetingTarget.ADD && projectId == "") {
            return
        }

        val state = when (target) {
            MeetingTarget.ADD -> meetingAddState.value.employeeState
            MeetingTarget.EDIT -> meetingEditState.value.employeeState
        }

        val updateState: (ProjectEmployeeSearchState) -> Unit = { newState ->
            when (target) {
                MeetingTarget.ADD -> _meetingAddState.update { it.copy(employeeState = newState) }
                MeetingTarget.EDIT -> _meetingEditState.update { it.copy(employeeState = newState) }
            }
        }

        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = true)))

        viewModelScope.launch {
            projectRepository.getPersonnel(
                projectId = projectId,
                userName = state.searchText,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedEmployees = if (isFirstPage) data.content else state.employees + data.content

                        updateState(state.copy(
                            employees = updatedEmployees,
                            paginationState = state.paginationState.copy(
                                currentPage = state.paginationState.currentPage + 1,
                                totalPage = data.totalPages,
                                isLoading = false
                            )
                        ))

                        Log.d(TAG, "[getPersonnel] 프로젝트 투입 인력 목록 조회 성공\n${data.content}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getPersonnel")
                    }
            }
        }
    }
}