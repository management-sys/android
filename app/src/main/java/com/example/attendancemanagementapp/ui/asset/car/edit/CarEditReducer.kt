package com.example.attendancemanagementapp.ui.asset.car.edit

import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO

object CarEditReducer {
    fun reduce(s: CarEditState, e: CarEditEvent): CarEditState = when (e) {
        is CarEditEvent.InitWith -> handleInit(e.carInfo)
        is CarEditEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is CarEditEvent.SelectedManagerWith -> handleSelectedManager(s, e.mangerInfo)
        is CarEditEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.value)
        CarEditEvent.ClickedSearchInit -> handleClickedSearchInit(s)
        else -> s
    }

    private fun handleInit(
        carInfo: CarDTO.GetCarResponse
    ): CarEditState {
        return CarEditState(
            inputData = CarDTO.AddCarRequest(
                managerId = carInfo.managerId,
                fuelType = carInfo.fuelType,
                ownership = carInfo.ownership,
                remark = carInfo.remark,
                status = carInfo.status,
                name = carInfo.name,
                number = carInfo.number,
                type = carInfo.type
            ),
            id = carInfo.id,
            managerName = carInfo.managerName
        )
    }

    private val carUpdaters: Map<CarEditField, (CarDTO.AddCarRequest, String) -> CarDTO.AddCarRequest> =
        mapOf(
            CarEditField.TYPE       to { s, v -> s.copy(type = v) },
            CarEditField.NUMBER     to { s, v -> s.copy(number = v) },
            CarEditField.FUEL       to { s, v -> s.copy(fuelType = v) },
            CarEditField.OWNER      to { s, v -> s.copy(ownership = v) },
            CarEditField.NAME       to { s, v -> s.copy(name = v) },
            CarEditField.REMARK     to { s, v -> s.copy(remark = v) },
            CarEditField.STATUS     to { s, v -> s.copy(status = v) },
        )

    private fun handleChangedValue(
        state: CarEditState,
        field: CarEditField,
        value: String
    ): CarEditState {
        val updater = carUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleSelectedManager(
        state: CarEditState,
        managerInfo: EmployeeDTO.ManageEmployeesInfo
    ): CarEditState {
        return state.copy(inputData = state.inputData.copy(managerId = managerInfo.userId), managerName = managerInfo.name)
    }

    private fun handleChangedSearchValue(
        state: CarEditState,
        value: String
    ): CarEditState {
        return state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleClickedSearchInit(
        state: CarEditState
    ): CarEditState {
        return state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }
}