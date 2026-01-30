package com.example.attendancemanagementapp.ui.attendance.report.edit

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.ui.attendance.report.add.TripExpenseField
import com.example.attendancemanagementapp.ui.attendance.report.add.TripExpenseSearchField

object ReportEditReducer {
    fun reduce(s: ReportEditState, e: ReportEditEvent): ReportEditState = when (e) {
        is ReportEditEvent.InitWith -> handleInit(e.tripInfo, e.reportInfo)
        is ReportEditEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.field, e.value)
        is ReportEditEvent.ClickedSearchInitWith -> handleClickedSearchInit(s, e.field)
        is ReportEditEvent.SelectedApproverWith -> handleSelectedApprover(s, e. checked, e.id)
        is ReportEditEvent.ChangedContentWith -> handleChangedContent(s, e.value)
        ReportEditEvent.ClickedAddExpense -> handleClickedAddExpense(s)
        is ReportEditEvent.SelectedCardTypeWith -> handleSelectedCardType(s, e.type)
        is ReportEditEvent.SelectedCardWith -> handleCheckCard(s, e.card, e.idx)
        is ReportEditEvent.SelectedManagerWith -> handleSelectedManager(s, e.id, e.idx)
        is ReportEditEvent.ChangedExpenseValueWith -> handleChangedExpenseValue(s, e.field, e.idx, e.value)
        is ReportEditEvent.ClickedDeleteTripExpenseWith -> handleClickedDeleteTripExpense(s, e.idx)
        else -> s
    }

    private fun handleInit(
        tripInfo: TripDTO.GetTripResponse,
        reportInfo: TripDTO.GetTripReportResponse
    ): ReportEditState {
        val inputData = TripDTO.UpdateTripReportRequest(
            content = reportInfo.content,
            approverIds = listOf(reportInfo.approverId),
            tripExpenses = reportInfo.tripExpenses.map { tripExpenseInfo ->
                TripDTO.AddTripExpenseInfo(
                    amount = tripExpenseInfo.amount,
                    type = tripExpenseInfo.type,
                    buyerId = tripExpenseInfo.buyerId,
                    category = tripExpenseInfo.category
                )
            }
        )
        return ReportEditState(inputData = inputData, tripInfo = tripInfo)
    }

    private fun handleChangedSearchValue(
        state: ReportEditState,
        field: TripExpenseSearchField,
        value: String
    ): ReportEditState {
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
        state: ReportEditState,
        field: TripExpenseSearchField
    ): ReportEditState {
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
        state: ReportEditState,
        checked: Boolean,
        id: String
    ): ReportEditState {
        val updatedList = if (checked) state.inputData.approverIds + id else state.inputData.approverIds - id
        return state.copy(inputData = state.inputData.copy(approverIds = updatedList))
    }

    private fun handleChangedContent(
        state: ReportEditState,
        value: String
    ): ReportEditState {
        return state.copy(inputData = state.inputData.copy(content = value))
    }

    private fun handleClickedAddExpense(
        state: ReportEditState
    ): ReportEditState {
        return state.copy(inputData = state.inputData.copy(tripExpenses = state.inputData.tripExpenses + TripDTO.AddTripExpenseInfo()))
    }

    private fun handleSelectedCardType(
        state: ReportEditState,
        type: String
    ): ReportEditState {
        return state.copy(cardState = state.cardState.copy(type = type))
    }

    private fun handleCheckCard(
        state: ReportEditState,
        card: CardDTO.CardsInfo,
        idx: Int
    ): ReportEditState {
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
        state: ReportEditState,
        id: String,
        idx: Int
    ): ReportEditState {
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
        state: ReportEditState,
        field: TripExpenseField,
        idx: Int,
        value: String
    ): ReportEditState {
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

    private fun handleClickedDeleteTripExpense(
        state: ReportEditState,
        idx: Int
    ): ReportEditState {
        val tripExpenses = state.inputData.tripExpenses
        val updated = tripExpenses.toMutableList().apply { removeAt(idx) }

        return state.copy(inputData = state.inputData.copy(tripExpenses = updated))
    }
}