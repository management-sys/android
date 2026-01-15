package com.example.attendancemanagementapp.ui.asset.car.usage

enum class CarUsageField {
    RESERVATION, USAGE
}

sealed interface CarUsageEvent {
    // 초기화
    data object Init: CarUsageEvent

    // 검색 키워드 선택 이벤트
    data class SelectedTypeWith(
        val field: CarUsageField,
        val type: String
    ): CarUsageEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchTextWith(
        val field: CarUsageField,
        val value: String
    ): CarUsageEvent

    // 검색 버튼 클릭 이벤트
    data class ClickedSearchWith(
        val field: CarUsageField
    ): CarUsageEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data class ClickedInitSearchTextWith(
        val field: CarUsageField
    ): CarUsageEvent

    // 다음 페이지 조회 이벤트
    data class LoadNextPage(
        val field: CarUsageField
    ): CarUsageEvent
}