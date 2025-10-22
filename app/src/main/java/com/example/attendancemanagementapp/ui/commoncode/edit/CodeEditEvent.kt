package com.example.attendancemanagementapp.ui.commoncode.edit

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.param.SearchType

enum class CodeEditField { CODENAME, CODEVALUE, DESCRIPTION }

sealed interface CodeEditEvent {
    // 초기화
    data object Init: CodeEditEvent
    data class InitWith(
        val codeInfo: CommonCodeDTO.CommonCodeInfo
    ): CodeEditEvent

    // 공통코드 검색 관련 초기화
    data object InitSearch: CodeEditEvent

    // 공통코드 정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: CodeEditField,
        val value: String
    ): CodeEditEvent

    // 공통코드 정보 수정 버튼 클릭 이벤트
    data object ClickedEdit: CodeEditEvent

    // 공통코드 검색 필드 값 변경 이벤트
    data class ChangedSearchWith(
        val value: String
    ): CodeEditEvent

    // 공통코드 검색어 초기화 버튼 클릭 이벤트
    data object ClickedInitSearch: CodeEditEvent

    // 공통코드 검색 버튼 클릭 이벤트
    data object ClickedSearch: CodeEditEvent

    // 상위코드 선택 이벤트
    data class SelectedUpperCodeWith(
        val upperCode: String,
        val upperCodeName: String
    ): CodeEditEvent

    // 검색 카테고리 변경 이벤트
    data class ChangedCategoryWith(
        val category: SearchType
    ): CodeEditEvent
}