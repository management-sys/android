package com.example.attendancemanagementapp.ui.home.calendar

import java.time.LocalDate
import java.time.YearMonth

data class CalendarState(
    val yearMonth: YearMonth = YearMonth.now(), // 출력할 월
    val selectedDate: Int = 1,                  // 선택한 날짜
    val openSheet: Boolean = false,             // 일정 목록 바텀 시트 열림 여부
    val schedules: List<List<String>> = listOf( // 스케줄 목록 (유형, 일정 이름, 직원 이름, 직급)
        listOf("공가", "", "테스트1", "사원"),
        listOf("연차", "", "테스트2", "대리"),
        listOf("국내 출장", "구미시", "테스트3", "주임")
    )
//    val schedules: List<List<String>> = emptyList()                 // 스케줄 목록 (유형, 일정 이름, 직원 이름, 직급)
)