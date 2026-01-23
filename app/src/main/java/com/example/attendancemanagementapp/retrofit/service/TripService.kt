package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.ApproverDTO
import com.example.attendancemanagementapp.data.dto.TripDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TripService {
    // 출장 신청
    @POST("/api/bsrps")
    suspend fun addTrip(
        @Body request: TripDTO.AddTripRequest
    )

    // 출장 현황 상세 조회
    @GET("/api/bsrps/{bsrpId}")
    suspend fun getTrip(
        @Path("bsrpId")  id: String
    ): TripDTO.GetTripResponse

    // 이전 승인자 불러오기
    @GET("/api/bsrps/latest-confmers")
    suspend fun getPrevApprovers(): ApproverDTO.GetPrevApproversResponse
}