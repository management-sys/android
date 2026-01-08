package com.example.attendancemanagementapp.ui.attendance.vacation.status

import com.example.attendancemanagementapp.data.param.VacationsSearchType

object VacationStatusReducer {
    fun reduce(s: VacationStatusState, e: VacationStatusEvent): VacationStatusState = when (e) {
        is VacationStatusEvent.ClickedVacationTypeWith -> handleClickedVacationTypeWith(s, e.type)
        is VacationStatusEvent.SelectedYearWith -> handleSelectedYearWith(s, e.year)
        else -> s
    }

    private fun handleClickedVacationTypeWith(
        state: VacationStatusState,
        type: VacationsSearchType
    ): VacationStatusState {
        return state.copy(vacationStatusInfo = state.vacationStatusInfo.copy(vacations = emptyList()), query = state.query.copy(filter = type))
    }

    private fun handleSelectedYearWith(
        state: VacationStatusState,
        year: Int
    ): VacationStatusState {
        return state.copy(vacationStatusInfo = state.vacationStatusInfo.copy(vacations = emptyList()), query = state.query.copy(year = year))
    }
}