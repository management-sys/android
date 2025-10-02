package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.HrDTO
import com.example.attendancemanagementapp.retrofit.JsonService
import com.example.attendancemanagementapp.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HrRepository @Inject constructor() {
    private val jsonService: JsonService = RetrofitInstance.retrofit.create(JsonService::class.java)

    // 직원 목록 조회 및 검색
    fun getEmployees(searchKeyword: String): Flow<Result<List<HrDTO.EmployeesInfo>>> = flow {
        val response = jsonService.getEmployees(
            name = searchKeyword
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 직원 정보 상세 조회
    fun getEmployeeDetail(userId: String): Flow<Result<HrDTO.EmployeeInfo>> = flow {
        val response = jsonService.getEmployeeDetail(
            userId = userId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 직원 관리 목록 조회 및 검색
    fun getManageEmployees(department: String, grade: String, title: String, name: String, page: Int): Flow<Result<HrDTO.GetManageEmployeesResponse>> = flow {
        val response = jsonService.getManageEmployees(
            department = if (department == "부서") "" else department,
            grade = if (grade == "직급") "" else grade,
            title = if (title == "직책") "" else title,
            name = name,
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 직원 정보 수정
    fun updateEmployee(request: HrDTO.UpdateEmployeeRequest): Flow<Result<HrDTO.EmployeeInfo>> = flow {
        val response = jsonService.updateEmployee(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 부서 목록 조회
    fun getDepartments(): Flow<Result<List<HrDTO.DepartmentsInfo>>> = flow {
        val response = jsonService.getDepartments()
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 권한 목록 조회
    fun getAuthors(): Flow<Result<List<AuthorDTO.GetAuthorsResponse>>> = flow {
        val response = jsonService.getAuthors()
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}