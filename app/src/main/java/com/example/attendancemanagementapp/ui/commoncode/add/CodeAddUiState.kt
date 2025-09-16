package com.example.attendancemanagementapp.ui.commoncode.add

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.ui.commoncode.FieldState

data class CodeAddUiState(
    val inputData: CommonCodeDTO.CodeInfo = CommonCodeDTO.CodeInfo(),   // 입력한 데이터
    val selectedCode: String = "",                                      // 선택한 공통코드의 코드
    val searchText: FieldState = FieldState(),                          // 검색어
    val selectedFilter: FieldState = FieldState(value = "전체")          // 선택한 필터
)