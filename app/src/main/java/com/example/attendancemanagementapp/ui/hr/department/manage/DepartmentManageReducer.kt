package com.example.attendancemanagementapp.ui.hr.department.manage

import android.util.Log
import com.example.attendancemanagementapp.data.dto.DepartmentDTO

object DepartmentManageReducer {
    fun reduce(s: DepartmentManageState, e: DepartmentManageEvent): DepartmentManageState = when (e) {
        is DepartmentManageEvent.MoveDepartmentWith -> handleMoveDepartment(s, e.fromDepartment, e.endDepartment)
        else -> s
    }

    private fun handleMoveDepartment(
        state: DepartmentManageState,
        fromDepartment: DepartmentDTO.DepartmentsInfo,
        endDepartment: DepartmentDTO.DepartmentsInfo
    ): DepartmentManageState {
        val updatedDepartments = state.departments.map { dept ->
            when (dept.id) {
                fromDepartment.id -> dept.copy(
                    depth = endDepartment.depth,
                    order = endDepartment.order,
                    upperId = endDepartment.upperId
                )
                endDepartment.id -> dept.copy(
                    depth = fromDepartment.depth,
                    order = fromDepartment.order,
                    upperId = fromDepartment.upperId
                )
                else -> dept
            }
        }.sortedWith(compareBy({ it.depth }, { it.order }))
        Log.d("부서 이동", "변경된 부서 목록\n${updatedDepartments}")

        return state.copy(departments = updatedDepartments)
    }
}