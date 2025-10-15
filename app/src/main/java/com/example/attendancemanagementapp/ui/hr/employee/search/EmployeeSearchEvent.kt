package com.example.attendancemanagementapp.ui.hr.employee.search

import com.example.attendancemanagementapp.ui.hr.employee.EmployeeTarget

sealed interface EmployeeSearchEvent {
    // 직원 검색 필드 값 변경 이벤트
    data class ChangedSearchWith(
        val value: String
    ): EmployeeSearchEvent

    // 직원 검색 버튼 클릭 이벤트
    data object ClickedSearch: EmployeeSearchEvent

    // 직원 검색어 초기화 버튼 클릭 이벤트
    data object ClickedInitSearch: EmployeeSearchEvent

    // 정보 조회할 직원 선택 이벤트
    data class SelectedEmployeeWith(
        val target: EmployeeTarget,
        val userId: String
    ): EmployeeSearchEvent
}