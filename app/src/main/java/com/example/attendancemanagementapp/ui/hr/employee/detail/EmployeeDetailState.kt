package com.example.attendancemanagementapp.ui.hr.employee.detail

import com.example.attendancemanagementapp.data.dto.EmployeeDTO

data class EmployeeDetailState(
    val employeeInfo: EmployeeDTO.EmployeeInfo = EmployeeDTO.EmployeeInfo(), // 직원 정보
    val careerInfo: List<EmployeeDTO.CareerInfo> = listOf(  // 경력 정보 테스트 (회사명, 입사일, 퇴사일, 기간)
        EmployeeDTO.CareerInfo(id = 0, name = "테이큰소프트", hireDate = "2025-07-21", resignDate = ""),
        EmployeeDTO.CareerInfo(id = 1, name = "영남대", hireDate = "2024-01-01", resignDate = "2025-06-30"),
//        "테이큰소프트", "2025-07-21", "", "3개월 (재직중)"
    )
)