package com.example.attendancemanagementapp.ui.commoncode.edit

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO

object CodeEditReducer {
    fun reduce(s: CodeEditState, e: CodeEditEvent): CodeEditState = when (e) {
        is CodeEditEvent.InitWith -> handleInit(s, e.codeInfo)
        is CodeEditEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is CodeEditEvent.SelectedUpperCodeWith -> handleSelectedUpperCode(s, e.upperCode, e.upperCodeName)
        else -> s
    }

    private fun handleInit(
        state: CodeEditState,
        codeInfo: CommonCodeDTO.CommonCodeInfo
    ): CodeEditState {
        return state.copy(inputData = codeInfo)
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
}