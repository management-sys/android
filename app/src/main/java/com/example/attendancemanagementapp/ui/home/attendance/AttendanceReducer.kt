package com.example.attendancemanagementapp.ui.home.attendance

object AttendanceReducer {
    fun reduce(s: AttendanceState, e: AttendanceEvent): AttendanceState = when (e) {
        AttendanceEvent.ClickedFinishWork -> handleWork(s)
        AttendanceEvent.ClickedStartWork -> handleWork(s)
    }

    private fun handleWork(
        state: AttendanceState
    ): AttendanceState {
        return state.copy(isWorking = !state.isWorking)
    }
}