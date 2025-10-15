package com.example.attendancemanagementapp.ui.commoncode.detail

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO

data class CodeDetailState(
    val codeInfo: CommonCodeDTO.CommonCodeInfo = CommonCodeDTO.CommonCodeInfo() // 공통코드 정보
)