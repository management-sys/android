package com.example.attendancemanagementapp.ui.attendance.vacation.status

sealed interface VacationStatusEvent {
    // 휴가 아이템 클릭 이벤트
    data class ClickedVacation(
        val id: String
    ): VacationStatusEvent
}