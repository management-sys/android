package com.example.attendancemanagementapp.ui.home.calendar

import com.example.attendancemanagementapp.data.dto.CalendarDTO
import java.time.LocalDate
import java.time.YearMonth

data class CalendarState(
    val yearMonth: YearMonth = YearMonth.now(),                     // 출력할 월
    val selectedDate: LocalDate = LocalDate.now(),                  // 선택한 날짜
    val schedules: List<CalendarDTO.SchedulesInfo> = emptyList()    // 스케줄 목록
)