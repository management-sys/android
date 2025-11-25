package com.example.attendancemanagementapp.ui.project.add

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO

enum class ProjectAddField {
    PROJECT_NAME, COMPANY_NAME, DEPARTMENT_ID, MANAGER_ID, BUSINESS_EXPENSE, MEETING_EXPENSE,
    BUSINESS_START, BUSINESS_END, PLAN_START, PLAN_END, REAL_START, REAL_END, REMARK
}

sealed interface ProjectAddEvent {
    // 초기화
    data object Init: ProjectAddEvent

    // 프로젝트 정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: ProjectAddField,
        val value: String
    ): ProjectAddEvent

    // 구분 선택 이벤트
    data class SelectedTypeWith(
        val type: String
    ): ProjectAddEvent

    // 등록 버튼 클릭 이벤트
    data object ClickedAdd: ProjectAddEvent

    // 담당부서 선택 이벤트
    data class SelectedDepartmentWith(
        val department: DepartmentDTO.DepartmentsInfo
    ): ProjectAddEvent

    // 프로젝트 책임자 선택 이벤트
    data class SelectedManagerWith(
        val manager: EmployeeDTO.EmployeesInfo
    ): ProjectAddEvent

    // 투입인력 체크박스 클릭 이벤트
    data class CheckedAssignedPersonnelWith(
        val checked: Boolean,
        val employee: EmployeeDTO.EmployeesInfo
    ): ProjectAddEvent
}