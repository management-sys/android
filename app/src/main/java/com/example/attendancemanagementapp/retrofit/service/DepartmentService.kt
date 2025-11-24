package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
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

    // 새로운 부서 등록
    @POST("/api/depts")
    suspend fun addDepartment(
        @Body request: DepartmentDTO.AddDepartmentRequest
    ): List<DepartmentDTO.DepartmentsInfo>

    // 부서 정보 상세 조회
    @GET("/api/depts/{deptId}")
    suspend fun getDepartmentDetail(
        @Path("deptId") departmentId: String
    ): DepartmentDTO.DepartmentInfo

    // 부서 삭제
    @DELETE("/api/depts/{deptId}")
    suspend fun deleteDepartment(
        @Path("deptId") departmentId: String
    )

    // 부서 정보 수정
    @PATCH("/api/depts/{deptId}")
    suspend fun updateDepartment(
        @Path("deptId") departmentId: String,
        @Body request: DepartmentDTO.UpdateDepartmentRequest
    ): DepartmentDTO.DepartmentInfo

    // 부서 위치 변경
    @PATCH("/api/depts/{deptId}/position")
    suspend fun updatePosition(
        @Path("deptId") departmentId: String,
        @Body request: DepartmentDTO.UpdatePositionRequest
    ): List<DepartmentDTO.DepartmentsInfo>

    // 전체 부서 목록 조회 (페이징 X)
    @GET("/api/depts/all")
    suspend fun getAllDepartments(): List<DepartmentDTO.DepartmentsInfo>

    // 부서 사용자 정보 저장
    @POST("/api/depts/save-users")
    suspend fun updateDepartmentUser(
        @Body request: DepartmentDTO.UpdateDepartmentUserRequest
    ): DepartmentDTO.DepartmentInfo
}