package com.example.attendancemanagementapp.ui.hr.department.add

import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentField

sealed interface DepartmentAddEvent {
    // 초기화
    data object Init: DepartmentAddEvent

    // 부서 정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: DepartmentField,
        val value: String
    ): DepartmentAddEvent
}