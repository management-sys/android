package com.example.attendancemanagementapp.ui.hr.department.add

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentField

object DepartmentAddReducer {
    fun reduce(s: DepartmentAddState, e: DepartmentAddEvent): DepartmentAddState = when (e) {
        is DepartmentAddEvent.Init -> handleInit()
        is DepartmentAddEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        else -> s
    }

    private fun handleInit(): DepartmentAddState {
        return DepartmentAddState()
    }

    private val departmentUpdaters: Map<DepartmentField, (DepartmentDTO.AddDepartmentRequest, String) -> DepartmentDTO.AddDepartmentRequest> =
        mapOf(
            DepartmentField.NAME        to { s, v -> s.copy(name = v) },
            DepartmentField.DESCRIPTION to { s, v -> s.copy(description = v) }
        )

    private fun handleChangedValue(
        state: DepartmentAddState,
        field: DepartmentField,
        value: String
    ): DepartmentAddState {
        val updater = departmentUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }
}