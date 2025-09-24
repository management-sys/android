package com.example.attendancemanagementapp.ui.hr.employee.detail

import com.example.attendancemanagementapp.data.dto.HrDTO

data class EmployeeDetailUiState(
    val employeeInfo: HrDTO.EmployeeInfo = HrDTO.EmployeeInfo() // 직원 정보
)