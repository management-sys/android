package com.example.attendancemanagementapp.ui.commoncode

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState
import com.example.attendancemanagementapp.retrofit.param.SearchType

interface CodeSearchState {
    val codes: List<CommonCodeDTO.CommonCodesInfo>  // 공통코드 목록
    val searchText: String                          // 검색어
    val selectedCategory: SearchType                // 선택한 카테고리
    val paginationState: PaginationState            // 페이지네이션 상태
}