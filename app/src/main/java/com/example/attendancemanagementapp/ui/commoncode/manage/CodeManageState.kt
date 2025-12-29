package com.example.attendancemanagementapp.ui.commoncode.manage

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.commoncode.add.CodeSearchState

data class CodeManageState(
    val codeState: CodeSearchState = CodeSearchState()  // 공통코드 검색 관련 상태
)