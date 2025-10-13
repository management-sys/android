package com.example.attendancemanagementapp.retrofit

import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.data.dto.HrDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface JsonService {
    // 공통코드 목록 조회 및 검색
    @GET("/api/codes")
    suspend fun getCommonCodes(
        @Query("searchType") searchType: String,
        @Query("searchKeyword") searchKeyword: String? = null,
        @Query("page") page: Int? = null,  // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null   // 페이지 당 데이터 개수
    ): CommonCodeDTO.GetCommonCodesResponse

    // 공통코드 상세 조회
    @GET("/api/codes/{code}")
    suspend fun getCommonCodeDetail(
        @Path("code") code: String
    ): CommonCodeDTO.CommonCodeInfo

    // 공통코드 등록
    @POST("/api/codes")
    suspend fun addCommonCode(
        @Body request: CommonCodeDTO.AddUpdateCommonCodeRequest
    ): CommonCodeDTO.AddUpdateCommonCodeResponse

    // 공통코드 수정
    @PUT("/api/codes")
    suspend fun updateCommonCode(
        @Body request: CommonCodeDTO.AddUpdateCommonCodeRequest
    ): CommonCodeDTO.AddUpdateCommonCodeResponse

    // 공통코드 삭제
    @DELETE("/api/codes/{code}")
    suspend fun deleteCommonCode(
        @Path("code") code: String
    ): CommonCodeDTO.DeleteCommonCodeResponse

    // 직원 목록 조회 및 검색
    @GET("/api/users")
    suspend fun getEmployees(
        @Query("userNm") name: String
    ): List<HrDTO.EmployeesInfo>

    // 직원 정보 상세 조회
    @GET("/api/users/{userId}")
    suspend fun getEmployeeDetail(
        @Path("userId") userId: String
    ): HrDTO.EmployeeInfo

    // 직원 관리 목록 조회 및 검색
    @GET("/api/users/search")
    suspend fun getManageEmployees(
        @Query("deptNm") department: String,
        @Query("clsf") grade: String,
        @Query("rspofc") title: String,
        @Query("userNm") name: String,
        @Query("page") page: Int? = null,  // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null   // 페이지 당 데이터 개수
    ): HrDTO.GetManageEmployeesResponse

    // 직원 정보 수정
    @PUT("/api/users")
    suspend fun updateEmployee(
        @Body request: HrDTO.UpdateEmployeeRequest
    ): HrDTO.EmployeeInfo

    // 부서 목록 조회
    @GET("/api/depts")
    suspend fun getDepartments(): List<HrDTO.DepartmentsInfo>

    // 부서 정보 상세 조회
    @GET("/api/depts/{deptId}")
    suspend fun getDepartmentDetail(
        @Path("deptId") departmentId: String
    ): HrDTO.DepartmentInfo

    // 권한 목록 조회
    @GET("/api/authors")
    suspend fun getAuthors(): List<AuthorDTO.GetAuthorsResponse>
}