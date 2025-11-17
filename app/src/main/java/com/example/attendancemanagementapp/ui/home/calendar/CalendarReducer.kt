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
        date: Int
    ): CalendarState {
        return state.copy(selectedDate = date, openSheet = true)
    }
}