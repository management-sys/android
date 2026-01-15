package com.example.attendancemanagementapp.ui.asset.card.detail

sealed interface CardDetailEvent {
    // 삭제 버튼 클릭 이벤트
    data object ClickedDelete: CardDetailEvent

    // 수정 버튼 클릭 이벤트
    data object ClickedUpdate: CardDetailEvent
}