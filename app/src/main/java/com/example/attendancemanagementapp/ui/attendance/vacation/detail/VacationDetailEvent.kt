package com.example.attendancemanagementapp.ui.attendance.vacation.detail

sealed interface VacationDetailEvent {
    // 휴가 신청 삭제 버튼 클릭 이벤트
    data object ClickedDelete: VacationDetailEvent

    // 휴가 신청 취소 버튼 클릭 이벤트
    data object ClickedCancel: VacationDetailEvent
}