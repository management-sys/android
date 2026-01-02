package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.data.param.PersonnelsQuery
import com.example.attendancemanagementapp.data.param.ProjectStatusQuery
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

    // 프로젝트 상세 조회
    fun getProject(projectId: String): Flow<Result<ProjectDTO.GetProjectResponse>> = flow {
        val response = service.getProject(
            projectId = projectId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 프로젝트 수정
    fun updateProject(projectId: String, request: ProjectDTO.UpdateProjectRequest): Flow<Result<ProjectDTO.GetProjectResponse>> = flow {
        val response = service.updateProject(
            projectId = projectId,
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 프로젝트 삭제
    fun deleteProject(projectId: String): Flow<Result<Unit>> = flow {
        val response = service.deleteProject(projectId = projectId)
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 프로젝트 중단
    fun stopProject(projectId: String): Flow<Result<Unit>> = flow {
        val response = service.stopProject(projectId = projectId)
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 프로젝트 투입 인력 목록 조회
    fun getPersonnel(projectId: String, userName: String, page: Int): Flow<Result<ProjectDTO.GetPersonnelResponse>> = flow {
        val response = service.getPersonnel(
            projectId = projectId,
            userName = userName,
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 프로젝트 현황 조회
    fun getProjectStatus(query: ProjectStatusQuery, page: Int): Flow<Result<ProjectDTO.GetProjectStatusResponse>> = flow {
        val response = service.getProjectStatus(
            year = if (query.year == 0) null else query.year,
            month = if (query.month == 0) null else query.month,
            departmentId = query.departmentId,
            type = query.searchType.toKey(),
            searchText = query.searchText,
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 투입 현황 조회
    fun getPersonnels(query: PersonnelsQuery, page: Int): Flow<Result<ProjectDTO.GetPersonnelsResponse>> = flow {
        val response = service.getPersonnels(
            departmentId = query.departmentId,
            userName = query.userName,
            year = if (query.year == 0) null else query.year,
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 투입 현황 상세 조회
    fun getPersonnelDetail(userId: String): Flow<Result<ProjectDTO.GetPersonnelDetailResponse>> = flow {
        val response = service.getPersonnelDetail(
            userId = userId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}