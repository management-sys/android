package com.example.attendancemanagementapp.ui.commoncode.add

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState
import com.example.attendancemanagementapp.retrofit.param.SearchType

object CodeAddReducer {
    fun reduce(s: CodeAddState, e: CodeAddEvent): CodeAddState = when (e) {
        CodeAddEvent.InitSearch -> handleInitSearch(s)
        is CodeAddEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is CodeAddEvent.SelectedUpperCodeWith -> handleSelectedUpperCode(s, e.upperCode, e.upperCodeName)
        is CodeAddEvent.ChangedSearchWith -> handleChangedSearch(s, e.value)
        is CodeAddEvent.ChangedCategoryWith -> handleChangedCategory(s, e.category)
        CodeAddEvent.ClickedInitSearch -> handleClickedInitSearch(s)
        else -> s
    }

    private fun handleInitSearch(
        state: CodeAddState
    ): CodeAddState {
        return state.copy(codeState = CodeSearchState())
    }

    private val codeUpdaters: Map<CodeAddField, (CommonCodeDTO.CommonCodeInfo, String) -> CommonCodeDTO.CommonCodeInfo> =
        mapOf(
            CodeAddField.CODE           to { s, v -> s.copy(code = v) },
            CodeAddField.CODENAME       to { s, v -> s.copy(codeName = v) },
            CodeAddField.CODEVALUE      to { s, v -> s.copy(codeValue = v) },
            CodeAddField.DESCRIPTION    to { s, v -> s.copy(description = v) }
        )

    private fun handleChangedValue(
        state: CodeAddState,
        field: CodeAddField,
        value: String
    ): CodeAddState {
        val updater = codeUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleSelectedUpperCode(
        state: CodeAddState,
        upperCode: String,
        upperCodeName: String
    ): CodeAddState {
        return state.copy(inputData = state.inputData.copy(upperCode = upperCode, upperCodeName = upperCodeName))
    }

    private fun handleChangedSearch(
        state: CodeAddState,
        value: String
    ): CodeAddState {
        return state.copy(codeState = state.codeState.copy(searchText = value, paginationState = state.codeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleChangedCategory(
        state: CodeAddState,
        category: SearchType
    ): CodeAddState {
        return state.copy(codeState = state.codeState.copy(selectedCategory = category, paginationState = state.codeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleClickedInitSearch(
        state: CodeAddState
    ): CodeAddState {
        return state.copy(codeState = state.codeState.copy(searchText = "", paginationState = state.codeState.paginationState.copy(currentPage = 0)))
    }
}