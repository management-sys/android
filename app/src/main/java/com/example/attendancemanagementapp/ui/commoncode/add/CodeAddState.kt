package com.example.attendancemanagementapp.ui.commoncode.add

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState
import com.example.attendancemanagementapp.retrofit.param.SearchType

data class CodeAddState(
    val inputData: CommonCodeDTO.CommonCodeInfo = CommonCodeDTO.CommonCodeInfo(),   // 입력한 공통코드 정보
    val codeState: CodeSearchState = CodeSearchState()                              // 공통코드 검색 관련 상태
)

/* 공통코드 검색 관련 상태 */
data class CodeSearchState(
    val codes: List<CommonCodeDTO.CommonCodesInfo> = emptyList(),   // 공통코드 목록
    val searchText: String = "",                                    // 공통코드 검색어
    val selectedCategory: SearchType = SearchType.ALL,              // 선택한 카테고리
    val paginationState: PaginationState = PaginationState()        // 페이지네이션 상태
)