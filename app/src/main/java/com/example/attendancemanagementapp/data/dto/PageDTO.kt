package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object PageDTO {
    data class PageInfo(
        @Json(name = "currentPage")     val currentPage: Int = 0,           // 현재 페이지 번호 (0부터 시작)
        @Json(name = "currentSize")     val currentSize: Int = 0,           // 현재 페이지 항목 수
        @Json(name = "hasNext")         val hasNext: Boolean = false,       // 다음 페이지 존재 여부
        @Json(name = "hasPrevious")     val hasPrevious: Boolean = false,   // 이전 페이지 존재 여부
        @Json(name = "totalElements")   val totalElements: Int = 0,         // 총 항목 수
        @Json(name = "totalPages")      val totalPages: Int = 0             // 총 페이지 수
    )
}