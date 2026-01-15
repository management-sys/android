package com.example.attendancemanagementapp.ui.asset.card.manage

object CardManageReducer {
    fun reduce(s: CardManageState, e: CardManageEvent): CardManageState = when (e) {
        is CardManageEvent.SelectedTypeWith -> handleSelectedType(s, e.type)
        is CardManageEvent.ChangedSearchTextWith -> handleChangedSearchText(s, e.value)
        CardManageEvent.ClickedInitSearchText -> handleClickedInitSearchText(s)
        else -> s
    }

    private fun handleSelectedType(
        state: CardManageState,
        type: String
    ): CardManageState {
        return state.copy(type = type)
    }

    private fun handleChangedSearchText(
        state: CardManageState,
        value: String
    ): CardManageState {
        return state.copy(searchText = value)
    }

    private fun handleClickedInitSearchText(
        state: CardManageState
    ): CardManageState {
        return state.copy(searchText = "")
    }
}