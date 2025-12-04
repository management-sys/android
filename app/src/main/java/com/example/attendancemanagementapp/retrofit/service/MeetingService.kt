package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.MeetingDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MeetingService {
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
}