package com.example.attendancemanagementapp.ui.project.status

import com.example.attendancemanagementapp.data.param.ProjectStatusQuery

object ProjectStatusReducer {
    fun reduce(s: ProjectStatusState, e: ProjectStatusEvent): ProjectStatusState = when (e) {
        ProjectStatusEvent.InitLast -> handleInitLast()
        is ProjectStatusEvent.ChangedSearchTextWith -> handleChangedSearchText(s, e.value)
        ProjectStatusEvent.ClickedInitSearchText -> handleClickedInitSearchText(s)
        is ProjectStatusEvent.ClickedUseFilter -> handleClickedUseFilter(s, e.filter)
        ProjectStatusEvent.ClickedInitFilter -> handleClickedInitFilter(s)
        else -> s
    }

    private fun handleInitLast(): ProjectStatusState {
        return ProjectStatusState()
    }

    private fun handleChangedSearchText(
        state: ProjectStatusState,
        value: String
    ): ProjectStatusState {
        return state.copy(filter = state.filter.copy(searchText = value), paginationState = state.paginationState.copy(currentPage = 0))
    }

    private fun handleClickedInitSearchText(
        state: ProjectStatusState
    ): ProjectStatusState {
        return state.copy(filter = state.filter.copy(searchText = ""), paginationState = state.paginationState.copy(currentPage = 0))
    }

    private fun handleClickedUseFilter(
        state: ProjectStatusState,
        filter: ProjectStatusQuery
    ): ProjectStatusState {
        return state.copy(filter = filter, paginationState = state.paginationState.copy(currentPage = 0))
    }

    private fun handleClickedInitFilter(
        state: ProjectStatusState
    ): ProjectStatusState {
        return state.copy(filter = ProjectStatusQuery(), paginationState = state.paginationState.copy(currentPage = 0))
    }
}