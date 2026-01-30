package com.example.attendancemanagementapp.ui.attendance.report.add

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.TripDTO

object ReportAddReducer {
    fun reduce(s: ReportAddState, e: ReportAddEvent): ReportAddState = when (e) {
        is ReportAddEvent.InitWith -> handleInit(e.tripInfo)
        is ReportAddEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.field, e.value)
        is ReportAddEvent.ClickedSearchInitWith -> handleClickedSearchInit(s, e.field)
        is ReportAddEvent.SelectedApproverWith -> handleSelectedApprover(s, e. checked, e.id)
        is ReportAddEvent.ChangedContentWith -> handleChangedContent(s, e.value)
        ReportAddEvent.ClickedAddExpense -> handleClickedAddExpense(s)
        is ReportAddEvent.SelectedCardTypeWith -> handleSelectedCardType(s, e.type)
        is ReportAddEvent.SelectedCardWith -> handleCheckCard(s, e.card, e.idx)
        is ReportAddEvent.SelectedManagerWith -> handleSelectedManager(s, e.id, e.idx)
        is ReportAddEvent.ChangedExpenseValueWith -> handleChangedExpenseValue(s, e.field, e.idx, e.value)
        else -> s
    }

    private fun handleInit(
        tripInfo: TripDTO.GetTripResponse
    ): ReportAddState {
        return ReportAddState(inputData = TripDTO.AddTripReportRequest(tripId = tripInfo.id), tripInfo = tripInfo)
    }

    private fun handleChangedSearchValue(
        state: ReportAddState,
        field: TripExpenseSearchField,
        value: String
    ): ReportAddState {
        return when (field) {
            TripExpenseSearchField.APPROVER -> {
                state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
            }
            TripExpenseSearchField.CARD -> {
                state.copy(cardState = state.cardState.copy(searchText = value))
            }
            TripExpenseSearchField.PAYER -> {
                state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
            }
        }
    }

    private fun handleClickedSearchInit(
        state: ReportAddState,
        field: TripExpenseSearchField
    ): ReportAddState {
        return when (field) {
            TripExpenseSearchField.APPROVER -> {
                state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
            }
            TripExpenseSearchField.CARD -> {
                state.copy(cardState = state.cardState.copy(searchText = "", type = "전체"))
            }
            TripExpenseSearchField.PAYER -> {
                state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
            }
        }
    }

    private fun handleSelectedApprover(
        state: ReportAddState,
        checked: Boolean,
        id: String
    ): ReportAddState {
        val updatedList = if (checked) state.inputData.approverIds + id else state.inputData.approverIds - id
        return state.copy(inputData = state.inputData.copy(approverIds = updatedList))
    }

    private fun handleChangedContent(
        state: ReportAddState,
        value: String
    ): ReportAddState {
        return state.copy(inputData = state.inputData.copy(content = value))
    }

    private fun handleClickedAddExpense(
        state: ReportAddState
    ): ReportAddState {
        return state.copy(inputData = state.inputData.copy(tripExpenses = state.inputData.tripExpenses + TripDTO.AddTripExpenseInfo()))
    }

    private fun handleSelectedCardType(
        state: ReportAddState,
        type: String
    ): ReportAddState {
        return state.copy(cardState = state.cardState.copy(type = type))
    }

    private fun handleCheckCard(
        state: ReportAddState,
        card: CardDTO.CardsInfo,
        idx: Int
    ): ReportAddState {
        val newTripExpenses = state.inputData.tripExpenses.mapIndexed { index, tripExpense ->
            if (index == idx) {
                tripExpense.copy(buyerId = card.id)
            } else {
                tripExpense
            }
        }

        return state.copy(inputData = state.inputData.copy(tripExpenses = newTripExpenses))
    }

    private fun handleSelectedManager(
        state: ReportAddState,
        id: String,
        idx: Int
    ): ReportAddState {
        val newTripExpenses = state.inputData.tripExpenses.mapIndexed { index, tripExpense ->
            if (index == idx) {
                tripExpense.copy(buyerId = id)
            } else {
                tripExpense
            }
        }

        return state.copy(inputData = state.inputData.copy(tripExpenses = newTripExpenses))
    }

    private fun handleChangedExpenseValue(
        state: ReportAddState,
        field: TripExpenseField,
        idx: Int,
        value: String
    ): ReportAddState {
        val tripExpense = state.inputData.tripExpenses[idx]
        val newTripExpense = when (field) {
            TripExpenseField.TYPE -> tripExpense.copy(type = value, buyerId = "")
            TripExpenseField.AMOUNT -> tripExpense.copy(amount = value.toInt())
            TripExpenseField.CATEGORY -> tripExpense.copy(category = value)
        }
        val newTripExpenses = state.inputData.tripExpenses.mapIndexed { index, tripExpense ->
            if (index == idx) {
                newTripExpense
            } else {
                tripExpense
            }
        }

        return state.copy(inputData = state.inputData.copy(tripExpenses = newTripExpenses))
    }
}