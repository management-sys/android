package com.example.attendancemanagementapp.ui.hr.employee.search

import com.example.attendancemanagementapp.data.dto.EmployeeDTO

data class EmployeeSearchState(
    val employees: List<EmployeeDTO.EmployeesInfo> = emptyList(),         // 직원 목록
    val employeeInfo: EmployeeDTO.EmployeeInfo = EmployeeDTO.EmployeeInfo(),    // 직원 정보
    val searchText: String = ""                                     // 검색어
)