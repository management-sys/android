package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.ProjectDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface ProjectService {
    // 프로젝트 등록
    @POST("/api/prjcts")
    suspend fun addProject(
        @Body request: ProjectDTO.AddProjectRequest
    ): ProjectDTO.ProjectInfo
}