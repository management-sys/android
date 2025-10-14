package com.example.attendancemanagementapp.ui.hr.employee.detail

sealed interface EmployeeDetailEvent {
    // 비밀번호 초기화 버튼 클릭 이벤트
    data object ClickedResetPassword: EmployeeDetailEvent

    // 직원 탈퇴 버튼 클릭 이벤트
    data object ClickedDeactivate: EmployeeDetailEvent

    // 직원 탈퇴 취소 버튼 클릭 이벤트
    data object ClickedDismissDeactivate: EmployeeDetailEvent

    // 직원 복구 버튼 클릭 이벤트
    data object ClickedActivate: EmployeeDetailEvent

    // 직원 복구 취소 버튼 클릭 이벤트
    data object ClickedDismissActivate: EmployeeDetailEvent
}