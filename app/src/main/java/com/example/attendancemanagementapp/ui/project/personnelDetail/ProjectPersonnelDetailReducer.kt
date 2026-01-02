package com.example.attendancemanagementapp.ui.project.personnelDetail

import java.time.LocalDate

object ProjectPersonnelDetailReducer {
    fun reduce(s: ProjectPersonnelDetailState, e: ProjectPersonnelDetailEvent): ProjectPersonnelDetailState = when (e) {
        ProjectPersonnelDetailEvent.Init -> handleInit()
        is ProjectPersonnelDetailEvent.ClickedPrev -> handleClickedPrev(s)
        is ProjectPersonnelDetailEvent.ClickedNext -> handleClickedNext(s)
        is ProjectPersonnelDetailEvent.ClickedDateWith -> handleClickedDate(s, e.date)
        ProjectPersonnelDetailEvent.ClickedCloseSheet -> handleClickedCloseSheet(s)
        else -> s
    }

    private fun handleInit(): ProjectPersonnelDetailState {
        return ProjectPersonnelDetailState()
    }

    private fun handleClickedPrev(
        state: ProjectPersonnelDetailState
    ): ProjectPersonnelDetailState {
        return state.copy(yearMonth = state.yearMonth.minusMonths(1))
    }

    private fun handleClickedNext(
        state: ProjectPersonnelDetailState
    ): ProjectPersonnelDetailState {
        return state.copy(yearMonth = state.yearMonth.plusMonths(1))
    }

    private fun handleClickedDate(
        state: ProjectPersonnelDetailState,
        date: LocalDate
    ): ProjectPersonnelDetailState {
        val filteredProjects = state.personnelInfo.projects
            .filter { project ->
                // 프로젝트 기간에 현재 날짜가 포함되는 프로젝트만 필터링
                val startDate = LocalDate.parse(project.businessStartDate.substring(0, 10))
                val endDate = LocalDate.parse(project.businessEndDate.substring(0, 10))

                !date.isBefore(startDate) && !date.isAfter(endDate)
            }
            .sortedBy { project ->
                LocalDate.parse(project.businessStartDate.substring(0, 10))
            }
        return state.copy(selectedDate = date, openSheet = true, filteredProjects = filteredProjects)
    }

    private fun handleClickedCloseSheet(
        state: ProjectPersonnelDetailState
    ): ProjectPersonnelDetailState {
        return state.copy(openSheet = false)
    }
}