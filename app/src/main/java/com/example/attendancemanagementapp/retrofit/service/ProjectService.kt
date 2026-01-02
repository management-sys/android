package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.ProjectDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProjectService {
    // 프로젝트 현황 조회
    @GET("/api/prjcts")
    suspend fun getProjectStatus(
        @Query("year") year: Int?,              // 년
        @Query("month") month: Int?,            // 월
        @Query("deptId") departmentId: String,  // 부서 아이디
        @Query("searchType") type: String,      // 검색 타입 (all: 전체, prjctNm: 프로젝트명, PM: 프로젝트 책임자명)
        @Query("keyword") searchText: String,   // 검색어
        @Query("page") page: Int? = null,       // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null        // 페이지 당 데이터 개수
    ): ProjectDTO.GetProjectStatusResponse

    // 프로젝트 등록
    @POST("/api/prjcts")
    suspend fun addProject(
        @Body request: ProjectDTO.AddProjectRequest
    ): ProjectDTO.ProjectInfo

    // 프로젝트 상세 조회
    @GET("/api/prjcts/{prjctId}")
    suspend fun getProject(
        @Path("prjctId") projectId: String
    ): ProjectDTO.GetProjectResponse

    // 프로젝트 수정
    @PUT("/api/prjcts/{prjctId}")
    suspend fun updateProject(
        @Path("prjctId") projectId: String,
        @Body request: ProjectDTO.UpdateProjectRequest
    ): ProjectDTO.GetProjectResponse

    // 프로젝트 삭제
    @DELETE("/api/prjcts/{prjctId}")
    suspend fun deleteProject(
        @Path("prjctId") projectId: String
    )

    // 프로젝트 중단
    @PATCH("/api/prjcts/{prjctId}")
    suspend fun stopProject(
        @Path("prjctId") projectId: String
    )

    // 프로젝트 투입 인력 목록 조회
    @GET("/api/prjcts/{prjctId}/users")
    suspend fun getPersonnel(
        @Path("prjctId") projectId: String,
        @Query("userNm") userName: String,
        @Query("page") page: Int? = null,  // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null   // 페이지 당 데이터 개수
    ): ProjectDTO.GetPersonnelResponse

    // 투입 현황 조회
    @GET("/api/prjcts/user-inpt")
    suspend fun getPersonnels(
        @Query("deptId") departmentId: String,  // 부서 아이디
        @Query("userNm") userName: String,      // 사용자 이름
        @Query("year") year: Int?,              // 검색 연도 (프로젝트 사업 시작일 기준)
        @Query("page") page: Int? = null,       // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null        // 페이지 당 데이터 개수
    ): ProjectDTO.GetPersonnelsResponse

    // 투입 현황 상세 조회
    @GET("/api/prjcts/user-inpt/{userId}")
    suspend fun getPersonnelDetail(
        @Path("userId") userId: String
    ): ProjectDTO.GetPersonnelDetailResponse
}