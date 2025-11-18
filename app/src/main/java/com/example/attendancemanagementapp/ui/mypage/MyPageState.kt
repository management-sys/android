package com.example.attendancemanagementapp.ui.mypage

import com.example.attendancemanagementapp.data.dto.EmployeeDTO

data class MyPageState(
    val myInfo: EmployeeDTO.GetMyInfoResponse = EmployeeDTO.GetMyInfoResponse(),    // 내 정보
    val curPassword: String = "",   // 현재 비밀번호
    val newPassword: String = ""    // 새로운 비밀번호
)