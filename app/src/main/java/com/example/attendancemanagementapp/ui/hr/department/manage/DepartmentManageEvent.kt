package com.example.attendancemanagementapp.ui.hr.department.manage

import com.example.attendancemanagementapp.data.dto.DepartmentDTO

sealed interface DepartmentManageEvent {
    // 정보 조회할 부서 선택 이벤트
    data class SelectedDepartmentWith(
        val departmentId: String
    ): DepartmentManageEvent

    // 부서 위치 이동 이벤트
    data class MoveDepartmentWith(
        val fromDepartment: DepartmentDTO.DepartmentsInfo,
        val endDepartment: DepartmentDTO.DepartmentsInfo
    ): DepartmentManageEvent
}