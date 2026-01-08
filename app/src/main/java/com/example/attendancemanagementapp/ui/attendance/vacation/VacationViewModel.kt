package com.example.attendancemanagementapp.ui.attendance.vacation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.datastore.TokenDataStore
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.data.repository.VacationRepository
import com.example.attendancemanagementapp.ui.attendance.vacation.add.VacationAddEvent
import com.example.attendancemanagementapp.ui.attendance.vacation.add.VacationAddReducer
import com.example.attendancemanagementapp.ui.attendance.vacation.add.VacationAddState
import com.example.attendancemanagementapp.ui.attendance.vacation.detail.VacationDetailEvent
import com.example.attendancemanagementapp.ui.attendance.vacation.detail.VacationDetailReducer
import com.example.attendancemanagementapp.ui.attendance.vacation.detail.VacationDetailState
import com.example.attendancemanagementapp.ui.attendance.vacation.status.VacationStatusEvent
import com.example.attendancemanagementapp.ui.attendance.vacation.status.VacationStatusReducer
import com.example.attendancemanagementapp.ui.attendance.vacation.status.VacationStatusState
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState
import com.example.attendancemanagementapp.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class VacationViewModel @Inject constructor(private val vacationRepository: VacationRepository, private val employeeRepository: EmployeeRepository, private val tokenDataStore: TokenDataStore) : ViewModel() {
    companion object {
        private const val TAG = "VacationViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    val userId = runBlocking {
        tokenDataStore.userIdFlow.first()
    }

    private val _vacationAddState = MutableStateFlow(VacationAddState())
    val vacationAddState = _vacationAddState.asStateFlow()
    private val _vacationDetailState = MutableStateFlow(VacationDetailState())
    val vacationDetailState = _vacationDetailState.asStateFlow()
    private val _vacationStatusState = MutableStateFlow(VacationStatusState())
    val vacationStatusState = _vacationStatusState.asStateFlow()

    fun onAddEvent(e: VacationAddEvent) {
        _vacationAddState.update { VacationAddReducer.reduce(it, e) }

        when (e) {
            is VacationAddEvent.InitWith -> getEmployees()
            VacationAddEvent.ClickedSearch -> getEmployees()
            VacationAddEvent.ClickedSearchInit -> getEmployees()
            VacationAddEvent.LoadNextPage -> getEmployees()
            VacationAddEvent.ClickedAdd -> addVacation()
            VacationAddEvent.ClickedGetPrevApprover -> getPrevApprovers()
            else -> Unit
        }
    }

    fun onDetailEvent(e: VacationDetailEvent) {
        _vacationDetailState.update { VacationDetailReducer.reduce(it, e) }

        when (e) {
            VacationDetailEvent.ClickedCancel -> cancelVacation()
            VacationDetailEvent.ClickedDelete -> deleteVacation()
            else -> Unit
        }
    }

    fun onStatusEvent(e: VacationStatusEvent) {
        _vacationStatusState.update { VacationStatusReducer.reduce(it, e) }

        when (e) {
            VacationStatusEvent.Init -> getVacations(isInit = true)
            is VacationStatusEvent.ClickedVacationWith -> {
                getVacation(e.id)
                _uiEffect.tryEmit(UiEffect.Navigate("vacationDetail"))
            }
            is VacationStatusEvent.ClickedVacationTypeWith -> getVacations()
            is VacationStatusEvent.SelectedYearWith -> getVacations()
            else -> Unit
        }
    }

    /* 휴가 신청 */
    fun addVacation() {
        viewModelScope.launch {
            vacationRepository.addVacation(
                request = vacationAddState.value.inputData
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _uiEffect.emit(UiEffect.ShowToast("휴가 신청이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[addVacation] 휴가 신청 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "addVacation")
                    }
            }
        }
    }

    /* 휴가 신청 상세 조회 */
    fun getVacation(vacationId: String) {
        viewModelScope.launch {
            vacationRepository.getVacation(
                vacationId = vacationId
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _vacationDetailState.update { it.copy(vacationInfo = data) }

                        Log.d(TAG, "[getVacation] 휴가 신청 상세 조회 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getVacation")
                    }
            }
        }
    }

    /* 휴가 신청 삭제 */
    fun deleteVacation() {
        val vacationId = vacationDetailState.value.vacationInfo.id

        viewModelScope.launch {
            vacationRepository.deleteVacation(
                vacationId = vacationId
            ).collect { result ->
                result
                    .onSuccess {
                        _uiEffect.emit(UiEffect.NavigateBack)
                        _uiEffect.emit(UiEffect.ShowToast("휴가 신청이 삭제되었습니다"))

                        Log.d(TAG, "[deleteVacation] 휴가 신청 삭제 성공")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "deleteVacation")
                    }
            }
        }
    }

    /* 휴가 신청 취소 */
    fun cancelVacation() {
        val vacationId = vacationDetailState.value.vacationInfo.id

        viewModelScope.launch {
            vacationRepository.cancelVacation(
                vacationId = vacationId
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _vacationDetailState.update { it.copy(vacationInfo = data) }

                        _uiEffect.emit(UiEffect.ShowToast("휴가 신청이 취소되었습니다"))

                        Log.d(TAG, "[cancelVacation] 휴가 신청 취소 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "cancelVacation")
                    }
            }
        }
    }

    /* 이전 승인자 불러오기 */
    fun getPrevApprovers() {
        viewModelScope.launch {
            vacationRepository.getPrevApprovers(
                userId = userId
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val newApproverIds = data.approvers.map { it.userId }
                        _vacationAddState.update { it.copy(it.inputData.copy(approverIds = newApproverIds)) }

                        _uiEffect.emit(UiEffect.ShowToast("이전 승인자를 불러왔습니다"))

                        Log.d(TAG, "[getPrevApprovers] 이전 승인자 불러오기 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getPrevApprovers")
                    }
            }
        }
    }

    /* 휴가 현황 목록 조회 */
    fun getVacations(isInit: Boolean = false) {
        viewModelScope.launch {
            vacationRepository.getVacations(
                userId = userId,
                query = vacationStatusState.value.query,
                page = vacationStatusState.value.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        if (isInit) {
                            _vacationStatusState.update { it.copy(vacationStatusInfo = data, query = it.query.copy(year = data.years.size - 1)) }
                        } else {
                            _vacationStatusState.update { it.copy(vacationStatusInfo = data) }
                        }

                        Log.d(TAG, "[getVacations] 휴가 현황 목록 조회 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getVacations")
                    }
            }
        }
    }

    /* 직원 목록 조회 */
    fun getEmployees() {
        val state = vacationAddState.value.employeeState
        val updateState: (EmployeeSearchState) -> Unit = { newState ->
            _vacationAddState.update { it.copy(employeeState = newState) }
        }

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

                        Log.d(TAG, "[getEmployees] 직원 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${vacationAddState.value.employeeState.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = false)))

                        ErrorHandler.handle(e, TAG, "getEmployees")
                    }
            }
        }
    }
}