package com.example.attendancemanagementapp.ui.hr.employee.detail

object EmployeeDetailReducer {
    fun reduce(s: EmployeeDetailState, e: EmployeeDetailEvent): EmployeeDetailState = when (e) {
        EmployeeDetailEvent.ClickedResetPassword -> s
        EmployeeDetailEvent.ClickedDeactivate -> s
        EmployeeDetailEvent.ClickedDismissDeactivate -> s
        EmployeeDetailEvent.ClickedActivate -> s
        EmployeeDetailEvent.ClickedDismissActivate -> s
    }
}