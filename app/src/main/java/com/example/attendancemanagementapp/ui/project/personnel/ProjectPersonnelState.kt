package com.example.attendancemanagementapp.ui.project.personnel

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.data.param.PersonnelsQuery
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class ProjectPersonnelState(
    val personnels: List<ProjectDTO.PersonnelInfo> = emptyList(),       // 투입 현황 목록
    val filter: PersonnelsQuery = PersonnelsQuery(),                    // 검색 조건
    val departments: List<DepartmentDTO.DepartmentsInfo> = emptyList(), // 부서 목록
    val paginationState: PaginationState = PaginationState()            // 페이지네이션 상태
)