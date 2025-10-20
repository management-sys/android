package com.example.attendancemanagementapp.ui.hr.employee.detail

import com.example.attendancemanagementapp.data.dto.EmployeeDTO

data class EmployeeDetailState(
    val employeeInfo: EmployeeDTO.EmployeeInfo = EmployeeDTO.EmployeeInfo(), // 직원 정보
//    val annualLeaveInfo: List<String> = listOf(),    // 연차 정보
//    val annualLeaveInfo: List<String> = listOf( // 연차 정보 테스트 (연차, 시작일, 종료일, 연차 개수, 이월 연차 개수, 사용 연차 개수
//        "0", "2025-07-21", "2026-07-20", "2", "0", "0"
//    ),
//    val careerInfo: List<String> = listOf(), // 경력 정보
//    val careerInfo: List<String> = listOf(  // 경력 정보 테스트 (회사명, 입사일, 퇴사일, 기간)
//        "테이큰소프트", "2025-07-21", "", "3개월 (재직중)"
//    )
)