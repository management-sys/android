package com.example.attendancemanagementapp.ui.project.detail

sealed interface ProjectDetailEvent {
    /* 정보 조회할 회의록 선택 이벤트 */
    data class SelectedMeetingWith(
        val meetingId: Long
    ): ProjectDetailEvent
}