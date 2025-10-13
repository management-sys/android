package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface EmployeeService {
    // 직원 목록 조회 및 검색
    @GET("/api/users")
    suspend fun getEmployees(
        @Query("userNm") name: String
    ): List<EmployeeDTO.EmployeesInfo>

    // 직원 정보 상세 조회
    @GET("/api/users/{userId}")
    suspend fun getEmployeeDetail(
        @Path("userId") userId: String
    ): EmployeeDTO.EmployeeInfo

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

    // 직원 정보 수정
    @PUT("/api/users")
    suspend fun updateEmployee(
        @Body request: EmployeeDTO.UpdateEmployeeRequest
    ): EmployeeDTO.EmployeeInfo
}