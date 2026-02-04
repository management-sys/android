package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object ApproverDTO {
    /* 결재 요청 목록 조회 응답 */
    data class GetApproversResponse(
        @Json(name = "content")     val approvers: List<ApproversInfo> = emptyList(),   // 결재 요청 목록
        @Json(name = "totalPages")  val totalPages: Int = 0                             // 총 페이지 개수
    )

    /* 결재 요청 목록 데이터 */
    data class ApproversInfo(
        @Json(name = "bgnde")       val startDate: String,          // 시작일
        @Json(name = "confmAt")     val status: String,             // 상태
        @Json(name = "endde")       val endDate: String,            // 종료일
        @Json(name = "period")      val period: String,             // 기간
        @Json(name = "rgsde")       val appliedDate: String,        // 신청일
        @Json(name = "sanctnIem")   val approvalType: String,       // 결재구분
        @Json(name = "type")        val applicationType: String,    // 신청구분
    )
}