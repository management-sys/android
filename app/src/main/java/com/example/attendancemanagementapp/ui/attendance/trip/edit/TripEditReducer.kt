package com.example.attendancemanagementapp.ui.attendance.trip.edit

import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripAddField
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripSearchField
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TripEditReducer {
    fun reduce(s: TripEditState, e: TripEditEvent): TripEditState = when (e) {
        is TripEditEvent.InitWith -> handleInit(e.data)
        is TripEditEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is TripEditEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.field, e.value)
        is TripEditEvent.ClickedSearchInit -> handleClickedSearchInit(s, e.field)
        is TripEditEvent.SelectedCardTypeWith -> handleSelectedCardType(s, e.type)
        is TripEditEvent.SelectedCarTypeWith -> handleSelectedCarType(s, e.type)
        is TripEditEvent.SelectedApproverWith -> handleSelectedApprover(s, e. checked, e.id)
        is TripEditEvent.SelectedAttendeeWith -> handleSelectedAttendee(s, e. checked, e.id)
        is TripEditEvent.SelectedTypeWith -> handleSelectedType(s, e.type)
        is TripEditEvent.CheckedCardWith -> handleCheckCard(s, e.checked, e.card)
        is TripEditEvent.ChangedCardDateWith -> handleChangedCardDate(s, e.id, e.isStart, e.value)
        is TripEditEvent.CheckedCarWith -> handleCheckCar(s, e.checked, e.car)
        is TripEditEvent.ChangedCarDateWith -> handleChangedCarDate(s, e.id, e.isStart, e.value)
        is TripEditEvent.SelectedDriverWith -> handleSelectedDriver(s, e.id, e.driverId)
        else -> s
    }

    private fun handleInit(
        data: TripDTO.GetTripResponse
    ): TripEditState {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        return TripEditState(
            inputData = TripDTO.UpdateTripRequest(
                startDate = LocalDateTime.parse(data.startDate, inputFormatter).format(outputFormatter),
                place = data.place,
                purpose = data.purpose,
                type = data.type,
                cardUsages = data.cards.map { card ->
                    TripDTO.CardUsagesInfo(
                        id = card.id,
                        startDate = LocalDateTime.parse(card.startDate, inputFormatter).format(outputFormatter),
                        endDate = LocalDateTime.parse(card.endDate, inputFormatter).format(outputFormatter)
                    )
                },
                content = data.content,
                approverIds = listOf(data.approverId),
                endDate = LocalDateTime.parse(data.endDate, inputFormatter).format(outputFormatter),
                attendeeIds = data.attendees.map { it.id },
                carUsages = data.cars.map { car ->
                    TripDTO.CarUsagesInfo(
                        id = car.id,
                        driverId = car.driverId,
                        startDate = LocalDateTime.parse(car.startDate, inputFormatter).format(outputFormatter),
                        endDate = LocalDateTime.parse(car.endDate, inputFormatter).format(outputFormatter)
                    )
                }
            ),
            tripId = data.id
        )
    }

    private val tripUpdaters: Map<TripAddField, (TripDTO.UpdateTripRequest, String) -> TripDTO.UpdateTripRequest> =
        mapOf(
            TripAddField.START      to { s, v -> s.copy(startDate = v) },
            TripAddField.END        to { s, v -> s.copy(endDate = v) },
            TripAddField.PLACE      to { s, v -> s.copy(place = v) },
            TripAddField.PURPOSE    to { s, v -> s.copy(purpose = v) },
            TripAddField.CONTENT    to { s, v -> s.copy(content = v) }
        )

    private fun handleChangedValue(
        state: TripEditState,
        field: TripAddField,
        value: String
    ): TripEditState {
        val updater = tripUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleChangedSearchValue(
        state: TripEditState,
        field: TripSearchField,
        value: String
    ): TripEditState {
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
        state: TripEditState,
        field: TripSearchField
    ): TripEditState {
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
        state: TripEditState,
        type: String
    ): TripEditState {
        return state.copy(cardState = state.cardState.copy(type = type))
    }

    private fun handleSelectedCarType(
        state: TripEditState,
        type: String
    ): TripEditState {
        return state.copy(carState = state.carState.copy(type = type))
    }

    private fun handleSelectedApprover(
        state: TripEditState,
        checked: Boolean,
        id: String
    ): TripEditState {
        val updatedList = if (checked) state.inputData.approverIds + id else state.inputData.approverIds - id

        return state.copy(inputData = state.inputData.copy(approverIds = updatedList))
    }

    private fun handleSelectedAttendee(
        state: TripEditState,
        checked: Boolean,
        id: String
    ): TripEditState {
        val updatedList = if (checked) state.inputData.attendeeIds + id else state.inputData.attendeeIds - id

        return state.copy(inputData = state.inputData.copy(attendeeIds = updatedList))
    }

    private fun handleSelectedType(
        state: TripEditState,
        type: String
    ): TripEditState {
        return state.copy(inputData = state.inputData.copy(type = type))
    }

    private fun handleCheckCard(
        state: TripEditState,
        checked: Boolean,
        card: CardDTO.CardsInfo
    ): TripEditState {
        val newCard = TripDTO.CardUsagesInfo(id = card.id, startDate = "", endDate = "")
        val updatedList = if (checked) state.inputData.cardUsages + newCard else state.inputData.cardUsages - newCard
        val sortedList = updatedList.sortedBy { card -> state.cardState.cards.indexOfFirst { it.id == card.id } }

        return state.copy(inputData = state.inputData.copy(cardUsages = sortedList))
    }

    private fun handleChangedCardDate(
        state: TripEditState,
        id: String,
        isStart: Boolean,
        value: String
    ): TripEditState {
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
        state: TripEditState,
        checked: Boolean,
        car: CarDTO.CarsInfo
    ): TripEditState {
        val newCar = TripDTO.CarUsagesInfo(id = car.id, driverId = "", startDate = "", endDate = "")
        val updatedList = if (checked) state.inputData.carUsages + newCar else state.inputData.carUsages - newCar
        val sortedList = updatedList.sortedBy { card -> state.carState.cars.indexOfFirst { it.id == car.id } }

        return state.copy(inputData = state.inputData.copy(carUsages = sortedList))
    }

    private fun handleChangedCarDate(
        state: TripEditState,
        id: String,
        isStart: Boolean,
        value: String
    ): TripEditState {
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
        state: TripEditState,
        id: String,
        driverId: String
    ): TripEditState {
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