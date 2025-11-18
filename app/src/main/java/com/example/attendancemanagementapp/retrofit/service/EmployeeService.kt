package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface EmployeeService {
    // 직원 목록 조회 및 검색
    @GET("/api/users")
    suspend fun getEmployees(
        @Query("userNm") name: String
    ): List<EmployeeDTO.EmployeesInfo>

    // 새로운 직원 등록
    @POST("/api/users")
    suspend fun addEmployee(
        @Body request: EmployeeDTO.AddEmployeeRequest
    ): EmployeeDTO.EmployeeInfo

    // 직원 정보 수정
    @PUT("/api/users")
    suspend fun updateEmployee(
        @Body request: EmployeeDTO.UpdateEmployeeRequest
    ): EmployeeDTO.EmployeeInfo

    // 직원 정보 상세 조회
    @GET("/api/users/{userId}")
    suspend fun getEmployeeDetail(
        @Path("userId") userId: String
    ): EmployeeDTO.EmployeeInfo

    // 직원 복구 (활성화)
    @PATCH("/api/users/{userId}/activate")
    suspend fun setActivate(
        @Path("userId") userId: String
    ): EmployeeDTO.EmployeeInfo

    // 직원 탈퇴 (비활성화)
    @PATCH("/api/users/{userId}/deactivate")
    suspend fun setDeactivate(
        @Path("userId") userId: String
    ): EmployeeDTO.EmployeeInfo

    // 내 정보 조회
    @GET("/api/users/my-info")
    suspend fun getMyInfo(): EmployeeDTO.GetMyInfoResponse

    // 내 정보 수정
    @PUT("/api/users/my-info")
    suspend fun updateMyInfo(
        @Body request: EmployeeDTO.UpdateMyInfoRequest
    ): EmployeeDTO.GetMyInfoResponse

    // 비밀번호 초기화
    @PATCH("/api/users/password-reset")
    suspend fun resetPassword(
        @Body request: EmployeeDTO.ResetPasswordRequest
    ): String

    // 직원 관리 목록 조회 및 검색
    @GET("/api/users/search")
    suspend fun getManageEmployees(
        @Query("deptNm") department: String,
        @Query("clsf") grade: String,
        @Query("rspofc") title: String,
        @Query("userNm") name: String,
        @Query("page") page: Int? = null,  // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null   // 페이지 당 데이터 개수
    ): EmployeeDTO.GetManageEmployeesResponse
}