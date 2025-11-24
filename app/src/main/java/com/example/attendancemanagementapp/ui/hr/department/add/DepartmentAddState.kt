package com.example.attendancemanagementapp.ui.hr.department.add

import com.example.attendancemanagementapp.data.dto.DepartmentDTO

data class DepartmentAddState(
    val inputData: DepartmentDTO.AddDepartmentRequest = DepartmentDTO.AddDepartmentRequest(),   // 입력한 부서 정보
    val upperName: String = ""                                                                  // 상위부서 이름
)