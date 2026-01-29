package com.example.attendancemanagementapp.ui.attendance.trip.add

import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.TripDTO

object TripAddReducer {
    fun reduce(s: TripAddState, e: TripAddEvent): TripAddState = when (e) {
        is TripAddEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is TripAddEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.field, e.value)
        is TripAddEvent.ClickedSearchInitWith -> handleClickedSearchInit(s, e.field)
        is TripAddEvent.SelectedCardTypeWith -> handleSelectedCardType(s, e.type)
        is TripAddEvent.SelectedCarTypeWith -> handleSelectedCarType(s, e.type)
        is TripAddEvent.SelectedApproverWith -> handleSelectedApprover(s, e. checked, e.id)
        is TripAddEvent.SelectedAttendeeWith -> handleSelectedAttendee(s, e. checked, e.id)
        is TripAddEvent.SelectedTypeWith -> handleSelectedType(s, e.type)
        is TripAddEvent.CheckedCardWith -> handleCheckCard(s, e.checked, e.card)
        is TripAddEvent.ChangedCardDateWith -> handleChangedCardDate(s, e.id, e.isStart, e.value)
        is TripAddEvent.CheckedCarWith -> handleCheckCar(s, e.checked, e.car)
        is TripAddEvent.ChangedCarDateWith -> handleChangedCarDate(s, e.id, e.isStart, e.value)
        is TripAddEvent.SelectedDriverWith -> handleSelectedDriver(s, e.id, e.driverId)
        else -> s
    }

    private val tripUpdaters: Map<TripAddField, (TripDTO.AddTripRequest, String) -> TripDTO.AddTripRequest> =
        mapOf(
            TripAddField.START      to { s, v -> s.copy(startDate = v) },
            TripAddField.END        to { s, v -> s.copy(endDate = v) },
            TripAddField.PLACE      to { s, v -> s.copy(place = v) },
            TripAddField.PURPOSE    to { s, v -> s.copy(purpose = v) },
            TripAddField.CONTENT    to { s, v -> s.copy(content = v) }
        )

    private fun handleChangedValue(
        state: TripAddState,
        field: TripAddField,
        value: String
    ): TripAddState {
        val updater = tripUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleChangedSearchValue(
        state: TripAddState,
        field: TripSearchField,
        value: String
    ): TripAddState {
        return when(field) {
            TripSearchField.ATTENDEE -> {
                state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
            }
            TripSearchField.APPROVER -> {
                state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
            }
            TripSearchField.CARD -> {
                state.copy(cardState = state.cardState.copy(searchText = value))
            }
            TripSearchField.CAR -> {
                state.copy(carState = state.carState.copy(searchText = value))
            }
            TripSearchField.DRIVER -> {
                state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
            }
        }
    }

    private fun handleClickedSearchInit(
        state: TripAddState,
        field: TripSearchField
    ): TripAddState {
        return when(field) {
            TripSearchField.ATTENDEE -> {
                state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
            }
            TripSearchField.APPROVER -> {
                state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
            }
            TripSearchField.CARD -> {
                state.copy(cardState = state.cardState.copy(searchText = "", type = "전체"))
            }
            TripSearchField.CAR -> {
                state.copy(carState = state.carState.copy(searchText = "", type = "전체"))
            }
            TripSearchField.DRIVER -> {
                state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
            }
        }
    }

    private fun handleSelectedCardType(
        state: TripAddState,
        type: String
    ): TripAddState {
        return state.copy(cardState = state.cardState.copy(type = type))
    }

    private fun handleSelectedCarType(
        state: TripAddState,
        type: String
    ): TripAddState {
        return state.copy(carState = state.carState.copy(type = type))
    }

    private fun handleSelectedApprover(
        state: TripAddState,
        checked: Boolean,
        id: String
    ): TripAddState {
        val updatedList = if (checked) state.inputData.approverIds + id else state.inputData.approverIds - id

        return state.copy(inputData = state.inputData.copy(approverIds = updatedList))
    }

    private fun handleSelectedAttendee(
        state: TripAddState,
        checked: Boolean,
        id: String
    ): TripAddState {
        val updatedList = if (checked) state.inputData.attendeeIds + id else state.inputData.attendeeIds - id

        return state.copy(inputData = state.inputData.copy(attendeeIds = updatedList))
    }

    private fun handleSelectedType(
        state: TripAddState,
        type: String
    ): TripAddState {
        return state.copy(inputData = state.inputData.copy(type = type))
    }

    private fun handleCheckCard(
        state: TripAddState,
        checked: Boolean,
        card: CardDTO.CardsInfo
    ): TripAddState {
        val newCard = TripDTO.CardUsagesInfo(id = card.id, startDate = "", endDate = "")
        val updatedList = if (checked) state.inputData.cardUsages + newCard else state.inputData.cardUsages - newCard
        val sortedList = updatedList.sortedBy { card -> state.cardState.cards.indexOfFirst { it.id == card.id } }

        return state.copy(inputData = state.inputData.copy(cardUsages = sortedList))
    }

    private fun handleChangedCardDate(
        state: TripAddState,
        id: String,
        isStart: Boolean,
        value: String
    ): TripAddState {
        val updatedList = state.inputData.cardUsages!!.map { cardUsage ->
            if (cardUsage.id == id) {
                cardUsage.copy(startDate = if (isStart) value else cardUsage.startDate, endDate = if (isStart) cardUsage.endDate else value)
            } else {
                cardUsage
            }
        }

        return state.copy(inputData = state.inputData.copy(cardUsages = updatedList))
    }

    private fun handleCheckCar(
        state: TripAddState,
        checked: Boolean,
        car: CarDTO.CarsInfo
    ): TripAddState {
        val newCar = TripDTO.CarUsagesInfo(id = car.id, driverId = "", startDate = "", endDate = "")
        val updatedList = if (checked) state.inputData.carUsages + newCar else state.inputData.carUsages - newCar
        val sortedList = updatedList.sortedBy { card -> state.carState.cars.indexOfFirst { it.id == car.id } }

        return state.copy(inputData = state.inputData.copy(carUsages = sortedList))
    }

    private fun handleChangedCarDate(
        state: TripAddState,
        id: String,
        isStart: Boolean,
        value: String
    ): TripAddState {
        val updatedList = state.inputData.carUsages!!.map { carUsage ->
            if (carUsage.id == id) {
                carUsage.copy(startDate = if (isStart) value else carUsage.startDate, endDate = if (isStart) carUsage.endDate else value)
            } else {
                carUsage
            }
        }

        return state.copy(inputData = state.inputData.copy(carUsages = updatedList))
    }

    private fun handleSelectedDriver(
        state: TripAddState,
        id: String,
        driverId: String
    ): TripAddState {
        val updatedList = state.inputData.carUsages!!.map { carUsage ->
            if (carUsage.id == id) {
                carUsage.copy(driverId = driverId)
            } else {
                carUsage
            }
        }

        return state.copy(inputData = state.inputData.copy(carUsages = updatedList))
    }
}