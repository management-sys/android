package com.example.attendancemanagementapp.ui.asset.card.add

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.asset.card.edit.CardEditField

object CardAddReducer {
    fun reduce(s: CardAddState, e: CardAddEvent): CardAddState = when (e) {
        is CardAddEvent.InitWith -> handleInit(e.carInfo)
        is CardAddEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is CardAddEvent.SelectedManagerWith -> handleSelectedManager(s, e.mangerInfo)
        is CardAddEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.value)
        CardAddEvent.ClickedSearchInit -> handleClickedSearchInit(s)
        else -> s
    }

    private fun handleInit(
        cardInfo: CardDTO.GetCardResponse
    ): CardAddState {
        return CardAddState(
            inputData = CardDTO.AddCardRequest(
                managerId = cardInfo.managerId,
                remark = cardInfo.remark,
                status = cardInfo.status,
                name = cardInfo.name
            ),
            managerName = cardInfo.managerName
        )
    }

    private val cardUpdaters: Map<CardEditField, (CardDTO.AddCardRequest, String) -> CardDTO.AddCardRequest> =
        mapOf(
            CardEditField.NAME       to { s, v -> s.copy(name = v) },
            CardEditField.REMARK     to { s, v -> s.copy(remark = v) },
            CardEditField.STATUS     to { s, v -> s.copy(status = v) },
        )

    private fun handleChangedValue(
        state: CardAddState,
        field: CardEditField,
        value: String
    ): CardAddState {
        val updater = cardUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleSelectedManager(
        state: CardAddState,
        managerInfo: EmployeeDTO.ManageEmployeesInfo
    ): CardAddState {
        return state.copy(inputData = state.inputData.copy(managerId = managerInfo.userId), managerName = managerInfo.name)
    }

    private fun handleChangedSearchValue(
        state: CardAddState,
        value: String
    ): CardAddState {
        return state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleClickedSearchInit(
        state: CardAddState
    ): CardAddState {
        return state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }
}