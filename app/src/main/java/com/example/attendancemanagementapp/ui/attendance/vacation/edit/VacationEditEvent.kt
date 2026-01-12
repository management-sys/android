package com.example.attendancemanagementapp.ui.attendance.vacation.edit

import com.example.attendancemanagementapp.data.dto.VacationDTO

enum class VacationEditField {
    START, END, DETAIL
}

sealed interface VacationEditEvent {
    // 초기화
    data class InitWith(
        val data: VacationDTO.GetVacationResponse
    ): VacationEditEvent

    // 휴가 정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: VacationEditField,
        val value: String
    ): VacationEditEvent

    // 구분 선택 이벤트
    data class SelectedTypeWith(
        val type: String
    ): VacationEditEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchValueWith(
        val value: String
    ): VacationEditEvent

    // 검색 버튼 클릭 이벤트
    data object ClickedSearch: VacationEditEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data object ClickedSearchInit: VacationEditEvent

    // 다음 페이지 조회 이벤트
    data object LoadNextPage: VacationEditEvent

    // 프로젝트 책임자 선택 이벤트
    data class SelectedApproverWith(
        val checked: Boolean,
        val id: String
    ): VacationEditEvent

    // 수정 버튼 클릭 이벤트
    data object ClickedUpdate: VacationEditEvent

    // 이전 승인자 불러오기 버튼 클릭 이벤트
    data object ClickedGetPrevApprover: VacationEditEvent
}