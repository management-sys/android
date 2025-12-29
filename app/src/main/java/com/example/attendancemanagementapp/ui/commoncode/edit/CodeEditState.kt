package com.example.attendancemanagementapp.ui.commoncode.edit

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.commoncode.add.CodeSearchState

data class CodeEditState(
    val inputData: CommonCodeDTO.CommonCodeInfo = CommonCodeDTO.CommonCodeInfo(),   // 입력한 데이터
    val codeState: CodeSearchState = CodeSearchState()                              // 공통코드 검색 관련 상태
)
