package com.example.attendancemanagementapp.ui.project.detail

import com.example.attendancemanagementapp.data.dto.ProjectDTO

data class ProjectDetailState(
    val projectInfo: ProjectDTO.GetProjectResponse = ProjectDTO.GetProjectResponse()    // 프로젝트 정보
)