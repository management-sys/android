package com.example.attendancemanagementapp.ui.home.project

import com.example.attendancemanagementapp.data.dto.ProjectDTO.ProjectStatusInfo

data class ProjectState(
    val projects: List<ProjectStatusInfo> = emptyList(),    // 프로젝트 목록
)