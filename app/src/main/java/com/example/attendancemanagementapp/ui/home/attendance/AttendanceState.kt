package com.example.attendancemanagementapp.ui.home.attendance

data class AttendanceState(
    val isWorking: Boolean = false,         // 근무 중 여부
    val currentTime: String = "--:--:--",   // 현재 시간
    val annualLeave: Int = 0                // 남은 연차 수
)