package com.example.attendancemanagementapp.ui.components.search

import com.example.attendancemanagementapp.ui.commoncode.FieldState

data class SearchUiState(
    val value: String,                      // 검색창 값
    val onValueChange: (String) -> Unit,    // 검색창 입력 이벤트
    val onClickSearch: () -> Unit,          // 검색 버튼 클릭 이벤트
    val selected: String,                   // 선택한 카테고리 값
    val categories: List<String>,           // 카테고리 목록
    val onClickCategory: (String) -> Unit   // 카테고리 클릭 이벤트
)