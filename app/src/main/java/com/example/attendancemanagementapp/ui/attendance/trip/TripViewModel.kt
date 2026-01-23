package com.example.attendancemanagementapp.ui.attendance.trip

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.data.repository.CarRepository
import com.example.attendancemanagementapp.data.repository.CardRepository
import com.example.attendancemanagementapp.data.repository.CommonCodeRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.data.repository.TripRepository
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.asset.car.CarViewModel
import com.example.attendancemanagementapp.ui.asset.car.manage.CarManageState
import com.example.attendancemanagementapp.ui.asset.card.CardViewModel
import com.example.attendancemanagementapp.ui.asset.card.manage.CardManageState
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripAddEvent
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripAddReducer
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripAddState
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripSearchField
import com.example.attendancemanagementapp.ui.attendance.vacation.VacationTarget
import com.example.attendancemanagementapp.ui.attendance.vacation.VacationViewModel
import com.example.attendancemanagementapp.ui.attendance.vacation.add.VacationAddEvent
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

enum class TripTarget {
    ADD, EDIT
}

@HiltViewModel
class TripViewModel @Inject constructor(private val tripRepository: TripRepository, private val cardRepository: CardRepository, private val carRepository: CarRepository, private val employeeRepository: EmployeeRepository, private val commonCodeRepository: CommonCodeRepository) : ViewModel() {
    companion object {
        private const val TAG = "TripViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _tripAddState = MutableStateFlow(TripAddState())
    val tripAddState = _tripAddState.asStateFlow()

    fun onAddEvent(e: TripAddEvent) {
        _tripAddState.update { TripAddReducer.reduce(it, e) }

        when (e) {
            TripAddEvent.Init -> {
                getTripType(TripTarget.ADD)
                getEmployees(TripTarget.ADD)
                getCards(TripTarget.ADD)
                getCars(TripTarget.ADD)
            }
            is TripAddEvent.ClickedSearch -> {
                when (e.field) {
                    TripSearchField.APPROVER -> getEmployees(TripTarget.ADD)
                    TripSearchField.ATTENDEE -> getEmployees(TripTarget.ADD)
                    TripSearchField.CARD -> getCards(TripTarget.ADD)
                    TripSearchField.CAR -> getCars(TripTarget.ADD)
                    TripSearchField.DRIVER -> getEmployees(TripTarget.ADD)
                }
            }
            is TripAddEvent.ClickedSearchInit -> {
                when (e.field) {
                    TripSearchField.APPROVER -> getEmployees(TripTarget.ADD)
                    TripSearchField.ATTENDEE -> getEmployees(TripTarget.ADD)
                    TripSearchField.CARD -> getCards(TripTarget.ADD)
                    TripSearchField.CAR -> getCars(TripTarget.ADD)
                    TripSearchField.DRIVER -> getEmployees(TripTarget.ADD)
                }
            }
            TripAddEvent.LoadNextPage -> getEmployees(TripTarget.ADD)
            TripAddEvent.ClickedAdd -> addTrip()
            TripAddEvent.ClickedGetPrevApprover -> getPrevApprovers(TripTarget.ADD)
            else -> Unit
        }
    }

    /* 출장 신청 */
    fun addTrip() {
        val state = tripAddState.value.inputData
        val request = state.copy(
            startDate = state.startDate.take(16),
            endDate = state.endDate.take(16),
            cardUsages = state.cardUsages.map { card ->
                card.copy(
                    startDate = card.startDate.take(16),
                    endDate = card.endDate.take(16)
                )
            },
            carUsages = state.carUsages.map { car ->
                car.copy(
                    startDate = car.startDate.take(16),
                    endDate = car.endDate.take(16)
                )
            }
        )

        viewModelScope.launch {
            tripRepository.addTrip(
                request = request
            ).collect { result ->
                result
                    .onSuccess {
                        _uiEffect.emit(UiEffect.ShowToast("출장 신청이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[addTrip] 출장 신청 성공\n${request}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "addTrip")
                    }
            }
        }
    }

    /* 출장 현황 상세 조회 */
    fun getTrip(id: String) {
        viewModelScope.launch {
            tripRepository.getTrip(
               id = id
            ).collect { result ->
                result
                    .onSuccess { data ->
//                        _vacationDetailState.update { it.copy(vacationInfo = data) }

                        Log.d(TAG, "[getTrip] 출장 현황 상세 조회 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getTrip")
                    }
            }
        }
    }

    /* 이전 승인자 불러오기 */
    fun getPrevApprovers(target: TripTarget) {
        viewModelScope.launch {
            tripRepository.getPrevApprovers().collect { result ->
                result
                    .onSuccess { data ->
                        val newApproverIds = data.approvers.map { it.userId }

                        when(target) {
                            TripTarget.ADD -> _tripAddState.update { it.copy(it.inputData.copy(approverIds = newApproverIds)) }
                            TripTarget.EDIT -> _tripAddState.update { it.copy(it.inputData.copy(approverIds = newApproverIds)) }
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

    /* 차량 목록 조회 및 검색 */
    fun getCars(target: TripTarget) {
        val state = when (target) {
            TripTarget.ADD -> tripAddState.value.carState
            TripTarget.EDIT -> tripAddState.value.carState
        }

        val updateState: (CarManageState) -> Unit = { newState ->
            when (target) {
                TripTarget.ADD -> _tripAddState.update { it.copy(carState = newState) }
                TripTarget.EDIT -> _tripAddState.update { it.copy(carState = newState) }
            }
        }

        viewModelScope.launch {
            carRepository.getCars(
                keyword = state.searchText,
                type = state.type
            ).collect { result ->
                result
                    .onSuccess { data ->
                        updateState(state.copy(cars = data.cars))

                        Log.d(TAG, "[getCars] 차량 목록 조회 및 검색 성공 (${state.type}-${state.searchText})\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getCars")
                    }
            }
        }
    }

    /* 카드 목록 조회 및 검색 */
    fun getCards(target: TripTarget) {
        val state = when (target) {
            TripTarget.ADD -> tripAddState.value.cardState
            TripTarget.EDIT -> tripAddState.value.cardState
        }

        val updateState: (CardManageState) -> Unit = { newState ->
            when (target) {
                TripTarget.ADD -> _tripAddState.update { it.copy(cardState = newState) }
                TripTarget.EDIT -> _tripAddState.update { it.copy(cardState = newState) }
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
    fun getEmployees(target: TripTarget) {
        val state = when (target) {
            TripTarget.ADD -> tripAddState.value.employeeState
            TripTarget.EDIT -> tripAddState.value.employeeState
        }

        val updateState: (EmployeeSearchState) -> Unit = { newState ->
            when (target) {
                TripTarget.ADD -> _tripAddState.update { it.copy(employeeState = newState) }
                TripTarget.EDIT -> _tripAddState.update { it.copy(employeeState = newState) }
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

                        Log.d(TAG, "[getEmployees-${target}] 직원 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${tripAddState.value.employeeState.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = false)))

                        ErrorHandler.handle(e, TAG, "getEmployees-${target}")
                    }
            }
        }
    }

    /* 공통코드 목록에서 출장 종류 조회 */
    fun getTripType(target: TripTarget) {
        viewModelScope.launch {
            commonCodeRepository.getCommonCodes(
                searchType = SearchType.UPPER_CODE,
                searchKeyword = "SANCTN_MBY_BSRP",
                page = 0    // 개수가 더 늘어나면 페이지 관리도 해야함 지금은 필요 없음
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val tripTypeNames: List<String> = data.content.map { it.codeName }

                        when (target) {
                            TripTarget.ADD -> _tripAddState.update { it.copy(tripTypeNames = tripTypeNames) }
                            TripTarget.EDIT -> _tripAddState.update { it.copy(tripTypeNames = tripTypeNames) }
                        }

                        Log.d(TAG, "[getTripType-${target}] 출장 종류 목록 조회 성공\n${tripTypeNames}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getTripType-${target}")
                    }
            }
        }
    }
}