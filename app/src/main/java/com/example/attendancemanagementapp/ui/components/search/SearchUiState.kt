package com.example.attendancemanagementapp.ui.components.search

import com.example.attendancemanagementapp.retrofit.param.SearchType
import kotlin.enums.EnumEntries

data class SearchUiState(
    val value: String,                          // 검색창 값
    val onValueChange: (String) -> Unit,        // 검색창 입력 이벤트
    val onClickSearch: () -> Unit,              // 검색 버튼 클릭 이벤트
    val onClickInit: () -> Unit,                // 초기화 버튼 클릭 이벤트
)

data class CodeSearchUiState(
    val searchUiState: SearchUiState,           // 검색 상태 값
    val selectedCategory: SearchType,           // 선택한 카테고리 값
    val categories: EnumEntries<SearchType>,    // 카테고리 목록
    val onClickCategory: (SearchType) -> Unit   // 카테고리 클릭 이벤트
)