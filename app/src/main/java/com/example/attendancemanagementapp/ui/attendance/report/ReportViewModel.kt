package com.example.attendancemanagementapp.ui.attendance.report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.repository.CardRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.data.repository.TripRepository
import com.example.attendancemanagementapp.ui.asset.card.manage.CardManageState
import com.example.attendancemanagementapp.ui.attendance.report.add.ReportAddEvent
import com.example.attendancemanagementapp.ui.attendance.report.add.ReportAddReducer
import com.example.attendancemanagementapp.ui.attendance.report.add.ReportAddState
import com.example.attendancemanagementapp.ui.attendance.report.add.TripExpenseSearchField
import com.example.attendancemanagementapp.ui.attendance.report.detail.ReportDetailState
import com.example.attendancemanagementapp.ui.attendance.report.edit.ReportEditEvent
import com.example.attendancemanagementapp.ui.attendance.report.edit.ReportEditReducer
import com.example.attendancemanagementapp.ui.attendance.report.edit.ReportEditState
import com.example.attendancemanagementapp.ui.base.UiEffect
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

enum class ReportTarget {
    ADD, EDIT
}

@HiltViewModel
class ReportViewModel @Inject constructor(private val tripRepository: TripRepository, private val employeeRepository: EmployeeRepository, private val cardRepository: CardRepository) : ViewModel() {
    companion object {
        private const val TAG = "ReportViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _reportAddState = MutableStateFlow(ReportAddState())
    val reportAddState = _reportAddState.asStateFlow()
    private val _reportDetailState = MutableStateFlow(ReportDetailState())
    val reportDetailState = _reportDetailState.asStateFlow()
    private val _reportEditState = MutableStateFlow(ReportEditState())
    val reportEditState = _reportEditState.asStateFlow()

    fun onAddEvent(e: ReportAddEvent) {
        _reportAddState.update { ReportAddReducer.reduce(it, e) }

        when (e) {
            is ReportAddEvent.InitWith -> {
                getEmployees(ReportTarget.ADD)
                getCards(ReportTarget.ADD)
            }
            is ReportAddEvent.ClickedSearchWith -> {
                when (e.field) {
                    TripExpenseSearchField.APPROVER -> getEmployees(ReportTarget.ADD)
                    TripExpenseSearchField.CARD -> getCards(ReportTarget.ADD)
                    TripExpenseSearchField.PAYER -> getEmployees(ReportTarget.ADD)
                }
            }
            is ReportAddEvent.ClickedSearchInitWith -> {
                when (e.field) {
                    TripExpenseSearchField.APPROVER -> getEmployees(ReportTarget.ADD)
                    TripExpenseSearchField.CARD -> getCards(ReportTarget.ADD)
                    TripExpenseSearchField.PAYER -> getEmployees(ReportTarget.ADD)
                }
            }
            ReportAddEvent.LoadNextPage -> getEmployees(ReportTarget.ADD)
            ReportAddEvent.ClickedAdd -> addTripReport()
            ReportAddEvent.ClickedGetPrevApprover -> getPrevApprover(ReportTarget.ADD)
            else -> Unit
        }
    }

    fun onEditEvent(e: ReportEditEvent) {
        _reportEditState.update { ReportEditReducer.reduce(it, e) }

        when (e) {
            is ReportEditEvent.InitWith -> {
                getEmployees(ReportTarget.EDIT)
                getCards(ReportTarget.EDIT)
            }
            is ReportEditEvent.ClickedSearchWith -> {
                when (e.field) {
                    TripExpenseSearchField.APPROVER -> getEmployees(ReportTarget.EDIT)
                    TripExpenseSearchField.CARD -> getCards(ReportTarget.EDIT)
                    TripExpenseSearchField.PAYER -> getEmployees(ReportTarget.EDIT)
                }
            }
            is ReportEditEvent.ClickedSearchInitWith -> {
                when (e.field) {
                    TripExpenseSearchField.APPROVER -> getEmployees(ReportTarget.EDIT)
                    TripExpenseSearchField.CARD -> getCards(ReportTarget.EDIT)
                    TripExpenseSearchField.PAYER -> getEmployees(ReportTarget.EDIT)
                }
            }
            ReportEditEvent.LoadNextPage -> getEmployees(ReportTarget.EDIT)
            ReportEditEvent.ClickedEdit -> updateTripReport()
            ReportEditEvent.ClickedGetPrevApprover -> getPrevApprover(ReportTarget.EDIT)
            else -> Unit
        }
    }

    /* 출장 복명서 등록 */
    fun addTripReport() {
        viewModelScope.launch {
            tripRepository.addTripReport(
                request = reportAddState.value.inputData
            ).collect { result ->
                result
                    .onSuccess {
                        _uiEffect.emit(UiEffect.ShowToast("출장 복명서 등록이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[addTripReport] 출장 복명서 등록 성공")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "addTripReport")
                    }
            }
        }
    }

    /* 출장 복명서 수정 */
    fun updateTripReport() {
        viewModelScope.launch {
            tripRepository.updateTripReport(
                id = reportEditState.value.tripInfo.id,
                request = reportEditState.value.inputData
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _reportDetailState.update { it.copy(reportInfo = data) }

                        _uiEffect.emit(UiEffect.ShowToast("출장 복명서 수정이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[updateTripReport] 출장 복명서 수정 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "updateTripReport")
                    }
            }
        }
    }

    /* 출장 복명서 조회 */
    fun getTripReport(id: String) {
        viewModelScope.launch {
            tripRepository.getTripReport(
                id = id
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _reportDetailState.update { it.copy(reportInfo = data) }

                        Log.d(TAG, "출장 복명서 조회 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getTripReport")
                    }
            }
        }
    }

    /* 출장 복명서 삭제 */
    fun deleteTripReport() {
        viewModelScope.launch {
            tripRepository.deleteTripReport(
                id = reportDetailState.value.reportInfo.tripId
            ).collect { result ->
                result
                    .onSuccess {
                        _uiEffect.emit(UiEffect.ShowToast("출장 복명서가 삭제되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[deleteTripReport] 출장 복명서 삭제 성공")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "deleteTripReport")
                    }
            }
        }
    }

    /* 출장 복명서 취소 */
    fun cancelTripReport() {
        // 이미 취소 상태인 경우 토스트
        if (reportDetailState.value.reportInfo.status == "C") {
            _uiEffect.tryEmit(UiEffect.ShowToast("이미 취소 상태입니다"))
            return
        }

        viewModelScope.launch {
            tripRepository.cancelTripReport(
                id = reportDetailState.value.reportInfo.tripId
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _reportDetailState.update { it.copy(reportInfo = data) }

                        _uiEffect.emit(UiEffect.ShowToast("출장 복명서가 취소되었습니다"))

                        Log.d(TAG, "[cancelTripReport] 출장 복명서 취소 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "cancelTripReport")
                    }
            }
        }
    }

    /* 이전 승인자 불러오기 */
    fun getPrevApprover(target: ReportTarget) {
        viewModelScope.launch {
            tripRepository.getReportPrevApprovers().collect { result ->
                result
                    .onSuccess { data ->
                        val newApproverIds = data.approvers.map { it.userId }

                        when(target) {
                            ReportTarget.ADD -> _reportAddState.update { it.copy(it.inputData.copy(approverIds = newApproverIds)) }
                            ReportTarget.EDIT -> _reportEditState.update { it.copy(it.inputData.copy(approverIds = newApproverIds)) }
                        }

                        _uiEffect.emit(UiEffect.ShowToast("이전 승인자를 불러왔습니다"))

                        Log.d(TAG, "[getPrevApprovers-${target}] 이전 승인자 불러오기 성공\n${data}")

                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getPrevApprover")
                    }
            }
        }
    }

    /* 카드 목록 조회 및 검색 */
    fun getCards(target: ReportTarget) {
        val state = when (target) {
            ReportTarget.ADD -> reportAddState.value.cardState
            ReportTarget.EDIT -> reportEditState.value.cardState
        }

        val updateState: (CardManageState) -> Unit = { newState ->
            when (target) {
                ReportTarget.ADD -> _reportAddState.update { it.copy(cardState = newState) }
                ReportTarget.EDIT -> _reportEditState.update { it.copy(cardState = newState) }
            }
        }

        viewModelScope.launch {
            cardRepository.getCards(
                keyword = state.searchText,
                type = state.type
            ).collect { result ->
                result
                    .onSuccess { data ->
                        updateState(state.copy(cards = data.cards))

                        Log.d(TAG, "[getCards] 카드 목록 조회 및 검색 성공 (${state.type}-${state.searchText})\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getCards")
                    }
            }
        }
    }

    /* 직원 목록 조회 */
    fun getEmployees(target: ReportTarget) {
        val state = when (target) {
            ReportTarget.ADD -> reportAddState.value.employeeState
            ReportTarget.EDIT -> reportEditState.value.employeeState
        }

        val updateState: (EmployeeSearchState) -> Unit = { newState ->
            when (target) {
                ReportTarget.ADD -> _reportAddState.update { it.copy(employeeState = newState) }
                ReportTarget.EDIT -> _reportEditState.update { it.copy(employeeState = newState) }
            }
        }

        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = true)))

        viewModelScope.launch {
            employeeRepository.getManageEmployees(
                department = "",
                grade = "",
                title = "",
                name = state.searchText,
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

                        Log.d(TAG, "[getEmployees-${target}] 직원 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${reportAddState.value.employeeState.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = false)))

                        ErrorHandler.handle(e, TAG, "getEmployees-${target}")
                    }
            }
        }
    }
}