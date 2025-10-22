package com.example.attendancemanagementapp.ui.commoncode.add

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO

object CodeAddReducer {
    fun reduce(s: CodeAddState, e: CodeAddEvent): CodeAddState = when (e) {
        is CodeAddEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is CodeAddEvent.SelectedUpperCodeWith -> handleSelectedUpperCode(s, e.upperCode, e.upperCodeName)
        else -> s
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
}