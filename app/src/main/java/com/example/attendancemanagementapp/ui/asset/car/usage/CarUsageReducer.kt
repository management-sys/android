package com.example.attendancemanagementapp.ui.asset.car.usage

object CarUsageReducer {
    fun reduce(s: CarUsageState, e: CarUsageEvent): CarUsageState = when (e) {
        CarUsageEvent.Init -> CarUsageState()
        is CarUsageEvent.SelectedTypeWith -> handleSelectedType(s, e.field, e.type)
        is CarUsageEvent.ChangedSearchTextWith -> handleChangedSearchText(s, e.field, e.value)
        is CarUsageEvent.ClickedInitSearchTextWith -> handleClickedInitSearchText(s, e.field)
        else -> s
    }

    private fun handleSelectedType(
        state: CarUsageState,
        field: CarUsageField,
        type: String
    ): CarUsageState {
        return when (field) {
            CarUsageField.RESERVATION -> state.copy(reservationState = state.reservationState.copy(type = type, paginationState = state.reservationState.paginationState.copy(currentPage = 0)))
            CarUsageField.USAGE -> state.copy(usageState = state.usageState.copy(type = type, paginationState = state.usageState.paginationState.copy(currentPage = 0)))
        }
    }

    private fun handleChangedSearchText(
        state: CarUsageState,
        field: CarUsageField,
        value: String
    ): CarUsageState {
        return when (field) {
            CarUsageField.RESERVATION -> state.copy(reservationState = state.reservationState.copy(searchText = value, paginationState = state.reservationState.paginationState.copy(currentPage = 0)))
            CarUsageField.USAGE -> state.copy(usageState = state.usageState.copy(searchText = value, paginationState = state.usageState.paginationState.copy(currentPage = 0)))
        }
    }

    private fun handleClickedInitSearchText(
        state: CarUsageState,
        field: CarUsageField
    ): CarUsageState {
        return when (field) {
            CarUsageField.RESERVATION -> state.copy(reservationState = state.reservationState.copy(searchText = "", paginationState = state.reservationState.paginationState.copy(currentPage = 0)))
            CarUsageField.USAGE -> state.copy(usageState = state.usageState.copy(searchText = "", paginationState = state.usageState.paginationState.copy(currentPage = 0)))
        }
    }
}