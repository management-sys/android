package com.example.attendancemanagementapp.ui.login

enum class LoginField { ID, PASSWORD }

sealed interface LoginEvent {
    // 로그인 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: LoginField,
        val value: String
    ): LoginEvent

    // 로그인 버튼 클릭 이벤트
    data object ClickedLogin: LoginEvent
}