package com.example.attendancemanagementapp.ui.hr.employee.search

object EmployeeSearchReducer {
    fun reduce(s: EmployeeSearchState, e: EmployeeSearchEvent): EmployeeSearchState = when (e) {
        is EmployeeSearchEvent.ChangedSearchWith -> handleChangedSearch(s, e.value)
        EmployeeSearchEvent.ClickedInitSearch -> handleClickedInitSearch()
        else -> s
    }

    private fun handleChangedSearch(
        state: EmployeeSearchState,
        value: String
    ): EmployeeSearchState {
        return state.copy(searchText = value)
    }

    private fun handleClickedInitSearch(): EmployeeSearchState {
        return EmployeeSearchState()
    }
}