package com.example.attendancemanagementapp.ui.project.personnel

import com.example.attendancemanagementapp.data.param.PersonnelsQuery

object ProjectPersonnelReducer {
    fun reduce(s: ProjectPersonnelState, e: ProjectPersonnelEvent): ProjectPersonnelState =
        when (e) {
            is ProjectPersonnelEvent.ClickedUseFilter -> handleClickedUseFilter(s, e.filter)
            ProjectPersonnelEvent.ClickedInitFilter -> handleClickedInitFilter(s)
            is ProjectPersonnelEvent.ChangedSearchTextWith -> handleChangedSearchText(s, e.value)
            ProjectPersonnelEvent.ClickedInitSearchText -> handleClickedInitSearchText(s)
            else -> s
        }

    private fun handleClickedUseFilter(
        state: ProjectPersonnelState,
        filter: PersonnelsQuery
    ): ProjectPersonnelState {
        return state.copy(
            filter = filter,
            paginationState = state.paginationState.copy(currentPage = 0)
        )
    }

    private fun handleClickedInitFilter(
        state: ProjectPersonnelState
    ): ProjectPersonnelState {
        return state.copy(
            filter = PersonnelsQuery(),
            paginationState = state.paginationState.copy(currentPage = 0)
        )
    }

    private fun handleChangedSearchText(
        state: ProjectPersonnelState,
        value: String
    ): ProjectPersonnelState {
        return state.copy(
            filter = state.filter.copy(userName = value),
            paginationState = state.paginationState.copy(currentPage = 0)
        )
    }

    private fun handleClickedInitSearchText(
        state: ProjectPersonnelState
    ): ProjectPersonnelState {
        return state.copy(
            filter = state.filter.copy(userName = ""),
            paginationState = state.paginationState.copy(currentPage = 0)
        )
    }
}