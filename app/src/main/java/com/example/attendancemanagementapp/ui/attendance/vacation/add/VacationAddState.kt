package com.example.attendancemanagementapp.ui.attendance.vacation.add

import com.example.attendancemanagementapp.data.dto.VacationDTO
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class VacationAddState(
    val inputData: VacationDTO.AddVacationRequest = VacationDTO.AddVacationRequest(),   // 입력 데이터
    val employeeState: EmployeeSearchState = EmployeeSearchState(),                     // 직원 검색 관련 상태
)