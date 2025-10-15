package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object AuthorDTO {
    /* 권한 목록 조회 응답 */
    data class GetAuthorsResponse(
        @Json(name = "authorCode")  val code: String,   // 권한 코드
        @Json(name = "authorNm")    val name: String    // 권한명
    )
}