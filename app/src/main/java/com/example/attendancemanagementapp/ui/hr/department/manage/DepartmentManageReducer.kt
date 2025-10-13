package com.example.attendancemanagementapp.ui.hr.department.manage

object DepartmentManageReducer {
    fun reduce(s: DepartmentManageState, e: DepartmentManageEvent): DepartmentManageState = when (e) {
        is DepartmentManageEvent.SelectedDepartmentWith -> s
    }
}