package com.example.attendancemanagementapp.data.dto

object CalendarDTO {
    /* 일정 목록 데이터 (임시): 종류가 너무 불명확함. 종류마다 출력 값이 달라서 알기 전까진 못할듯 */
    data class SchedulesInfo(
        val id: Int,            // 일정 아이디
        val type: String,       // 유형
        val name: String,       // 신청자
        val grade: String,      // 직급
        val startDate: String,  // 시작 날짜
        val endDate: String     // 종료 날짜
    )
}