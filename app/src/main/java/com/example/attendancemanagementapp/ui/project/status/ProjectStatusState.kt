package com.example.attendancemanagementapp.ui.project.status

import com.example.attendancemanagementapp.data.dto.DepartmentDTO

data class ProjectStatusState(
    val departments: List<DepartmentDTO.DepartmentsInfo> = emptyList(),  // 부서 목록
    val selectedYear: String = "전체",                                    // 선택한 연도
    val selectedMonth: String = "전체",                                   // 선택한 월
    val selectedDepartment: String = "부서",                              // 선택한 부서
    val selectedKeyword: String = "전체",                                 // 선택한 검색 키워드 (전체/프로젝트명/PM)
    val searchText: String = "",                                         // 검색어
    val selectedProjectId: String = ""                                  // 선택한 프로젝트 아이디
)