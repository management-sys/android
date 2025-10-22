package com.example.attendancemanagementapp.ui.commoncode.manage

import com.example.attendancemanagementapp.retrofit.param.SearchType

sealed interface CodeManageEvent {
    // 공통코드 검색 관련 초기화
    data object InitSearch: CodeManageEvent

    // 검색 카테고리 변경 이벤트
    data class ChangedCategoryWith(
        val category: SearchType
    ): CodeManageEvent

    // 공통코드 검색 필드 값 변경 이벤트
    data class ChangedSearchWith(
        val value: String
    ): CodeManageEvent

    // 공통코드 검색어 초기화 버튼 클릭 이벤트
    data object ClickedInitSearch: CodeManageEvent

    // 공통코드 검색 버튼 클릭 이벤트
    data object ClickedSearch: CodeManageEvent

    // 정보 조회할 코드 선택 이벤트
    data class SelectedCode(
        val code: String
    ): CodeManageEvent
}