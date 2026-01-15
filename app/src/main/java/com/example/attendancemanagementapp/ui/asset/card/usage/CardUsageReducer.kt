package com.example.attendancemanagementapp.ui.asset.card.usage

object CardUsageReducer {
    fun reduce(s: CardUsageState, e: CardUsageEvent): CardUsageState = when (e) {
        CardUsageEvent.Init -> CardUsageState()
        is CardUsageEvent.SelectedTypeWith -> handleSelectedType(s, e.field, e.type)
        is CardUsageEvent.ChangedSearchTextWith -> handleChangedSearchText(s, e.field, e.value)
        is CardUsageEvent.ClickedInitSearchTextWith -> handleClickedInitSearchText(s, e.field)
        else -> s
    }

    private fun handleSelectedType(
        state: CardUsageState,
        field: CardUsageField,
        type: String
    ): CardUsageState {
        return when (field) {
            CardUsageField.RESERVATION -> state.copy(reservationState = state.reservationState.copy(type = type, paginationState = state.reservationState.paginationState.copy(currentPage = 0)))
            CardUsageField.USAGE -> state.copy(usageState = state.usageState.copy(type = type, paginationState = state.usageState.paginationState.copy(currentPage = 0)))
        }
    }

    private fun handleChangedSearchText(
        state: CardUsageState,
        field: CardUsageField,
        value: String
    ): CardUsageState {
        return when (field) {
            CardUsageField.RESERVATION -> state.copy(reservationState = state.reservationState.copy(searchText = value, paginationState = state.reservationState.paginationState.copy(currentPage = 0)))
            CardUsageField.USAGE -> state.copy(usageState = state.usageState.copy(searchText = value, paginationState = state.usageState.paginationState.copy(currentPage = 0)))
        }
    }

    private fun handleClickedInitSearchText(
        state: CardUsageState,
        field: CardUsageField
    ): CardUsageState {
        return when (field) {
            CardUsageField.RESERVATION -> state.copy(reservationState = state.reservationState.copy(searchText = "", paginationState = state.reservationState.paginationState.copy(currentPage = 0)))
            CardUsageField.USAGE -> state.copy(usageState = state.usageState.copy(searchText = "", paginationState = state.usageState.paginationState.copy(currentPage = 0)))
        }
    }
}