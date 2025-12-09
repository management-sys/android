package com.example.attendancemanagementapp.ui.meeting.status


sealed interface MeetingStatusEvent {
    /* 초기화 */
    data object Init: MeetingStatusEvent

    /* 검색 버튼 클릭 이벤트 */
    data object ClickedSearch: MeetingStatusEvent

    /* 검색 초기화 버튼 클릭 이벤트 */
    data object ClickedInitSearch: MeetingStatusEvent

    /* 다음 페이지 조회 이벤트 */
    data object LoadNextPage: MeetingStatusEvent

    /* 검색어 필드 값 변경 이벤트 */
    data class ChangedSearchValue(
        val value: String
    ): MeetingStatusEvent

    /* 조회할 회의록 아이템 클릭 이벤트 */
    data class ClickedMeeting(
        val id: Long
    ): MeetingStatusEvent

    /* 필터 초기화 버튼 클릭 이벤트 */
    data object ClickedInitFilter: MeetingStatusEvent

    /* 필터 적용 버튼 클릭 이벤트 */
    data class ClickedUseFilter(
        val startDate: String,
        val endDate: String,
        val type: String
    ): MeetingStatusEvent
}