package com.example.attendancemanagementapp.ui.commoncode.add

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO

data class CodeAddUiState(
    val inputData: CommonCodeDTO.CommonCodeInfo = CommonCodeDTO.CommonCodeInfo(),   // 입력한 데이터
    val selectedCode: String = "",                                                  // 선택한 공통코드의 코드
)