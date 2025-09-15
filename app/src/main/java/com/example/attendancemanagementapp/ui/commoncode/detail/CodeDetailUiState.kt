package com.example.attendancemanagementapp.ui.commoncode.detail

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO

data class CodeDetailUiState(
    val codeInfo: CommonCodeDTO.CodeInfo = CommonCodeDTO.CodeInfo() // 공통코드 정보
)