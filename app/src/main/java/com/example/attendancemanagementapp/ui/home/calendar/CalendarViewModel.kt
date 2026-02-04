package com.example.attendancemanagementapp.ui.home.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.datastore.TokenDataStore
import com.example.attendancemanagementapp.data.param.TripsQuery
import com.example.attendancemanagementapp.data.param.VacationsQuery
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.data.repository.TripRepository
import com.example.attendancemanagementapp.data.repository.VacationRepository
import com.example.attendancemanagementapp.ui.attendance.trip.TripViewModel
import com.example.attendancemanagementapp.ui.attendance.vacation.VacationViewModel
import com.example.attendancemanagementapp.ui.base.UiEffect
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
import kotlin.collections.plus

@HiltViewModel
class CalendarViewModel @Inject constructor(private val vacationRepository: VacationRepository, private val tripRepository: TripRepository, private val tokenDataStore: TokenDataStore) : ViewModel() {
    companion object {
        private const val TAG = "CalendarViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    val userId = runBlocking {
        tokenDataStore.userIdFlow.first()
    }

    private val _calendarState = MutableStateFlow(CalendarState())
    val calendarState = _calendarState.asStateFlow()

    fun onEvent(e: CalendarEvent) {
        _calendarState.update { CalendarReducer.reduce(it, e) }

        when (e) {
            CalendarEvent.Init -> {
                getTrips(true)
                getVacations(true)
            }
            else -> Unit
        }
    }

    /* 바텀 시트 열림 처리 */
    fun resetOpenSheet() {
        _calendarState.update { it.copy(openSheet = false) }
    }

    /* 출장 현황 목록 조회 */
    fun getTrips(isInit: Boolean = false) {
        val state = calendarState.value.tripStatusState

        _calendarState.update { it.copy(tripStatusState = it.tripStatusState.copy(paginationState = it.tripStatusState.paginationState.copy(isLoading = true))) }

        viewModelScope.launch {
            tripRepository.getTrips(
                query = TripsQuery(filter = ""),
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedTrips = if (isFirstPage) data.trips else state.tripStatusInfo.trips + data.trips
                        val updatedTripStatus = data.copy(trips = updatedTrips)

                        if (isInit) {
                            _calendarState.update { it.copy(tripStatusState = it.tripStatusState.copy(
                                tripStatusInfo = updatedTripStatus,
                                paginationState = it.tripStatusState.paginationState.copy(
                                    currentPage = it.tripStatusState.paginationState.currentPage + 1,
                                    totalPage = data.pageInfo.totalPages,
                                    isLoading = false
                                )
                            )) }
                        } else {
                            _calendarState.update { it.copy(tripStatusState = it.tripStatusState.copy(
                                tripStatusInfo = updatedTripStatus,
                                paginationState = it.tripStatusState.paginationState.copy(
                                    currentPage = it.tripStatusState.paginationState.currentPage + 1,
                                    totalPage = data.pageInfo.totalPages,
                                    isLoading = false
                                )
                            )) }
                        }

                        Log.d(TAG, "[getTrips] 출장 현황 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.pageInfo.totalPages}, 검색(${state.query.year}년차, ${state.query.filter})\n${data}")
                    }
                    .onFailure { e ->
                        _calendarState.update { it.copy(tripStatusState = it.tripStatusState.copy(paginationState = it.tripStatusState.paginationState.copy(isLoading = false))) }

                        ErrorHandler.handle(e, TAG, "getTrips")
                    }
            }
        }
    }

    /* 휴가 현황 목록 조회 */
    fun getVacations(isInit: Boolean = false) {
        val state = calendarState.value.vacationStatusState

        _calendarState.update { it.copy(vacationStatusState = it.vacationStatusState.copy(paginationState = it.vacationStatusState.paginationState.copy(isLoading = true))) }

        viewModelScope.launch {
            vacationRepository.getVacations(
                userId = userId,
                query = VacationsQuery(),
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedVacations = if (isFirstPage) data.vacations else state.vacationStatusInfo.vacations + data.vacations
                        val updatedVacationStatus = data.copy(vacations = updatedVacations)

                        if (isInit) {
                            _calendarState.update { it.copy(vacationStatusState = it.vacationStatusState.copy(
                                vacationStatusInfo = updatedVacationStatus,
                                paginationState = it.vacationStatusState.paginationState.copy(
                                    currentPage = it.vacationStatusState.paginationState.currentPage + 1,
                                    totalPage = data.pageInfo.totalPages,
                                    isLoading = false
                                )
                            )) }
                        } else {
                            _calendarState.update { it.copy(vacationStatusState = it.vacationStatusState.copy(
                                vacationStatusInfo = updatedVacationStatus,
                                paginationState = it.vacationStatusState.paginationState.copy(
                                    currentPage = it.vacationStatusState.paginationState.currentPage + 1,
                                    totalPage = data.pageInfo.totalPages,
                                    isLoading = false
                                )
                            )) }
                        }

                        Log.d(TAG, "[getVacations] 휴가 현황 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.pageInfo.totalPages}, 검색(${state.query.year}년차, ${state.query.filter.name})\n${data}")
                    }
                    .onFailure { e ->
                        _calendarState.update { it.copy(vacationStatusState = it.vacationStatusState.copy(paginationState = it.vacationStatusState.paginationState.copy(isLoading = false))) }

                        ErrorHandler.handle(e, TAG, "getVacations")
                    }
            }
        }
    }
}