package com.example.attendancemanagementapp.ui.commoncode.edit

import com.example.attendancemanagementapp.ui.commoncode.FieldState

data class CodeEditUiState(
    val codeName: FieldState = FieldState(),    // 코드명
    val codeValue: FieldState = FieldState(),   // 코드값
    val description: FieldState = FieldState()  // 설명
)
