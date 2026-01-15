package com.example.attendancemanagementapp.ui.asset.card.edit

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO

enum class CardEditField {
    NAME, REMARK, STATUS
}

sealed interface CardEditEvent {
    // 초기화
    data class InitWith(
        val carInfo: CardDTO.GetCardResponse
    ): CardEditEvent

    // 차량정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: CardEditField,
        val value: String
    ): CardEditEvent

    // 매니저 선택 이벤트
    data class SelectedManagerWith(
        val mangerInfo: EmployeeDTO.ManageEmployeesInfo
    ): CardEditEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchValueWith(
        val value: String
    ): CardEditEvent

    // 검색 버튼 클릭 이벤트
    data object ClickedSearch: CardEditEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data object ClickedSearchInit: CardEditEvent

    // 다음 페이지 조회 이벤트
    data object LoadNextPage: CardEditEvent

    // 수정 버튼 클릭 이벤트
    data object ClickedUpdate: CardEditEvent
}