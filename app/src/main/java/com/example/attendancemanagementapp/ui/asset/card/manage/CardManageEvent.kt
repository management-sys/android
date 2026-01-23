package com.example.attendancemanagementapp.ui.asset.card.manage

sealed interface CardManageEvent {
    // 초기화
    data object Init: CardManageEvent

    // 검색 키워드 선택 이벤트
    data class SelectedTypeWith(
        val type: String
    ): CardManageEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchTextWith(
        val value: String
    ): CardManageEvent

    // 검색 버튼 클릭 이벤트
    data object ClickedSearch: CardManageEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data object ClickedInitSearchText: CardManageEvent

    // 조회할 카드 클릭 이벤트
    data class ClickedCardWith(
        val id: String
    ): CardManageEvent
}