package com.example.attendancemanagementapp.ui.commoncode.edit

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.commoncode.CodeSearchState

data class CodeEditState(
    val inputData: CommonCodeDTO.CommonCodeInfo = CommonCodeDTO.CommonCodeInfo(),   // 입력한 데이터
    override val codes: List<CommonCodeDTO.CommonCodesInfo> = emptyList(),                   // 공통코드 목록
    override val searchText: String = "",                                                    // 검색어
    override val selectedCategory: SearchType = SearchType.ALL,                              // 선택한 카테고리
    override val paginationState: PaginationState = PaginationState()
): CodeSearchState
