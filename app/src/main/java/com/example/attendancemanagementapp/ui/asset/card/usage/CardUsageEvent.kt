package com.example.attendancemanagementapp.ui.asset.card.usage

enum class CardUsageField {
    RESERVATION, USAGE
}

sealed interface CardUsageEvent {
    // 초기화
    data object Init: CardUsageEvent

    // 검색 키워드 선택 이벤트
    data class SelectedTypeWith(
        val field: CardUsageField,
        val type: String
    ): CardUsageEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchTextWith(
        val field: CardUsageField,
        val value: String
    ): CardUsageEvent

    // 검색 버튼 클릭 이벤트
    data class ClickedSearchWith(
        val field: CardUsageField
    ): CardUsageEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data class ClickedInitSearchTextWith(
        val field: CardUsageField
    ): CardUsageEvent

    // 다음 페이지 조회 이벤트
    data class LoadNextPage(
        val field: CardUsageField
    ): CardUsageEvent
}