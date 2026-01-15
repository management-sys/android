package com.example.attendancemanagementapp.ui.asset.card.add

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.asset.card.edit.CardEditField

sealed interface CardAddEvent {
    // 초기화
    data class InitWith(
        val carInfo: CardDTO.GetCardResponse
    ): CardAddEvent

    // 차량정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: CardEditField,
        val value: String
    ): CardAddEvent

    // 매니저 선택 이벤트
    data class SelectedManagerWith(
        val mangerInfo: EmployeeDTO.ManageEmployeesInfo
    ): CardAddEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchValueWith(
        val value: String
    ): CardAddEvent

    // 검색 버튼 클릭 이벤트
    data object ClickedSearch: CardAddEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data object ClickedSearchInit: CardAddEvent

    // 다음 페이지 조회 이벤트
    data object LoadNextPage: CardAddEvent

    // 등록 버튼 클릭 이벤트
    data object ClickedAdd: CardAddEvent
}