package com.example.attendancemanagementapp.ui.hr.department.manage

import com.example.attendancemanagementapp.data.dto.DepartmentDTO

data class DepartmentManageState(
    val departments: List<DepartmentDTO.DepartmentsInfo> = listOf(DepartmentDTO.DepartmentsInfo(name = "부서", depth = 0))    // 부서 목록
)