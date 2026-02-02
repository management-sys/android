package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.ApproverDTO
import com.example.attendancemanagementapp.data.dto.TripDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface TripService {
    // 출장 현황 목록 조회
    @GET("/api/bsrps")
    suspend fun getTrips(
        @Query("year") year: Int?,          // 연차 년도, 없으면 현재 연차 기준으로 조회
        @Query("filter") filter: String,    // 출장 구분: 국내, 국외
        @Query("page") page: Int? = null,   // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null,   // 페이지 당 데이터 개수
    ): TripDTO.GetTripsResponse

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

    // 출장 품의서 수정
    @PUT("/api/bsrps/{bsrpId}")
    suspend fun updateTrip(
        @Path("bsrpId") id: String,
        @Body request: TripDTO.UpdateTripRequest
    ): TripDTO.GetTripResponse

    // 출장 신청 삭제
    @DELETE("/api/bsrps/{bsrpId}")
    suspend fun deleteTrip(
        @Path("bsrpId") id: String
    )

    // 출장 신청 취소
    @PUT("/api/bsrps/{bsrpId}/cancel")
    suspend fun cancelTrip(
        @Path("bsrpId") id: String
    ): TripDTO.GetTripResponse

    // 출장 품의서 다운로드(PDF)
    @Streaming  // InputStream에 저장
    @GET("/api/bsrps/{bsrpId}/download/pdf")
    suspend fun downloadTripPdf(
        @Path("bsrpId") id: String
    ): Response<ResponseBody>

    // 이전 승인자 불러오기
    @GET("/api/bsrps/latest-confmers")
    suspend fun getPrevApprovers(): ApproverDTO.GetPrevApproversResponse

    // 출장 복명서 등록
    @POST("/api/bsrp-rports")
    suspend fun addTripReport(
        @Body request: TripDTO.AddTripReportRequest
    )

    // 출장 복명서 조회
    @GET("/api/bsrp-rports/{bsrpId}")
    suspend fun getTripReport(
        @Path("bsrpId") id: String
    ): TripDTO.GetTripReportResponse

    @PUT("/api/bsrp-rports/{bsrpId}")
    suspend fun updateTripReport(
        @Path("bsrpId") id: String,
        @Body request: TripDTO.UpdateTripReportRequest
    ): TripDTO.GetTripReportResponse

    // 출장 복명서 삭제
    @DELETE("/api/bsrp-rports/{bsrpId}")
    suspend fun deleteTripReport(
        @Path("bsrpId") id: String
    )

    // 출장 복명서 취소
    @PUT("/api/bsrp-rports/{bsrpId}/cancel")
    suspend fun cancelTripReport(
        @Path("bsrpId") id: String
    ): TripDTO.GetTripReportResponse

    // 출장 복명서 다운로드(PDF)
    @Streaming  // InputStream에 저장
    @GET("/api/bsrp-rports/{bsrpId}/download/pdf")
    suspend fun downloadTripReportPdf(
        @Path("bsrpId") id: String
    ): Response<ResponseBody>

    // 이전 승인자 불러오기 (복명서)
    @GET("/api/bsrp-rports/latest-confmers")
    suspend fun getReportPrevApprovers(): ApproverDTO.GetPrevApproversResponse
}