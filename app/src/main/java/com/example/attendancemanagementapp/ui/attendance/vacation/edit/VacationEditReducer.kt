package com.example.attendancemanagementapp.ui.attendance.vacation.edit

import com.example.attendancemanagementapp.data.dto.VacationDTO

object VacationEditReducer {
    fun reduce(s: VacationEditState, e: VacationEditEvent): VacationEditState = when (e) {
        is VacationEditEvent.InitWith -> handleInit(e.data)
        is VacationEditEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is VacationEditEvent.SelectedTypeWith -> handleSelectedType(s, e.type)
        is VacationEditEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.value)
        VacationEditEvent.ClickedSearchInit -> handleClickedSearchInit(s)
        is VacationEditEvent.SelectedApproverWith -> handleSelectedApprover(s, e. checked, e.id)
        else -> s
    }

    private fun handleInit(
        data: VacationDTO.GetVacationResponse
    ): VacationEditState {
        return VacationEditState(
            inputData = VacationDTO.UpdateVacationRequest(
                startDate = data.startDate,
                endDate = data.endDate,
                detail = data.detail,
                approverIds = listOf(data.approverId),
                type = data.type
            ),
            vacationId = data.id
        )
    }

    private val vacationUpdaters: Map<VacationEditField, (VacationDTO.UpdateVacationRequest, String) -> VacationDTO.UpdateVacationRequest> =
        mapOf(
            VacationEditField.START      to { s, v -> s.copy(startDate = v) },
            VacationEditField.END        to { s, v -> s.copy(endDate = v) },
            VacationEditField.DETAIL     to { s, v -> s.copy(detail = v) }
        )

    private fun handleChangedValue(
        state: VacationEditState,
        field: VacationEditField,
        value: String
    ): VacationEditState {
        val updater = vacationUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleSelectedType(
        state: VacationEditState,
        type: String
    ): VacationEditState {
        return state.copy(inputData = state.inputData.copy(type = type))
    }

    private fun handleChangedSearchValue(
        state: VacationEditState,
        value: String
    ): VacationEditState {
        return state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleClickedSearchInit(
        state: VacationEditState
    ): VacationEditState {
        return state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleSelectedApprover(
        state: VacationEditState,
        checked: Boolean,
        id: String
    ): VacationEditState {
        val updatedList = if (checked) state.inputData.approverIds + id else state.inputData.approverIds - id

        return state.copy(inputData = state.inputData.copy(approverIds = updatedList))
    }
}