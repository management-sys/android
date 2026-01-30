package com.example.attendancemanagementapp.ui.attendance.report.edit

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.ui.attendance.report.add.ReportAddEvent
import com.example.attendancemanagementapp.ui.attendance.report.add.TripExpenseField
import com.example.attendancemanagementapp.ui.attendance.report.add.TripExpenseSearchField

sealed interface ReportEditEvent {
    // 초기화
    data class InitWith(
        val tripInfo: TripDTO.GetTripResponse,
        val reportInfo: TripDTO.GetTripReportResponse
    ): ReportEditEvent

    // 다음 페이지 조회 이벤트
    data object LoadNextPage: ReportEditEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchValueWith(
        val field: TripExpenseSearchField,
        val value: String
    ): ReportEditEvent

    // 검색 버튼 클릭 이벤트
    data class ClickedSearchWith(
        val field: TripExpenseSearchField
    ): ReportEditEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data class ClickedSearchInitWith(
        val field: TripExpenseSearchField
    ): ReportEditEvent

    // 승인자 선택 이벤트
    data class SelectedApproverWith(
        val checked: Boolean,
        val id: String
    ): ReportEditEvent

    // 복명내용 필드 값 변경 이벤트
    data class ChangedContentWith(
        val value: String
    ): ReportEditEvent

    // 여비계산 아이템 추가 버튼 클릭 이벤트
    data object ClickedAddExpense: ReportEditEvent

    // 여비계산 아이템 값 변경 이벤트
    data class ChangedExpenseValueWith(
        val field: TripExpenseField,
        val idx: Int,
        val value: String
    ): ReportEditEvent

    // 카드 검색 타입 선택 이벤트
    data class SelectedCardTypeWith(
        val type: String
    ): ReportEditEvent

    // 카드 체크박스 클릭 이벤트
    data class SelectedCardWith(
        val card: CardDTO.CardsInfo,
        val idx: Int
    ): ReportEditEvent

    // 결제자 선택 이벤트
    data class SelectedManagerWith(
        val id: String,
        val idx: Int
    ): ReportEditEvent

    // 이전 승인자 불러오기 버튼 클릭 이벤트
    data object ClickedGetPrevApprover: ReportEditEvent

    // 수정 버튼 클릭 이벤트
    data object ClickedEdit: ReportEditEvent

    // 여비계산 아이템 삭제 버튼 클릭 이벤트
    data class ClickedDeleteTripExpenseWith(
        val idx: Int
    ): ReportEditEvent
}