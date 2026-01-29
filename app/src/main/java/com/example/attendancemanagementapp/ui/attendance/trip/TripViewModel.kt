package com.example.attendancemanagementapp.ui.attendance.trip

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.repository.CarRepository
import com.example.attendancemanagementapp.data.repository.CardRepository
import com.example.attendancemanagementapp.data.repository.CommonCodeRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.data.repository.TripRepository
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.asset.car.manage.CarManageState
import com.example.attendancemanagementapp.ui.asset.card.manage.CardManageState
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripAddEvent
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripAddReducer
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripAddState
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripSearchField
import com.example.attendancemanagementapp.ui.attendance.trip.detail.TripDetailEvent
import com.example.attendancemanagementapp.ui.attendance.trip.detail.TripDetailState
import com.example.attendancemanagementapp.ui.attendance.trip.edit.TripEditEvent
import com.example.attendancemanagementapp.ui.attendance.trip.edit.TripEditReducer
import com.example.attendancemanagementapp.ui.attendance.trip.edit.TripEditState
import com.example.attendancemanagementapp.ui.attendance.trip.status.TripStatusEvent
import com.example.attendancemanagementapp.ui.attendance.trip.status.TripStatusReducer
import com.example.attendancemanagementapp.ui.attendance.trip.status.TripStatusState
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
    private val _tripDetailState = MutableStateFlow(TripDetailState())
    val tripDetailState = _tripDetailState.asStateFlow()
    private val _tripEditState = MutableStateFlow(TripEditState())
    val tripEditState = _tripEditState.asStateFlow()
    private val _tripStatusState = MutableStateFlow(TripStatusState())
    val tripStatusState = _tripStatusState.asStateFlow()

    fun onAddEvent(e: TripAddEvent) {
        _tripAddState.update { TripAddReducer.reduce(it, e) }

        when (e) {
            TripAddEvent.Init -> {
                getTripType(TripTarget.ADD)
                getEmployees(TripTarget.ADD)
                getCards(TripTarget.ADD)
                getCars(TripTarget.ADD)
            }
            is TripAddEvent.ClickedSearchWith -> {
                when (e.field) {
                    TripSearchField.APPROVER -> getEmployees(TripTarget.ADD)
                    TripSearchField.ATTENDEE -> getEmployees(TripTarget.ADD)
                    TripSearchField.CARD -> getCards(TripTarget.ADD)
                    TripSearchField.CAR -> getCars(TripTarget.ADD)
                    TripSearchField.DRIVER -> getEmployees(TripTarget.ADD)
                }
            }
            is TripAddEvent.ClickedSearchInitWith -> {
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

    fun onDetailEvent(e: TripDetailEvent) {
        when (e) {
            TripDetailEvent.ClickedCancel -> cancelTrip()
            TripDetailEvent.ClickedDelete -> deleteTrip()
            TripDetailEvent.ClickedUpdate -> {
                // 결재(승인/반려) 이전에만 수정 가능
                if (tripDetailState.value.tripInfo.status == "승인" || tripDetailState.value.tripInfo.status == "반려") {
                    _uiEffect.tryEmit(UiEffect.ShowToast("결재 이전에만 수정 가능합니다"))
                } else {
                    onEditEvent(TripEditEvent.InitWith(tripDetailState.value.tripInfo))
                    _uiEffect.tryEmit(UiEffect.Navigate("tripEdit"))
                }
            }
            TripDetailEvent.ClickedDownload -> downloadTripPdf()
            TripDetailEvent.ClickedAddReport -> _uiEffect.tryEmit(UiEffect.Navigate("reportAdd"))
            else -> Unit
        }
    }

    fun onEditEvent(e: TripEditEvent) {
        _tripEditState.update { TripEditReducer.reduce(it, e) }

        when (e) {
            is TripEditEvent.InitWith -> {
                getTripType(TripTarget.EDIT)
                getEmployees(TripTarget.EDIT)
                getCards(TripTarget.EDIT)
                getCars(TripTarget.EDIT)
            }
            is TripEditEvent.ClickedSearch -> {
                when (e.field) {
                    TripSearchField.APPROVER -> getEmployees(TripTarget.EDIT)
                    TripSearchField.ATTENDEE -> getEmployees(TripTarget.EDIT)
                    TripSearchField.CARD -> getCards(TripTarget.EDIT)
                    TripSearchField.CAR -> getCars(TripTarget.EDIT)
                    TripSearchField.DRIVER -> getEmployees(TripTarget.EDIT)
                }
            }
            is TripEditEvent.ClickedSearchInit -> {
                when (e.field) {
                    TripSearchField.APPROVER -> getEmployees(TripTarget.EDIT)
                    TripSearchField.ATTENDEE -> getEmployees(TripTarget.EDIT)
                    TripSearchField.CARD -> getCards(TripTarget.EDIT)
                    TripSearchField.CAR -> getCars(TripTarget.EDIT)
                    TripSearchField.DRIVER -> getEmployees(TripTarget.EDIT)
                }
            }
            TripEditEvent.LoadNextPage -> getEmployees(TripTarget.EDIT)
            TripEditEvent.ClickedUpdate -> updateTrip()
            TripEditEvent.ClickedGetPrevApprover -> getPrevApprovers(TripTarget.EDIT)
            else -> Unit
        }
    }

    fun onStatusEvent(e: TripStatusEvent) {
        _tripStatusState.update { TripStatusReducer.reduce(it, e) }

        when (e) {
            TripStatusEvent.Init -> getTrips(isInit = true)
            is TripStatusEvent.SelectedYearWith -> getTrips()
            is TripStatusEvent.SelectedFilterWith -> getTrips()
            is TripStatusEvent.ClickedTripWith -> {
                getTrip(e.id)
                _uiEffect.tryEmit(UiEffect.Navigate("tripDetail"))
            }
            TripStatusEvent.LoadNextPage -> getTrips()
            else -> Unit
        }
    }

    /* 출장 현황 목록 조회 */
    fun getTrips(isInit: Boolean = false) {
        val state = tripStatusState.value

        _tripStatusState.update { it.copy(paginationState = it.paginationState.copy(isLoading = true)) }

        viewModelScope.launch {
            tripRepository.getTrips(
                query = state.query.copy(filter = if (state.query.filter == "전체") "" else state.query.filter),
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedTrips = if (isFirstPage) data.trips else state.tripStatusInfo.trips + data.trips
                        val updatedTripStatus = data.copy(trips = updatedTrips)

                        if (isInit) {
                            _tripStatusState.update { it.copy(
                                tripStatusInfo = updatedTripStatus,
                                paginationState = it.paginationState.copy(
                                    currentPage = it.paginationState.currentPage + 1,
                                    totalPage = data.pageInfo.totalPages,
                                    isLoading = false
                                )
                            ) }
                        } else {
                            _tripStatusState.update { it.copy(
                                tripStatusInfo = updatedTripStatus,
                                paginationState = it.paginationState.copy(
                                    currentPage = it.paginationState.currentPage + 1,
                                    totalPage = data.pageInfo.totalPages,
                                    isLoading = false
                                )
                            ) }
                        }

                        Log.d(TAG, "[getTrips] 출장 현황 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.pageInfo.totalPages}, 검색(${state.query.year}년차, ${state.query.filter})\n${data}")
                    }
                    .onFailure { e ->
                        _tripStatusState.update { it.copy(paginationState = it.paginationState.copy(isLoading = false)) }

                        ErrorHandler.handle(e, TAG, "getTrips")
                    }
            }
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
                        _tripDetailState.update { it.copy(tripInfo = data) }

                        Log.d(TAG, "[getTrip] 출장 현황 상세 조회 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getTrip")
                    }
            }
        }
    }

    /* 출장 품의서 수정 */
    fun updateTrip() {
        // 결재(승인/반려) 이전에만 수정 가능
        if (tripDetailState.value.tripInfo.status == "승인" || tripDetailState.value.tripInfo.status == "반려") {
            return
        }

        val state = tripEditState.value
        val request = state.inputData.copy(
            startDate = state.inputData.startDate.take(16),
            endDate = state.inputData.endDate.take(16),
            cardUsages = state.inputData.cardUsages.map { card ->
                card.copy(
                    startDate = card.startDate.take(16),
                    endDate = card.endDate.take(16)
                )
            },
            carUsages = state.inputData.carUsages.map { car ->
                car.copy(
                    startDate = car.startDate.take(16),
                    endDate = car.endDate.take(16)
                )
            }
        )

        viewModelScope.launch {
            tripRepository.updateTrip(
                id = state.tripId,
                request = request
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _tripDetailState.update { it.copy(tripInfo = data) }

                        _uiEffect.emit(UiEffect.ShowToast("수정이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[updateTrip] 출장 품의서 수정 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "updateTrip")
                    }
            }
        }
    }

    /* 출장 신청 삭제 */
    fun deleteTrip() {
        viewModelScope.launch {
            tripRepository.deleteTrip(
                id = tripDetailState.value.tripInfo.id
            ).collect { result ->
                result
                    .onSuccess {
                        _uiEffect.emit(UiEffect.ShowToast("출장 신청이 삭제되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[deleteTrip] 출장 신청 삭제 성공")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "deleteTrip")
                    }
            }
        }
    }

    /* 출장 신청 취소 */
    fun cancelTrip() {
        // 복명서 작성 전까지만 취소 가능
        if (tripDetailState.value.tripInfo.hasReport == "Y") {
            _uiEffect.tryEmit(UiEffect.ShowToast("복명서 작성 전에만 취소할 수 있습니다"))
            return
        }

        // 이미 취소 상태인 경우 토스트
        if (tripDetailState.value.tripInfo.status == "C") {
            _uiEffect.tryEmit(UiEffect.ShowToast("이미 취소 상태입니다"))
            return
        }

        viewModelScope.launch {
            tripRepository.cancelTrip(
                id = tripDetailState.value.tripInfo.id
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _tripDetailState.update { it.copy(tripInfo = data) }

                        _uiEffect.emit(UiEffect.ShowToast("출장 신청이 취소되었습니다"))

                        Log.d(TAG, "[cancelTrip] 출장 신청 취소 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "cancelTrip")
                    }
            }
        }
    }

    /* 출장 품의서 다운로드(PDF) */
    fun downloadTripPdf() {
        viewModelScope.launch {
            tripRepository.downloadTripPdf(
                id = tripDetailState.value.tripInfo.id
            ).collect { result ->
                result
                    .onSuccess { uri ->
                        _uiEffect.emit(UiEffect.ShowToast("다운로드가 완료되었습니다"))

                        Log.d(TAG, "[downloadTripPdf] 출장 품의서 다운로드 성공: ${uri}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "downloadTripPdf")
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
                            TripTarget.EDIT -> _tripEditState.update { it.copy(it.inputData.copy(approverIds = newApproverIds)) }
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
            TripTarget.EDIT -> tripEditState.value.carState
        }

        val updateState: (CarManageState) -> Unit = { newState ->
            when (target) {
                TripTarget.ADD -> _tripAddState.update { it.copy(carState = newState) }
                TripTarget.EDIT -> _tripEditState.update { it.copy(carState = newState) }
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
            TripTarget.EDIT -> tripEditState.value.cardState
        }

        val updateState: (CardManageState) -> Unit = { newState ->
            when (target) {
                TripTarget.ADD -> _tripAddState.update { it.copy(cardState = newState) }
                TripTarget.EDIT -> _tripEditState.update { it.copy(cardState = newState) }
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
            TripTarget.EDIT -> tripEditState.value.employeeState
        }

        val updateState: (EmployeeSearchState) -> Unit = { newState ->
            when (target) {
                TripTarget.ADD -> _tripAddState.update { it.copy(employeeState = newState) }
                TripTarget.EDIT -> _tripEditState.update { it.copy(employeeState = newState) }
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
                            TripTarget.EDIT -> _tripEditState.update { it.copy(tripTypeNames = tripTypeNames) }
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