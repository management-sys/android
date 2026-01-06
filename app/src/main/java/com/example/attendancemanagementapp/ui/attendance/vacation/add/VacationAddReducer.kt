package com.example.attendancemanagementapp.ui.attendance.vacation.add

import com.example.attendancemanagementapp.data.dto.VacationDTO

object VacationAddReducer {
    fun reduce(s: VacationAddState, e: VacationAddEvent): VacationAddState = when (e) {
        is VacationAddEvent.InitWith -> handleInitWith(e.id)
        is VacationAddEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is VacationAddEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.value)
        VacationAddEvent.ClickedSearchInit -> handleClickedSearchInit(s)
        is VacationAddEvent.SelectedApproverWith -> handleSelectedApprover(s, e. checked, e.id)
        else -> s
    }

    private fun handleInitWith(
        id: String
    ): VacationAddState {
        return VacationAddState(inputData = VacationDTO.AddVacationRequest(userId = id))
    }

    private val vacationUpdaters: Map<VacationAddField, (VacationDTO.AddVacationRequest, String) -> VacationDTO.AddVacationRequest> =
        mapOf(
            VacationAddField.TYPE       to { s, v -> s.copy(type = v) },
            VacationAddField.START      to { s, v -> s.copy(startDate = v) },
            VacationAddField.END        to { s, v -> s.copy(endDate = v) },
            VacationAddField.DETAIL     to { s, v -> s.copy(detail = v) }
        )

    private fun handleChangedValue(
        state: VacationAddState,
        field: VacationAddField,
        value: String
    ): VacationAddState {
        val updater = vacationUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleChangedSearchValue(
        state: VacationAddState,
        value: String
    ): VacationAddState {
        return state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleClickedSearchInit(
        state: VacationAddState
    ): VacationAddState {
        return state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleSelectedApprover(
        state: VacationAddState,
        checked: Boolean,
        id: String
    ): VacationAddState {
        val updatedList = if (checked) state.inputData.approverIds + id else state.inputData.approverIds - id

        return state.copy(inputData = state.inputData.copy(approverIds = updatedList))
    }
}