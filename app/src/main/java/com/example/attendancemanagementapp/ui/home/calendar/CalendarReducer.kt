package com.example.attendancemanagementapp.ui.home.calendar

import java.time.LocalDate

object CalendarReducer {
    fun reduce(s: CalendarState, e: CalendarEvent): CalendarState = when (e) {
        is CalendarEvent.ClickedPrev -> handleClickedPrev(s)
        is CalendarEvent.ClickedNext -> handleClickedNext(s)
        is CalendarEvent.ClickedDateWith -> handleClickedDate(s, e.date)
        else -> s
    }

    private fun handleClickedPrev(
        state: CalendarState
    ): CalendarState {
        return state.copy(yearMonth = state.yearMonth.minusMonths(1))
    }

    private fun handleClickedNext(
        state: CalendarState
    ): CalendarState {
        return state.copy(yearMonth = state.yearMonth.plusMonths(1))
    }

    private fun handleClickedDate(
        state: CalendarState,
        date: LocalDate
    ): CalendarState {
        val filteredSchedules = state.schedules.filter { schedule ->
            // 일정 기간에 현재 날짜가 포함되는 일정만 필터링
            val startDate = LocalDate.parse(schedule.startDateTime.substring(0, 10))
            val endDate = LocalDate.parse(schedule.endDateTime.substring(0, 10))

            !date.isBefore(startDate) && !date.isAfter(endDate)
        }
        return state.copy(selectedDate = date, openSheet = true, filteredSchedules = filteredSchedules)
    }
}