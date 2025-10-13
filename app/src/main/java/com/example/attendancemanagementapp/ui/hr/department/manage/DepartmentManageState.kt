package com.example.attendancemanagementapp.ui.hr.department.manage

import com.example.attendancemanagementapp.data.dto.HrDTO

data class DepartmentManageState(
    val departments: List<HrDTO.DepartmentsInfo> = listOf(HrDTO.DepartmentsInfo(name = "부서", depth = 0))    // 부서 목록
)