package com.example.attendancemanagementapp.ui.meeting.detail

sealed interface MeetingDetailEvent {
    /* 회의록 수정 버튼 클릭 이벤트 */
    data object ClickedUpdate: MeetingDetailEvent

    /* 회의록 삭제 버튼 클릭 이벤트 */
    data object ClickedDelete: MeetingDetailEvent
}