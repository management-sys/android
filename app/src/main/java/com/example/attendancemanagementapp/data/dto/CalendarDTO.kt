package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object CalendarDTO {
    /* 일정 목록 데이터 (임시): 종류가 너무 불명확함. 종류마다 출력 값이 달라서 알기 전까진 못할듯 */
    data class SchedulesInfo(
        @Json(name = "id")          val id: Int,            // 일정 아이디
        @Json(name = "type")        val type: String,       // 유형
        @Json(name = "name")        val name: String,       // 신청자
        @Json(name = "grade")       val grade: String,      // 직급
        @Json(name = "startDate")   val startDate: String,  // 시작 날짜
        @Json(name = "endDate")     val endDate: String     // 종료 날짜
    )
}