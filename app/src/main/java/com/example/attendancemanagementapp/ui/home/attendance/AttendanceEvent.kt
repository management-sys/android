package com.example.attendancemanagementapp.ui.home.attendance

sealed interface AttendanceEvent {
    // 출근하기 버튼 클릭 이벤트
    data object ClickedStartWork: AttendanceEvent

    // 퇴근하기 버튼 클릭 이벤트
    data object ClickedFinishWork: AttendanceEvent
}