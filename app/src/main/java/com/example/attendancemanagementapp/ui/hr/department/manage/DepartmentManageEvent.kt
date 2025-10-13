package com.example.attendancemanagementapp.ui.hr.department.manage

sealed interface DepartmentManageEvent {
    // 정보 조회할 부서 선택 이벤트
    data class SelectedDepartmentWith(
        val departmentId: String
    ): DepartmentManageEvent
}