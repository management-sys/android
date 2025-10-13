package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CommonCodeService {
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
}