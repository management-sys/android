package com.example.attendancemanagementapp.ui.attendance.vacation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.datastore.TokenDataStore
import com.example.attendancemanagementapp.data.repository.CommonCodeRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.data.repository.VacationRepository
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.attendance.vacation.add.VacationAddEvent
import com.example.attendancemanagementapp.ui.attendance.vacation.add.VacationAddReducer
import com.example.attendancemanagementapp.ui.attendance.vacation.add.VacationAddState
import com.example.attendancemanagementapp.ui.attendance.vacation.detail.VacationDetailEvent
import com.example.attendancemanagementapp.ui.attendance.vacation.detail.VacationDetailState
import com.example.attendancemanagementapp.ui.attendance.vacation.edit.VacationEditEvent
import com.example.attendancemanagementapp.ui.attendance.vacation.edit.VacationEditReducer
import com.example.attendancemanagementapp.ui.attendance.vacation.edit.VacationEditState
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

enum class VacationTarget { ADD, EDIT }

@HiltViewModel
class VacationViewModel @Inject constructor(private val vacationRepository: VacationRepository, private val employeeRepository: EmployeeRepository, private val commonCodeRepository: CommonCodeRepository, private val tokenDataStore: TokenDataStore) : ViewModel() {
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
    private val _vacationEditState = MutableStateFlow(VacationEditState())
    val vacationEditState = _vacationEditState.asStateFlow()
    private val _vacationStatusState = MutableStateFlow(VacationStatusState())
    val vacationStatusState = _vacationStatusState.asStateFlow()


    fun onAddEvent(e: VacationAddEvent) {
        _vacationAddState.update { VacationAddReducer.reduce(it, e) }

        when (e) {
            is VacationAddEvent.InitWith -> {
                getEmployees(VacationTarget.ADD)
                getVacationType(VacationTarget.ADD)
            }
            VacationAddEvent.ClickedSearch -> getEmployees(VacationTarget.ADD)
            VacationAddEvent.ClickedSearchInit -> getEmployees(VacationTarget.ADD)
            VacationAddEvent.LoadNextPage -> getEmployees(VacationTarget.ADD)
            VacationAddEvent.ClickedAdd -> addVacation()
            VacationAddEvent.ClickedGetPrevApprover -> getPrevApprovers(VacationTarget.ADD)
            else -> Unit
        }
    }

    fun onDetailEvent(e: VacationDetailEvent) {
        when (e) {
            VacationDetailEvent.ClickedCancel -> cancelVacation()
            VacationDetailEvent.ClickedDelete -> deleteVacation()
            VacationDetailEvent.ClickedDownload -> downloadVacationPdf()
            else -> Unit
        }
    }

    fun onEditEvent(e: VacationEditEvent) {
        _vacationEditState.update { VacationEditReducer.reduce(it, e) }

        when (e) {
            is VacationEditEvent.InitWith -> {
                getEmployees(VacationTarget.EDIT)
                getVacationType(VacationTarget.EDIT)
            }
            VacationEditEvent.ClickedSearch -> getEmployees(VacationTarget.EDIT)
            VacationEditEvent.ClickedSearchInit -> getEmployees(VacationTarget.EDIT)
            VacationEditEvent.LoadNextPage -> getEmployees(VacationTarget.EDIT)
            VacationEditEvent.ClickedGetPrevApprover -> getPrevApprovers(VacationTarget.EDIT)
            VacationEditEvent.ClickedUpdate -> updateVacation()
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
            VacationStatusEvent.LoadNextPage -> getVacations()
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

    /* 휴가 정보 수정 */
    fun updateVacation() {
        // 결재(승인/반려) 이전에만 수정 가능
        if (vacationDetailState.value.vacationInfo.status == "승인" || vacationDetailState.value.vacationInfo.status == "반려") {
            return
        }

        viewModelScope.launch {
            vacationRepository.updateVacation(
                vacationId = vacationEditState.value.vacationId,
                request = vacationEditState.value.inputData
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _vacationDetailState.update { it.copy(vacationInfo = data) }

                        _uiEffect.emit(UiEffect.ShowToast("수정이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[updateVacation] 휴가 정보 수정 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "updateVacation")
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

        // 휴가 신청 취소는 신청 기간 당일까지만 가능
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val isToday = LocalDateTime.parse(vacationDetailState.value.vacationInfo.appliedDate, formatter).toLocalDate() == LocalDate.now()

        if (!isToday) {
            _uiEffect.tryEmit(UiEffect.ShowToast("휴가 신청 당일에만 취소할 수 있습니다"))
            return
        }

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
    fun getPrevApprovers(target: VacationTarget) {
        viewModelScope.launch {
            vacationRepository.getPrevApprovers(
                userId = userId
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val newApproverIds = data.approvers.map { it.userId }

                        when(target) {
                            VacationTarget.ADD -> _vacationAddState.update { it.copy(it.inputData.copy(approverIds = newApproverIds)) }
                            VacationTarget.EDIT -> _vacationEditState.update { it.copy(it.inputData.copy(approverIds = newApproverIds)) }
                        }

                        _uiEffect.emit(UiEffect.ShowToast("이전 승인자를 불러왔습니다"))

                        Log.d(TAG, "[getPrevApprovers-${target}] 이전 승인자 불러오기 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getPrevApprovers-${target}")
                    }
            }
        }
    }

    /* 휴가 현황 목록 조회 */
    fun getVacations(isInit: Boolean = false) {
        val state = vacationStatusState.value

        _vacationStatusState.update { it.copy(paginationState = it.paginationState.copy(isLoading = true)) }

        viewModelScope.launch {
            vacationRepository.getVacations(
                userId = userId,
                query = vacationStatusState.value.query,
                page = vacationStatusState.value.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedVacations = if (isFirstPage) data.vacations else state.vacationStatusInfo.vacations + data.vacations
                        val updatedVacationStatus = data.copy(vacations = updatedVacations)

                        if (isInit) {
                            _vacationStatusState.update { it.copy(
                                vacationStatusInfo = updatedVacationStatus,
                                paginationState = it.paginationState.copy(
                                    currentPage = it.paginationState.currentPage + 1,
                                    totalPage = data.pageInfo.totalPages,
                                    isLoading = false
                                )
                            ) }
                        } else {
                            _vacationStatusState.update { it.copy(
                                vacationStatusInfo = updatedVacationStatus,
                                paginationState = it.paginationState.copy(
                                    currentPage = it.paginationState.currentPage + 1,
                                    totalPage = data.pageInfo.totalPages,
                                    isLoading = false
                                )
                            ) }
                        }

                        Log.d(TAG, "[getVacations] 휴가 현황 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.pageInfo.totalPages}, 검색(${state.query.year}년차, ${state.query.filter.name})\n${data}")
                    }
                    .onFailure { e ->
                        _vacationStatusState.update { it.copy(paginationState = it.paginationState.copy(isLoading = false)) }

                        ErrorHandler.handle(e, TAG, "getVacations")
                    }
            }
        }
    }

    /* 휴가 신청서 다운로드(PDF) */
    fun downloadVacationPdf() {
        viewModelScope.launch {
            vacationRepository.downloadVacationPdf(
                vacationId = vacationDetailState.value.vacationInfo.id
            ).collect { result ->
                result
                    .onSuccess { uri ->
                        _uiEffect.emit(UiEffect.ShowToast("다운로드가 완료되었습니다"))

                        Log.d(TAG, "[downloadVacationPdf] 휴가 신청서 다운로드 성공: ${uri}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "downloadVacationPdf")
                    }
            }
        }
    }

    /* 직원 목록 조회 */
    fun getEmployees(target: VacationTarget) {
        val state = when (target) {
            VacationTarget.ADD -> vacationAddState.value.employeeState
            VacationTarget.EDIT -> vacationEditState.value.employeeState
        }

        val updateState: (EmployeeSearchState) -> Unit = { newState ->
            when (target) {
                VacationTarget.ADD -> _vacationAddState.update { it.copy(employeeState = newState) }
                VacationTarget.EDIT -> _vacationEditState.update { it.copy(employeeState = newState) }
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

                        Log.d(TAG, "[getEmployees-${target}] 직원 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${vacationAddState.value.employeeState.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = false)))

                        ErrorHandler.handle(e, TAG, "getEmployees-${target}")
                    }
            }
        }
    }

    /* 공통코드 목록에서 휴가 종류 조회 */
    fun getVacationType(target: VacationTarget) {
        viewModelScope.launch {
            commonCodeRepository.getCommonCodes(
                searchType = SearchType.UPPER_CODE_NM,
                searchKeyword = "휴가",
                page = 0    // 개수가 더 늘어나면 페이지 관리도 해야함 지금은 필요 없음
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val vacationTypeNames: List<String> = data.content.map { it.codeName }

                        when (target) {
                            VacationTarget.ADD -> _vacationAddState.update {
                                it.copy(
                                    vacationTypeOptions = vacationTypeNames
                                )
                            }

                            VacationTarget.EDIT -> {
                                _vacationEditState.update { it.copy(vacationTypeOptions = vacationTypeNames) }
                            }
                        }

                        Log.d(
                            TAG,
                            "[getVacationType-${target}] 휴가 종류 목록 조회 성공\n${vacationTypeNames}"
                        )
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getVacationType-${target}")
                    }
            }
        }
    }

}