package com.example.attendancemanagementapp.ui.commoncode.edit

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.commoncode.add.CodeSearchState

object CodeEditReducer {
    fun reduce(s: CodeEditState, e: CodeEditEvent): CodeEditState = when (e) {
        is CodeEditEvent.InitWith -> handleInit(s, e.codeInfo)
        CodeEditEvent.InitSearch -> handleInitSearch(s)
        is CodeEditEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is CodeEditEvent.SelectedUpperCodeWith -> handleSelectedUpperCode(s, e.upperCode, e.upperCodeName)
        is CodeEditEvent.ChangedSearchWith -> handleChangedSearch(s, e.value)
        is CodeEditEvent.ChangedCategoryWith -> handleChangedCategory(s, e.category)
        CodeEditEvent.ClickedInitSearch -> handleClickedInitSearch(s)
        else -> s
    }

    private fun handleInit(
        state: CodeEditState,
        codeInfo: CommonCodeDTO.CommonCodeInfo
    ): CodeEditState {
        return state.copy(inputData = codeInfo)
    }

    private fun handleInitSearch(
        state: CodeEditState
    ): CodeEditState {
        return state.copy(codeState = CodeSearchState())
    }

    private val codeUpdaters: Map<CodeEditField, (CommonCodeDTO.CommonCodeInfo, String) -> CommonCodeDTO.CommonCodeInfo> =
        mapOf(
            CodeEditField.CODENAME       to { s, v -> s.copy(codeName = v) },
            CodeEditField.CODEVALUE      to { s, v -> s.copy(codeValue = v) },
            CodeEditField.DESCRIPTION    to { s, v -> s.copy(description = v) }
        )

    private fun handleChangedValue(
        state: CodeEditState,
        field: CodeEditField,
        value: String
    ): CodeEditState {
        val updater = codeUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleSelectedUpperCode(
        state: CodeEditState,
        upperCode: String,
        upperCodeName: String
    ): CodeEditState {
        return state.copy(inputData = state.inputData.copy(upperCode = upperCode, upperCodeName = upperCodeName))
    }

    private fun handleChangedSearch(
        state: CodeEditState,
        value: String
    ): CodeEditState {
        return state.copy(codeState = state.codeState.copy(searchText = value, paginationState = state.codeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleChangedCategory(
        state: CodeEditState,
        category: SearchType
    ): CodeEditState {
        return state.copy(codeState = state.codeState.copy(selectedCategory = category, paginationState = state.codeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleClickedInitSearch(
        state: CodeEditState
    ): CodeEditState {
        return state.copy(codeState = state.codeState.copy(searchText = "", paginationState = state.codeState.paginationState.copy(currentPage = 0)))
    }
}