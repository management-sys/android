package com.example.attendancemanagementapp.ui.meeting.status

object MeetingStatusReducer {
    fun reduce(s: MeetingStatusState, e: MeetingStatusEvent): MeetingStatusState = when (e) {
        MeetingStatusEvent.Init -> handleInit()
        is MeetingStatusEvent.ChangedSearchValue -> handleChangedSearchValue(s, e.value)
        MeetingStatusEvent.ClickedInitSearch -> handleClickedInitSearch(s)
        MeetingStatusEvent.ClickedInitFilter -> handleClickedInitFilter(s)
        is MeetingStatusEvent.ClickedUseFilter -> handleClickedUseFilter(s, e.startDate, e.endDate, e.type)
        else -> s
    }

    private fun handleInit(): MeetingStatusState {
        return MeetingStatusState()
    }

    private fun handleChangedSearchValue(
        state: MeetingStatusState,
        value: String
    ): MeetingStatusState {
        return state.copy(searchText = value, paginationState = state.paginationState.copy(currentPage = 0))
    }

    private fun handleClickedInitSearch(
        state: MeetingStatusState
    ): MeetingStatusState {
        return state.copy(searchText = "", paginationState = state.paginationState.copy(currentPage = 0))
    }

    private fun handleClickedInitFilter(
        state: MeetingStatusState
    ): MeetingStatusState {
        return state.copy(startDate = "", endDate = "", type = "전체", paginationState = state.paginationState.copy(currentPage = 0))
    }

    private fun handleClickedUseFilter(
        state: MeetingStatusState,
        startDate: String,
        endDate: String,
        type: String
    ): MeetingStatusState {
        return state.copy(startDate = startDate, endDate = endDate, type = type, paginationState = state.paginationState.copy(currentPage = 0))
    }
}