package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.retrofit.service.ProjectService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProjectRepository @Inject constructor(private val service: ProjectService) {
    // 프로젝트 등록
    fun addProject(request: ProjectDTO.AddProjectRequest): Flow<Result<ProjectDTO.ProjectInfo>> = flow {
        val response = service.addProject(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 프로젝트 현황 조회
    fun getProjectStatus(projectId: String): Flow<Result<ProjectDTO.GetProjectResponse>> = flow {
        val response = service.getProject(
            projectId = projectId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 프로젝트 투입 인력 목록 조회
    fun getPersonnel(projectId: String, page: Int): Flow<Result<ProjectDTO.GetPersonnelResponse>> = flow {
        val response = service.getPersonnel(
            projectId = projectId,
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}