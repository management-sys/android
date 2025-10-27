package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface DepartmentService {
    // 부서 목록 조회 및 검색 (페이징 O)
    @GET("/api/depts")
    suspend fun getDepartments(
        @Query("searchDeptNm")  searchName: String? = null,
        @Query("page")          page: Int? = null,          // 페이지 번호: 0부터 시작
        @Query("size")          size: Int? = null           // 페이지 당 데이터 개수
    ): DepartmentDTO.GetDepartmentsResponse

    // 부서 정보 상세 조회
    @GET("/api/depts/{deptId}")
    suspend fun getDepartmentDetail(
        @Path("deptId") departmentId: String
    ): DepartmentDTO.DepartmentInfo

    // 부서 정보 수정
    @PATCH("/api/depts/{deptId}")
    suspend fun updateDepartment(
        @Path("deptId") departmentId: String,
        @Body request: DepartmentDTO.UpdateDepartmentRequest
    ): DepartmentDTO.DepartmentInfo

    // 전체 부서 목록 조회 (페이징 X)
    @GET("/api/depts/all")
    suspend fun getAllDepartments(): List<DepartmentDTO.DepartmentsInfo>
}