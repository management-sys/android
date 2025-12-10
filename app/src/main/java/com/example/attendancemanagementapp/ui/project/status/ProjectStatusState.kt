package com.example.attendancemanagementapp.ui.project.status

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO.ProjectStatusInfo
import com.example.attendancemanagementapp.data.param.ProjectStatusQuery
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class ProjectStatusState(
    val departments: List<DepartmentDTO.DepartmentsInfo> = emptyList(), // 부서 목록
    val filter: ProjectStatusQuery = ProjectStatusQuery(),              // 검색 조건
    val cntStatus: ProjectStatusCnt = ProjectStatusCnt(),               // 프로젝트 상태별 개수
    val projects: List<ProjectStatusInfo> = emptyList(),                // 프로젝트 목록
    val paginationState: PaginationState = PaginationState()            // 페이지네이션 상태
)

/* 프로젝트 상태별 개수 데이터 */
data class ProjectStatusCnt(
    val total: Int = 0,
    val inProgress: Int = 0,
    val notStarted: Int = 0,
    val completed: Int = 0
)