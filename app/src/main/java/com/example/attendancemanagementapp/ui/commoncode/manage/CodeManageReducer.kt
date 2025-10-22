package com.example.attendancemanagementapp.ui.commoncode.manage

import com.example.attendancemanagementapp.retrofit.param.SearchType

object CodeManageReducer {
    fun reduce(s: CodeManageState, e: CodeManageEvent): CodeManageState = when (e) {
        CodeManageEvent.InitSearch -> handleInitSearch()
        is CodeManageEvent.ChangedSearchWith -> handleChangedSearch(s, e.value)
        is CodeManageEvent.ChangedCategoryWith -> handleChangedCategory(s, e.category)
        CodeManageEvent.ClickedInitSearch -> handleClickedInitSearch(s)
        else -> s
    }

    private fun handleInitSearch(): CodeManageState {
        return CodeManageState()
    }

    private fun handleChangedSearch(
        state: CodeManageState,
        value: String
    ): CodeManageState {
        return state.copy(searchText = value, currentPage = 0)
    }

    private fun handleChangedCategory(
        state: CodeManageState,
        category: SearchType
    ): CodeManageState {
        return state.copy(selectedCategory = category, currentPage = 0)
    }

    private fun handleClickedInitSearch(
        state: CodeManageState
    ): CodeManageState {
        return state.copy(searchText = "", currentPage = 0)
    }
}