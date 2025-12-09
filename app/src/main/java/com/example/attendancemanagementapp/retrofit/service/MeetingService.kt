package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.MeetingDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MeetingService {
    // 회의록 목록 조회 및 검색
    @GET("/api/mtgs")
    suspend fun getMeetings(
        @Query("bgnde") startDate: String,      // 시작일
        @Query("endde") endDate: String,        // 종료일
        @Query("keyword") searchText: String,   // 검색어
        @Query("keywordType") type: String,     // 검색 타입 (all: 전체, prjctNm: 프로젝트명, sj: 회의명)
        @Query("page") page: Int? = null,       // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null        // 페이지 당 데이터 개수
    ): MeetingDTO.GetMeetingsResponse

    // 회의록 등록
    @POST("/api/mtgs")
    suspend fun addMeeting(
        @Body request: MeetingDTO.AddMeetingRequest
    ): MeetingDTO.GetMeetingResponse

    // 회의록 상세 조회
    @GET("/api/mtgs/{mtgRcordId}")
    suspend fun getMeeting(
        @Path("mtgRcordId") meetingId: Long
    ): MeetingDTO.GetMeetingResponse

    // 회의록 수정
    @PUT("/api/mtgs/{mtgRcordId}")
    suspend fun updateMeeting(
        @Path("mtgRcordId") meetingId: Long,
        @Body request: MeetingDTO.UpdateMeetingRequest
    ): MeetingDTO.GetMeetingResponse

    // 회의록 삭제
    @DELETE("/api/mtgs/{mtgRcordId}")
    suspend fun deleteMeeting(
        @Path("mtgRcordId") meetingId: Long
    )
}