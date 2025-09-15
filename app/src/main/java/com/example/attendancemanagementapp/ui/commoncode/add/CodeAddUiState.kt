package com.example.attendancemanagementapp.ui.commoncode.add

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO

data class CodeAddUiState(
    val inputData: CommonCodeDTO.CodeInfo = CommonCodeDTO.CodeInfo() // 입력한 데이터
)