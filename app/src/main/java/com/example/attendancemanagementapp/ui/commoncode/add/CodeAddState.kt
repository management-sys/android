package com.example.attendancemanagementapp.ui.commoncode.add

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.ui.components.search.CodeSearchState

data class CodeAddState(
    val inputData: CommonCodeDTO.CommonCodeInfo = CommonCodeDTO.CommonCodeInfo()    // 입력한 공통코드 정보
)