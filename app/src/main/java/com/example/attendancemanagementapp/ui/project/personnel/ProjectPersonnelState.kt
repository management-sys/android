package com.example.attendancemanagementapp.ui.project.personnel

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class ProjectPersonnelState(
    val personnels: List<ProjectDTO.PersonnelInfo> = emptyList(),       // 투입 현황 목록
    val searchText: String = "",                                        // 검색어
    val selectedKeyword: String = "전체",                                // 선택한 검색 키워드 (전체/연도/참여자 이름)
    val selectedDepartment: String = "부서",                             // 선택한 부서
    val departments: List<DepartmentDTO.DepartmentsInfo> = emptyList(), // 부서 목록
    val paginationState: PaginationState = PaginationState()            // 페이지네이션 상태
)