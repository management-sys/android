package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.retrofit.RetrofitInstance
import com.example.attendancemanagementapp.retrofit.service.DepartmentService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DepartmentRepository @Inject constructor() {
    private val service: DepartmentService = RetrofitInstance.retrofit.create(DepartmentService::class.java)

    // 부서 목록 조회
    fun getDepartments(): Flow<Result<List<DepartmentDTO.DepartmentsInfo>>> = flow {
        val response = service.getDepartments()
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 부서 정보 상세 조회
    fun getDepartmentDetail(departmentId: String): Flow<Result<DepartmentDTO.DepartmentInfo>> = flow {
        val response = service.getDepartmentDetail(
            departmentId = departmentId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}