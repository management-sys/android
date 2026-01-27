package com.example.attendancemanagementapp.ui.attendance.trip.status

import com.example.attendancemanagementapp.data.param.TripsQuery
import com.example.attendancemanagementapp.retrofit.param.PaginationState

object TripStatusReducer {
    fun reduce(s: TripStatusState, e: TripStatusEvent): TripStatusState = when (e) {
        TripStatusEvent.Init -> handleInit(s)
        is TripStatusEvent.SelectedYearWith -> handleSelectedYear(s, e.year)
        is TripStatusEvent.SelectedFilterWith -> handleSelectedFilter(s, e.filter)
        else -> s
    }

    private fun handleInit(
        state: TripStatusState
    ): TripStatusState {
        return state.copy(
            query = TripsQuery(),
            paginationState = PaginationState()
        )
    }

    private fun handleSelectedYear(
        state: TripStatusState,
        year: Int?
    ): TripStatusState {
        return state.copy(
            tripStatusInfo = state.tripStatusInfo.copy(trips = emptyList()),
            query = state.query.copy(year = year),
            paginationState = state.paginationState.copy(currentPage = 0)
        )
    }

    private fun handleSelectedFilter(
        state: TripStatusState,
        filter: String
    ): TripStatusState {
        return state.copy(
            tripStatusInfo = state.tripStatusInfo.copy(trips = emptyList()),
            query = state.query.copy(filter = filter),
            paginationState = state.paginationState.copy(currentPage = 0)
        )
    }
}