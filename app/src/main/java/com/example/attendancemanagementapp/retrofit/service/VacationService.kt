package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.VacationDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
}