package com.example.attendancemanagementapp.ui.asset.car.manage

import com.example.attendancemanagementapp.ui.project.status.ProjectStatusEvent

sealed interface CarManageEvent {
    // 초기화
    data object Init: CarManageEvent

    // 검색 키워드 선택 이벤트
    data class SelectedTypeWith(
        val type: String
    ): CarManageEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchTextWith(
        val value: String
    ): CarManageEvent

    // 검색 버튼 클릭 이벤트
    data object ClickedSearch: CarManageEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data object ClickedInitSearchText: CarManageEvent

    // 조회할 차량 클릭 이벤트
    data class ClickedCarWith(
        val id: String
    ): CarManageEvent
}