package com.example.attendancemanagementapp.ui.commoncode.list

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.param.SearchType

data class CodeListUiState(
    val codes: List<CommonCodeDTO.CommonCodesInfo> = emptyList(),   // 공통코드 목록
    val searchText: String = "",                                    // 검색어
    val selectedCategory: SearchType = SearchType.ALL,              // 선택한 카테고리
    val currentPage: Int = 0,                                       // 현재 페이지 번호
    val totalPage: Int = Int.MAX_VALUE,                             // 총 페이지 개수
    val isLoading: Boolean = false                                  // 로딩 중 여부
)