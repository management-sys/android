package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.retrofit.service.EmployeeService
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
}