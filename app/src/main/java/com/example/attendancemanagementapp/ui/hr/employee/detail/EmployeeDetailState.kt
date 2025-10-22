package com.example.attendancemanagementapp.ui.hr.employee.detail

import com.example.attendancemanagementapp.data.dto.EmployeeDTO

data class EmployeeDetailState(
    val employeeInfo: EmployeeDTO.EmployeeInfo = EmployeeDTO.EmployeeInfo() // 직원 정보
)