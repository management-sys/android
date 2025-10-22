package com.example.attendancemanagementapp.ui.commoncode.detail

sealed interface CodeDetailEvent {
    // 공통코드 삭제 버튼 클릭 이벤트
    data object ClickedDelete: CodeDetailEvent
}