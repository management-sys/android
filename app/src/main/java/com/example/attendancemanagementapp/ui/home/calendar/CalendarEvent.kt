package com.example.attendancemanagementapp.ui.home.calendar

import java.time.LocalDate

sealed interface CalendarEvent {
    // 이전 달 이동 버튼 클릭 이벤트
    data object ClickedPrev: CalendarEvent

    // 다음 달 이동 버튼 클릭 이벤트
    data object ClickedNext: CalendarEvent

    // 날짜 클릭 이벤트
    data class ClickedDateWith(
        val date: Int
    ): CalendarEvent
}