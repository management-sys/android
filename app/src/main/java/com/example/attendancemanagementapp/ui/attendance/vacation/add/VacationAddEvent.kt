package com.example.attendancemanagementapp.ui.attendance.vacation.add

enum class VacationAddField {
    TYPE, START, END, DETAIL
}

sealed interface VacationAddEvent {
    // 초기화
    data class InitWith(
        val id: String
    ): VacationAddEvent

    // 회의록 정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: VacationAddField,
        val value: String
    ): VacationAddEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchValueWith(
        val value: String
    ): VacationAddEvent

    // 검색 버튼 클릭 이벤트
    data object ClickedSearch: VacationAddEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data object ClickedSearchInit: VacationAddEvent

    // 다음 페이지 조회 이벤트
    data object LoadNextPage: VacationAddEvent

    // 프로젝트 책임자 선택 이벤트
    data class SelectedApproverWith(
        val checked: Boolean,
        val id: String
    ): VacationAddEvent

    // 신청 버튼 클릭 이벤트
    data object ClickedAdd: VacationAddEvent

    // 이전 승인자 불러오기 버튼 클릭 이벤트
    data object ClickedGetPrevApprover: VacationAddEvent
}