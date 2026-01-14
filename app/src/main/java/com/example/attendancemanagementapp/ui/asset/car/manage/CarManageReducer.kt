package com.example.attendancemanagementapp.ui.asset.car.manage

object CarManageReducer {
    fun reduce(s: CarManageState, e: CarManageEvent): CarManageState = when (e) {
        is CarManageEvent.SelectedTypeWith -> handleSelectedType(s, e.type)
        is CarManageEvent.ChangedSearchTextWith -> handleChangedSearchText(s, e.value)
        CarManageEvent.ClickedInitSearchText -> handleClickedInitSearchText(s)
        else -> s
    }

    private fun handleSelectedType(
        state: CarManageState,
        type: String
    ): CarManageState {
        return state.copy(type = type)
    }

    private fun handleChangedSearchText(
        state: CarManageState,
        value: String
    ): CarManageState {
        return state.copy(searchText = value)
    }

    private fun handleClickedInitSearchText(
        state: CarManageState
    ): CarManageState {
        return state.copy(searchText = "")
    }
}