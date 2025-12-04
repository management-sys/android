package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.ProjectDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProjectService {
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

    // 프로젝트 투입 인력 목록 조회
    @GET("/api/prjcts/{prjctId}/users")
    suspend fun getPersonnel(
        @Path("prjctId") projectId: String,
        @Query("page") page: Int? = null,  // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null   // 페이지 당 데이터 개수
    ): ProjectDTO.GetPersonnelResponse
}