package com.example.attendancemanagementapp.ui.asset.car.add

import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class CarAddState(
    val inputData: CarDTO.AddCarRequest = CarDTO.AddCarRequest(),
    val managerName: String = "",
    val employeeState: EmployeeSearchState = EmployeeSearchState(), // 직원 검색 관련 상태
)