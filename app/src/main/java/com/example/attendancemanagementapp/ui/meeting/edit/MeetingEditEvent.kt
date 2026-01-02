package com.example.attendancemanagementapp.ui.meeting.edit

import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.meeting.add.ExpenseField
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddEvent
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddField

sealed interface MeetingEditEvent {
    /* 초기화 */
    data class InitWith(
        val data: MeetingDTO.GetMeetingResponse
    ): MeetingEditEvent

    /* 회의록 정보 필드 값 변경 이벤트 */
    data class ChangedValueWith(
        val field: MeetingAddField,
        val value: String
    ): MeetingEditEvent

    /* 외부 참석자 아이템 추가 버튼 클릭 이벤트 */
    data class ClickedAddExternalAttendeeWith(
        val newAttendee: MeetingDTO.AttendeesInfo
    ): MeetingEditEvent

    /* 회의비 아이템 추가 버튼 클릭 이벤트 */
    data object ClickedAddExpense: MeetingEditEvent

    /* 회의비 필드 값 변경 이벤트 */
    data class ChangedExpenseWith(
        val field: ExpenseField,
        val value: String,
        val idx: Int
    ): MeetingEditEvent

    /* 회의비 아이템 삭제 버튼 클릭 이벤트 */
    data class ClickedDeleteExpenseWith(
        val idx: Int
    ): MeetingEditEvent

    /* 수정 버튼 클릭 이벤트 */
    data object ClickedUpdate: MeetingEditEvent

    /* 다음 페이지 조회 이벤트 */
    data object LoadNextPage: MeetingEditEvent

    /* 검색어 필드 값 변경 이벤트 */
    data class ChangedSearchValueWith(
        val value: String
    ): MeetingEditEvent

    /* 검색 버튼 클릭 이벤트 */
    data object ClickedSearch: MeetingEditEvent

    /* 검색어 초기화 버튼 클릭 이벤트 */
    data object ClickedInitSearch: MeetingEditEvent

    /* 내부 참석자 추가 버튼 클릭 이벤트 */
    data object ClickedAddInnerAttendee: MeetingEditEvent

    /* 참석자 체크박스 클릭 이벤트 */
    data class CheckedAttendeeWith(
        val checked: Boolean,
        val employee: ProjectDTO.EmployeeInfo
    ): MeetingEditEvent

    /* 참석자 아이템 삭제 버튼 클릭 이벤트 */
    data class ClickedDeleteAttendeeWith(
        val idx: Int
    ): MeetingEditEvent
}