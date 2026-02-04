package com.example.attendancemanagementapp.ui.approver.request

import com.example.attendancemanagementapp.data.param.ApproverQuery

object ApproverRequestReducer {
    fun reduce(s: ApproverRequestState, e: ApproverRequestEvent): ApproverRequestState = when (e) {
        ApproverRequestEvent.InitLast -> handleInitLast()
        is ApproverRequestEvent.ClickedUseFilter -> handleClickedUseFilter(s, e.filter)
        ApproverRequestEvent.ClickedInitFilter -> handleClickedInitFilter(s)
        else -> s
    }

    private fun handleInitLast(): ApproverRequestState {
        return ApproverRequestState()
    }

    private fun handleClickedUseFilter(
        state: ApproverRequestState,
        filter: ApproverQuery
    ): ApproverRequestState {
        return state.copy(filter = filter, paginationState = state.paginationState.copy(currentPage = 0))
    }

    private fun handleClickedInitFilter(
        state: ApproverRequestState
    ): ApproverRequestState {
        return state.copy(filter = ApproverQuery(), paginationState = state.paginationState.copy(currentPage = 0))
    }
}