package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.retrofit.service.DepartmentService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DepartmentRepository @Inject constructor(private val service: DepartmentService) {
    // 전체 부서 목록 조회
    fun getAllDepartments(): Flow<Result<List<DepartmentDTO.DepartmentsInfo>>> = flow {
        val response = service.getAllDepartments()
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 부서 목록 조회 및 검색
    fun getDepartments(searchName: String, page: Int): Flow<Result<DepartmentDTO.GetDepartmentsResponse>> = flow {
        val response = service.getDepartments(
            searchName = searchName,
            page = page
        )
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

    // 부서 정보 수정
    fun updateDepartment(departmentId: String, request: DepartmentDTO.UpdateDepartmentRequest): Flow<Result<DepartmentDTO.DepartmentInfo>> = flow {
        val response = service.updateDepartment(
            departmentId = departmentId,
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 부서 위치 변경
    fun updatePosition(departmentId: String, request: DepartmentDTO.UpdatePositionRequest): Flow<Result<List<DepartmentDTO.DepartmentsInfo>>> = flow {
        val response = service.updatePosition(departmentId, request)
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 새로운 부서 등록
    fun addDepartment(request: DepartmentDTO.AddDepartmentRequest): Flow<Result<List<DepartmentDTO.DepartmentsInfo>>> = flow {
        val response = service.addDepartment(request)
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 부서 삭제
    fun deleteDepartment(departmentId: String): Flow<Result<Unit>> = flow {
        val response = service.deleteDepartment(departmentId)
        emit(Result.success(Unit))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 부서 사용자 정보 저장
    fun updateDepartmentUser(request: DepartmentDTO.UpdateDepartmentUserRequest): Flow<Result<DepartmentDTO.DepartmentInfo>> = flow {
        val response = service.updateDepartmentUser(request)
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}