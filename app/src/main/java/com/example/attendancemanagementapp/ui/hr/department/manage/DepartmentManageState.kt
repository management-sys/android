package com.example.attendancemanagementapp.ui.hr.department.manage

import com.example.attendancemanagementapp.data.dto.DepartmentDTO

data class DepartmentManageState(
    val departments: List<DepartmentDTO.DepartmentsInfo> = listOf(DepartmentDTO.DepartmentsInfo())  // 부서 목록
)