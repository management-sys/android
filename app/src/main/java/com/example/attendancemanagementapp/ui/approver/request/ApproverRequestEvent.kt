package com.example.attendancemanagementapp.ui.approver.request

import com.example.attendancemanagementapp.data.param.ApproverQuery

sealed interface ApproverRequestEvent {
    /* 초기화 */
    data object InitFirst: ApproverRequestEvent
    data object InitLast: ApproverRequestEvent

    /* 필터 초기화 버튼 클릭 이벤트 */
    data object ClickedInitFilter: ApproverRequestEvent

    /* 필터 적용 버튼 클릭 이벤트 */
    data class ClickedUseFilter(
        val filter: ApproverQuery
    ): ApproverRequestEvent

    /* 다음 페이지 조회 이벤트 */
    data object LoadNextPage: ApproverRequestEvent

    /* 출장 신청 버튼 클릭 이벤트 */
    data object ClickedAddTrip: ApproverRequestEvent

    /* 휴가 신청 버튼 클릭 이벤트 */
    data object ClickedAddVacation: ApproverRequestEvent
}