package com.example.attendancemanagementapp.ui.hr.employee.manage

import com.example.attendancemanagementapp.ui.hr.employee.HrTarget

enum class DropDownField { DEPARTMENT, GRADE, TITLE }

sealed interface EmployeeManageEvent {
    // 화면 초기화
    data object Init: EmployeeManageEvent

    // 직원 검색 필드 값 변경 이벤트
    data class ChangedSearchWith(
        val value: String
    ): EmployeeManageEvent

    // 직원 검색 버튼 클릭 이벤트
    data object ClickedSearch: EmployeeManageEvent

    // 직원 검색어 초기화 버튼 클릭 이벤트
    data object ClickedInitSearch: EmployeeManageEvent

    // 정보 조회할 직원 선택 이벤트
    data class SelectedEmployeeWith(
        val target: HrTarget,
        val userId: String
    ): EmployeeManageEvent

    // 드롭다운 선택 이벤트
    data class SelectedDropDownWith(
        val field: DropDownField,
        val value: String
    ): EmployeeManageEvent
}