package com.example.attendancemanagementapp.ui.mypage

enum class MyPageField { NAME, PHONE, BIRTHDATE, CUR_PASSWORD, NEW_PASSWORD }

sealed interface MyPageEvent {
    // 초기화
    data object Init: MyPageEvent

    // 수정 버튼 클릭 이벤트
    data object ClickedUpdate: MyPageEvent

    // 마이페이지 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: MyPageField,
        val value: String
    ): MyPageEvent

    // 로그아웃 버튼 클릭 이벤트
    data object ClickedLogout: MyPageEvent
}