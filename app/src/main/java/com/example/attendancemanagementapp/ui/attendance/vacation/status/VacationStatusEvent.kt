package com.example.attendancemanagementapp.ui.attendance.vacation.status

import com.example.attendancemanagementapp.data.param.VacationsSearchType

sealed interface VacationStatusEvent {
    // 초기화
    data object Init: VacationStatusEvent

    // 휴가 아이템 클릭 이벤트
    data class ClickedVacationWith(
        val id: String
    ): VacationStatusEvent

    // 휴가 종류 아이템 클릭 이벤트
    data class ClickedVacationTypeWith(
        val type: VacationsSearchType
    ): VacationStatusEvent

    // 연차 선택 이벤트
    data class SelectedYearWith(
        val year: Int
    ): VacationStatusEvent
}