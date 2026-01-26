package com.example.attendancemanagementapp.ui.attendance.trip.status

object TripStatusReducer {
    fun reduce(s: TripStatusState, e: TripStatusEvent): TripStatusState = when (e) {
        is TripStatusEvent.SelectedYearWith -> handleSelectedYear(s, e.year)
        is TripStatusEvent.SelectedFilterWith -> handleSelectedFilter(s, e.filter)
        else -> s
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