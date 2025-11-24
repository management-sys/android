package com.example.attendancemanagementapp.ui.hr.department.manage

import com.example.attendancemanagementapp.data.dto.DepartmentDTO

data class DepartmentManageState(
    val departments: List<DepartmentDTO.DepartmentsInfo> = listOf(DepartmentDTO.DepartmentsInfo()), // 부서 목록
    val departmentMap: Map<String?, List<DepartmentDTO.DepartmentsInfo>> = emptyMap()               // 상위부서 아이디, 하위부서
)