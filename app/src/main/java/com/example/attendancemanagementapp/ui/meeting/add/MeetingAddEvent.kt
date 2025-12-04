package com.example.attendancemanagementapp.ui.meeting.add

import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO.AssignedPersonnelInfo

enum class MeetingAddField {
    TITLE, START, END, PLACE, CONTENT, REMARK
}

enum class ExpenseField {
    TYPE, AMOUNT
}

sealed interface MeetingAddEvent {
    /* 초기화 */
    data class InitWith(
        val projectId: String,
        val projectName: String,
        val assignedPersonnels: List<AssignedPersonnelInfo>
    ): MeetingAddEvent

    /* 회의록 정보 필드 값 변경 이벤트 */
    data class ChangedValueWith(
        val field: MeetingAddField,
        val value: String
    ): MeetingAddEvent

    /* 외부 참석자 아이템 추가 버튼 클릭 이벤트 */
    data class ClickedAddExternalAttendeeWith(
        val newAttendee: MeetingDTO.AddAttendeesInfo
    ): MeetingAddEvent

    /* 회의비 아이템 추가 버튼 클릭 이벤트 */
    data object ClickedAddExpense: MeetingAddEvent

    /* 회의비 필드 값 변경 이벤트 */
    data class ChangedExpenseWith(
        val field: ExpenseField,
        val value: String,
        val idx: Int
    ): MeetingAddEvent

    /* 회의비 아이템 삭제 버튼 클릭 이벤트 */
    data class ClickedDeleteExpenseWith(
        val idx: Int
    ): MeetingAddEvent

    /* 등록 버튼 클릭 이벤트 */
    data object ClickedAdd: MeetingAddEvent

    /* 다음 페이지 조회 이벤트 */
    data object LoadNextPage: MeetingAddEvent

    /* 검색어 필드 값 변경 이벤트 */
    data class ChangedSearchValueWith(
        val value: String
    ): MeetingAddEvent

    /* 검색 버튼 클릭 이벤트 */
    data object ClickedSearch: MeetingAddEvent

    /* 검색어 초기화 버튼 클릭 이벤트 */
    data object ClickedSearchInit: MeetingAddEvent

    /* 참석자 체크박스 클릭 이벤트 */
    data class CheckedAttendeeWith(
        val checked: Boolean,
        val employee: EmployeeDTO.ManageEmployeesInfo
    ): MeetingAddEvent

    /* 참석자 아이템 삭제 버튼 클릭 이벤트 */
    data class ClickedDeleteAttendeeWith(
        val idx: Int
    ): MeetingAddEvent
}