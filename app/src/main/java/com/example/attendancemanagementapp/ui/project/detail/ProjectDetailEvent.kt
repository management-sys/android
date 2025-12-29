package com.example.attendancemanagementapp.ui.project.detail

sealed interface ProjectDetailEvent {
    /* 정보 조회할 회의록 선택 이벤트 */
    data class SelectedMeetingWith(
        val meetingId: Long
    ): ProjectDetailEvent

    /* 수정 버튼 클릭 이벤트 */
    data object ClickedUpdate: ProjectDetailEvent

    /* 삭제 버튼 클릭 이벤트 */
    data object ClickedDelete: ProjectDetailEvent

    /* 중단 버튼 클릭 이벤트 */
    data object ClickedStop: ProjectDetailEvent
}