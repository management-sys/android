package com.example.attendancemanagementapp.ui.hr.employee.search

import com.example.attendancemanagementapp.data.dto.HrDTO

data class EmployeeSearchUiState(
    val employees: List<HrDTO.EmployeesInfo> = emptyList(),         // 직원 목록
    val employeeInfo: HrDTO.EmployeeInfo = HrDTO.EmployeeInfo(),    // 직원 정보
    val searchText: String = ""                                     // 검색어
)