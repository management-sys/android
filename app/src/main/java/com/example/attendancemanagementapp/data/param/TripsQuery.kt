package com.example.attendancemanagementapp.data.param

/* 출장 현황 목록 조회에서 사용하는 쿼리 데이터 */
data class TripsQuery(
    val year: Int? = null,
    val filter: String = "전체"
)