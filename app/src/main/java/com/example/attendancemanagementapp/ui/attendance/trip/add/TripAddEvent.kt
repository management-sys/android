package com.example.attendancemanagementapp.ui.attendance.trip.add

import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.data.dto.CardDTO

enum class TripAddField {
    START, END, PLACE, PURPOSE, CONTENT
}

enum class TripSearchField {
    ATTENDEE, APPROVER, CARD, CAR, DRIVER
}

sealed interface TripAddEvent {
    // 초기화
    data object Init: TripAddEvent

    // 이전 승인자 불러오기 버튼 클릭 이벤트
    data object ClickedGetPrevApprover: TripAddEvent

    // 신청 버튼 클릭 이벤트
    data object ClickedAdd: TripAddEvent

    // 구분 선택 이벤트
    data class SelectedTypeWith(
        val type: String
    ): TripAddEvent

    // 출장 정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: TripAddField,
        val value: String
    ): TripAddEvent

    // 다음 페이지 조회 이벤트
    data object LoadNextPage: TripAddEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchValueWith(
        val field: TripSearchField,
        val value: String
    ): TripAddEvent

    // 검색 버튼 클릭 이벤트
    data class ClickedSearch(
        val field: TripSearchField
    ): TripAddEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data class ClickedSearchInit(
        val field: TripSearchField
    ): TripAddEvent

    // 승인자 선택 이벤트
    data class SelectedApproverWith(
        val checked: Boolean,
        val id: String
    ): TripAddEvent

    // 동행자 선택 이벤트
    data class SelectedAttendeeWith(
        val checked: Boolean,
        val id: String
    ): TripAddEvent

    // 차량 선택 이벤트
    data class ClickedCarWith(
        val id: String
    ): TripAddEvent

    // 카드 검색 타입 선택 이벤트
    data class SelectedCardTypeWith(
        val type: String
    ): TripAddEvent

    // 차량 검색 타입 선택 이벤트
    data class SelectedCarTypeWith(
        val type: String
    ): TripAddEvent

    // 카드 체크박스 클릭 이벤트
    data class CheckedCardWith(
        val checked: Boolean,
        val card: CardDTO.CardsInfo
    ): TripAddEvent

    // 카드 사용 현황 일시 수정 이벤트
    data class ChangedCardDateWith(
        val id: String,
        val isStart: Boolean,
        val value: String
    ): TripAddEvent

    // 차량 체크박스 클릭 이벤트
    data class CheckedCarWith(
        val checked: Boolean,
        val car: CarDTO.CarsInfo
    ): TripAddEvent

    // 차량 사용 현황 일시 수정 이벤트
    data class ChangedCarDateWith(
        val id: String,
        val isStart: Boolean,
        val value: String
    ): TripAddEvent

    // 운전자 선택 이벤트
    data class SelectedDriverWith(
        val id: String,
        val driverId: String
    ): TripAddEvent
}