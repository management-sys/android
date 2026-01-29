package com.example.attendancemanagementapp.ui.attendance.report.add

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.TripDTO

enum class TripExpenseField {
    AMOUNT, CATEGORY, TYPE
}

enum class TripExpenseSearchField {
    APPROVER, CARD, PAYER
}

sealed interface ReportAddEvent {
    // 초기화
    data class InitWith(
        val tripInfo: TripDTO.GetTripResponse
    ): ReportAddEvent

    // 다음 페이지 조회 이벤트
    data object LoadNextPage: ReportAddEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchValueWith(
        val field: TripExpenseSearchField,
        val value: String
    ): ReportAddEvent

    // 검색 버튼 클릭 이벤트
    data class ClickedSearchWith(
        val field: TripExpenseSearchField
    ): ReportAddEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data class ClickedSearchInitWith(
        val field: TripExpenseSearchField
    ): ReportAddEvent

    // 승인자 선택 이벤트
    data class SelectedApproverWith(
        val checked: Boolean,
        val id: String
    ): ReportAddEvent

    // 복명내용 필드 값 변경 이벤트
    data class ChangedContentWith(
        val value: String
    ): ReportAddEvent

    // 여비계산 아이템 추가 버튼 클릭 이벤트
    data object ClickedAddExpense: ReportAddEvent

    // 여비계산 아이템 값 변경 이벤트
    data class ChangedExpenseValueWith(
        val field: TripExpenseField,
        val idx: Int,
        val value: String
    ): ReportAddEvent

    // 카드 검색 타입 선택 이벤트
    data class SelectedCardTypeWith(
        val type: String
    ): ReportAddEvent

    // 카드 체크박스 클릭 이벤트
    data class SelectedCardWith(
        val card: CardDTO.CardsInfo,
        val idx: Int
    ): ReportAddEvent

    // 결제자 선택 이벤트
    data class SelectedManagerWith(
        val id: String,
        val idx: Int
    ): ReportAddEvent

    // 이전 승인자 불러오기 버튼 클릭 이벤트
    data object ClickedGetPrevApprover: ReportAddEvent

    // 신청 버튼 클릭 이벤트
    data object ClickedAdd: ReportAddEvent
}