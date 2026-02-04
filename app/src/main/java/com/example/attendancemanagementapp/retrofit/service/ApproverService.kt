package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.ApproverDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface ApproverService {
    // 결재 요청 목록 조회
    @GET("/api/sanctns")
    suspend fun getApprovers(
        @Query("confmAt") approvalType: String,         // 결재구분 (W: 결재대기, A: 승인, R: 반려, C: 취소)
        @Query("sanctnIem") applicationType: String,    // 신청구분 (VCAT: 휴가, BSRP: 출장)
        @Query("year") year: Int?,                      // 조회 년도
        @Query("month") month: Int?,                    // 조회 월 (1~12)
        @Query("page") page: Int? = null,               // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null,               // 페이지 당 데이터 개수
    ): ApproverDTO.GetApproversResponse
}