package com.example.attendancemanagementapp.ui.commoncode.add

import com.example.attendancemanagementapp.retrofit.param.SearchType

enum class CodeAddField { CODE, CODENAME, CODEVALUE, DESCRIPTION }

sealed interface CodeAddEvent {
    // 초기화
    data object Init: CodeAddEvent

    // 공통코드 검색 관련 초기화
    data object InitSearch: CodeAddEvent

    // 공통코드 정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: CodeAddField,
        val value: String
    ): CodeAddEvent

    // 공통코드 정보 추가 버튼 클릭 이벤트
    data object ClickedAdd: CodeAddEvent

    // 공통코드 검색 필드 값 변경 이벤트
    data class ChangedSearchWith(
        val value: String
    ): CodeAddEvent

    // 공통코드 검색어 초기화 버튼 클릭 이벤트
    data object ClickedInitSearch: CodeAddEvent

    // 공통코드 검색 버튼 클릭 이벤트
    data object ClickedSearch: CodeAddEvent

    // 상위코드 선택 이벤트
    data class SelectedUpperCodeWith(
        val upperCode: String,
        val upperCodeName: String
    ): CodeAddEvent

    // 검색 카테고리 변경 이벤트
    data class ChangedCategoryWith(
        val category: SearchType
    ): CodeAddEvent
}