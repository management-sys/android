package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object ApproverDTO {
    /* 이전 승인자 불러오기 응답 */
    data class GetPrevApproversResponse(
        @Json(name = "confmers")    val approvers: List<ApproversInfo>  // 가장 최근 휴가 신청서의 승인자 목록 (아이디, 이름)
    )

    /* 승인자 목록 데이터 */
    data class ApproversInfo(
        @Json(name = "name")    val name: String,   // 승인자 이름
        @Json(name = "userId")  val userId: String, // 승인자 아이디
    )
}