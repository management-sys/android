package com.example.attendancemanagementapp.ui.commoncode.detail

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO

data class CodeDetailUiState(
    val codeInfo: CommonCodeDTO.CommonCodeInfo = CommonCodeDTO.CommonCodeInfo() // 공통코드 정보
)