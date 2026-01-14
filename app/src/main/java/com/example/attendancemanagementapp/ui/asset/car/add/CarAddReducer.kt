package com.example.attendancemanagementapp.ui.asset.car.add

import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.asset.car.edit.CarEditField

object CarAddReducer {
    fun reduce(s: CarAddState, e: CarAddEvent): CarAddState = when (e) {
        is CarAddEvent.InitWith -> handleInit(e.carInfo)
        is CarAddEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is CarAddEvent.SelectedManagerWith -> handleSelectedManager(s, e.mangerInfo)
        is CarAddEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.value)
        CarAddEvent.ClickedSearchInit -> handleClickedSearchInit(s)
        else -> s
    }

    private fun handleInit(
        carInfo: CarDTO.GetCarResponse
    ): CarAddState {
        return CarAddState(
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
        state: CarAddState,
        field: CarEditField,
        value: String
    ): CarAddState {
        val updater = carUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleSelectedManager(
        state: CarAddState,
        managerInfo: EmployeeDTO.ManageEmployeesInfo
    ): CarAddState {
        return state.copy(inputData = state.inputData.copy(managerId = managerInfo.userId), managerName = managerInfo.name)
    }

    private fun handleChangedSearchValue(
        state: CarAddState,
        value: String
    ): CarAddState {
        return state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleClickedSearchInit(
        state: CarAddState
    ): CarAddState {
        return state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }
}