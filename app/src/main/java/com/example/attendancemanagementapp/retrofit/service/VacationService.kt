package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.VacationDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface VacationService {
    // 휴가 신청
    @POST("/api/vcatns")
    suspend fun addVacation(
        @Body request: VacationDTO.AddVacationRequest
    ): VacationDTO.GetVacationResponse

    // 휴가 신청 상세 조회
    @GET("/api/vcatns/{vcatnId}")
    suspend fun getVacation(
        @Path("vcatnId") vacationId: String
    ): VacationDTO.GetVacationResponse

    // 휴가 신청 삭제
    @DELETE("/api/vcatns/{vcatnId}")
    suspend fun deleteVacation(
        @Path("vcatnId") vacationId: String
    )

    // 휴가 신청 취소
    @PUT("/api/vcatns/{vcatnId}/cancel")
    suspend fun cancelVacation(
        @Path("vcatnId") vacationId: String
    ): VacationDTO.GetVacationResponse

    // 이전 승인자 불러오기
    @GET("/api/vcatns/latest-confmers/{userId}")
    suspend fun getPrevApprovers(
        @Path("userId") userId: String
    ): VacationDTO.GetPrevApproversResponse

    // 휴가 현황 목록 조회
    @GET("/api/vcatns/users/{userId}")
    suspend fun getVacations(
        @Path("userId") userId: String,
        @Query("year") year: Int?,          // 연차 년도, 없으면 현재 연차 기준으로 조회
        @Query("filter") filter: String,    // total(전체), minus(마이너스 이월), used(사용 연차, 반차), official(공가), sick(병가), special(특별휴가)
        @Query("page") page: Int? = null,   // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null,   // 페이지 당 데이터 개수
    ): VacationDTO.GetVacationsResponse
}