package com.example.attendancemanagementapp.ui.asset.card.edit

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO

object CardEditReducer {
    fun reduce(s: CardEditState, e: CardEditEvent): CardEditState = when (e) {
        is CardEditEvent.InitWith -> handleInit(e.carInfo)
        is CardEditEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is CardEditEvent.SelectedManagerWith -> handleSelectedManager(s, e.mangerInfo)
        is CardEditEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.value)
        CardEditEvent.ClickedSearchInit -> handleClickedSearchInit(s)
        else -> s
    }

    private fun handleInit(
        cardInfo: CardDTO.GetCardResponse
    ): CardEditState {
        return CardEditState(
            inputData = CardDTO.AddCardRequest(
                managerId = cardInfo.managerId,
                remark = cardInfo.remark,
                status = cardInfo.status,
                name = cardInfo.name
            ),
            id = cardInfo.id,
            managerName = cardInfo.managerName
        )
    }

    private val carUpdaters: Map<CardEditField, (CardDTO.AddCardRequest, String) -> CardDTO.AddCardRequest> =
        mapOf(
            CardEditField.NAME       to { s, v -> s.copy(name = v) },
            CardEditField.REMARK     to { s, v -> s.copy(remark = v) },
            CardEditField.STATUS     to { s, v -> s.copy(status = v) },
        )

    private fun handleChangedValue(
        state: CardEditState,
        field: CardEditField,
        value: String
    ): CardEditState {
        val updater = carUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleSelectedManager(
        state: CardEditState,
        managerInfo: EmployeeDTO.ManageEmployeesInfo
    ): CardEditState {
        return state.copy(inputData = state.inputData.copy(managerId = managerInfo.userId), managerName = managerInfo.name)
    }

    private fun handleChangedSearchValue(
        state: CardEditState,
        value: String
    ): CardEditState {
        return state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleClickedSearchInit(
        state: CardEditState
    ): CardEditState {
        return state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }
}