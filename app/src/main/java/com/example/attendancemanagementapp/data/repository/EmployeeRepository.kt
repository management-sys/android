package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.retrofit.RetrofitInstance
import com.example.attendancemanagementapp.retrofit.service.EmployeeService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EmployeeRepository @Inject constructor() {
    private val service = RetrofitInstance.employeeService

    // 직원 목록 조회 및 검색
    fun getEmployees(searchKeyword: String): Flow<Result<List<EmployeeDTO.EmployeesInfo>>> = flow {
        val response = service.getEmployees(
            name = searchKeyword
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 새로운 직원 등록
    fun addEmployee(request: EmployeeDTO.AddEmployeeRequest): Flow<Result<EmployeeDTO.EmployeeInfo>> = flow {
        val response = service.addEmployee(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 직원 정보 수정
    fun updateEmployee(request: EmployeeDTO.UpdateEmployeeRequest): Flow<Result<EmployeeDTO.EmployeeInfo>> = flow {
        val response = service.updateEmployee(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 직원 정보 상세 조회
    fun getEmployeeDetail(userId: String): Flow<Result<EmployeeDTO.EmployeeInfo>> = flow {
        val response = service.getEmployeeDetail(
            userId = userId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 직원 복구 (활성화)
    fun setActivate(userId: String): Flow<Result<EmployeeDTO.EmployeeInfo>> = flow {
        val response = service.setActivate(
            userId = userId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 직원 탈퇴 (비활성화)
    fun setDeactivate(userId: String): Flow<Result<EmployeeDTO.EmployeeInfo>> = flow {
        val response = service.setDeactivate(
            userId = userId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 비밀번호 초기화
    fun resetPassword(request: EmployeeDTO.ResetPasswordRequest): Flow<Result<String>> = flow {
        val response = service.resetPassword(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 직원 관리 목록 조회 및 검색 (직원 목록 고급 조회)
    fun getManageEmployees(department: String, grade: String, title: String, name: String, page: Int): Flow<Result<EmployeeDTO.GetManageEmployeesResponse>> = flow {
        val response = service.getManageEmployees(
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
}